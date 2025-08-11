import { generateUUID, isNull } from './utils';
import { webpeerExt } from './config';
import { UiElement } from './model';

export type Context = {
    request?: any;
    rawResponse?: Response;
    response?: any;
    context?: Map<string, any | null>;
};

type QueueItem = {
    payload: any;
    initOverrides?: RequestInit | InitOverrideFunction;
    resolve?: (result: any | undefined) => void;
    reject?: (error: any | undefined) => void;
};

export type HTTPHeaders = { [key: string]: string };
export type HTTPMethod =
    | 'GET'
    | 'POST'
    | 'PUT'
    | 'PATCH'
    | 'DELETE'
    | 'OPTIONS'
    | 'HEAD';

export class FetchError extends Error {
    override name: 'FetchError' = 'FetchError';
    cause: Error;

    constructor(
        public aCause: Error,
        msg?: string
    ) {
        // noinspection TypeScriptValidateTypes
        super(msg);
        this.cause = aCause;
    }
}

export type Middleware = {
    advice: (
        request: Context,
        callback: (request: Context) => Promise<Context>
    ) => Promise<Context>;
    priority: number;
};

export class Configuration {
    clientId: string = '';
    restPath: string = ''; // override base path
    webSocketUrl?: string;
    middleware?: Middleware[]; // middleware to apply before/after fetch requests
    headers?: HTTPHeaders; //header params we want to use on every request
}

export type Json = any;
export type HTTPBody = Json | FormData | URLSearchParams;
export type HTTPRequestInit = {
    headers?: HTTPHeaders;
    method: HTTPMethod;
    body?: HTTPBody;
};

export type InitOverrideFunction = (
    requestContext: HTTPRequestInit
) => Promise<RequestInit>;

class UiElementsRegistry {
    private readonly elements: Map<string, UiElement> = new Map();

    private rootElement?: UiElement;

    getRootElement() {
        return this.rootElement;
    }

    findNode(id: string) {
        return this.elements.get(id);
    }

    private disposeAndDeleteId(node: UiElement) {
        node.dispose();
        this.elements.delete(node.id);
        node.children?.forEach((ch) => this.disposeAndDeleteId(ch));
    }

    removeNode(nodeId: string) {
        const node = this.findNode(nodeId);
        if (!node || !node.parent) {
            return;
        }
        const idx = node.parent.children!.indexOf(node);
        node.parent.children!.splice(idx, 1);
        this.disposeAndDeleteId(node);
    }

    private registerAndInitNode(node: UiElement) {
        node.init();
        this.elements.set(node.id, node);
        node.children?.forEach((ch) => this.registerAndInitNode(ch));
    }

    addNode(node: UiElement, parentId?: string, insertAfterId?: string) {
        if (!parentId) {
            this.rootElement = node;
            this.registerAndInitNode(node);
            return;
        }
        const parent = this.findNode(parentId)!;
        parent.children = parent.children || [];
        if (!insertAfterId) {
            parent.children = [node, ...parent.children];
        } else {
            const idx = parent.children.findIndex((it) => it.id === insertAfterId)!;
            const arr1 = [...parent.children];
            const arr2 = [...parent.children];
            parent.children = [...arr1.splice(idx), node, ...arr2.splice(0, idx)];
        }
        this.registerAndInitNode(node);
        parent.redraw();
    }
}

export class API {
    private serverVersion = '-1';

    private middleware?: Middleware[];

    private activeSubscriptionsIds: string[] = [];

    private socket: WebSocket | null = null;

    private awaitingWS: {
        resolve: (value: unknown) => void;
        reject: (reason?: any) => void;
    }[] = [];

    private connecting = false;

    private processingQueue = false;

    private queue: QueueItem[] = [];

    private deferredCommands: any[] = [];

    private uiElementsRegistry = new UiElementsRegistry();

    private sortMiddleware() {
        this.middleware?.sort((a, b) => a.priority - b.priority);
    }

    constructor(protected configuration: Configuration) {
        this.middleware = configuration.middleware;
        this.sortMiddleware();
    }

    async makeRequest(
        elementId: string,
        commandId: string,
        commandData?: any,
        initOverrides?: RequestInit | InitOverrideFunction
    ) {
        let context = {
            request: {
                elementId,
                commandId,
                data: commandData,
            },
            context: new Map(),
        } as Context;
        context = await this.requestWithMiddleware(context, 0, initOverrides);
        return context.response;
    }

    async sendCommandAsync(
        elementId: string,
        commandId: string,
        commandData?: any,
        deferred?: boolean,
        initOverrides?: RequestInit | InitOverrideFunction
    ) {
        const cmd = {
            cmd: 'ec',
            id: elementId,
            data: {
                cmd: commandId,
                data: commandData,
            },
        };
        if (deferred) {
            if (commandId === 'pc') {
                const existingCommand = this.deferredCommands.find(
                    (it) =>
                        it.cmd === 'ec' &&
                        it.data.commandId === 'pc' &&
                        it.data.data.pn == cmd.data.data.pn
                );
                if (existingCommand) {
                    existingCommand.data.data.pv = cmd.data.data.pv;
                    return;
                }
            }
            this.deferredCommands.push(cmd);
            return;
        }
        const payload = [...this.deferredCommands];
        this.deferredCommands.splice(0);
        payload.push(cmd);
        const prom = new Promise((resolve, reject) => {
            this.queue.push({
                resolve,
                reject,
                initOverrides,
                payload,
            });
        });
        await this.processQueue();
        return await prom;
    }

    private processCommands = (commands: any[]) => {
        if (commands.find((it) => it.cmd === 'resync')) {
            const queueItem = {
                payload: {
                    command: 'resync',
                    model: this.uiElementsRegistry.getRootElement()!.serialize(),
                },
                reject: () => {
                    this.queue.forEach((it) => {
                        if (it !== queueItem && it.reject) {
                            it.reject('Unable to connect to server');
                        }
                    });
                    this.queue = [];
                },
            };
            this.queue = [queueItem, ...this.queue];
            return;
        }
        commands.forEach((cmd: any) => {
            if (cmd.cmd === 'ec') {
                const node = this.uiElementsRegistry.findNode(cmd.id)!;
                node.processCommandFromServer(cmd.data);
            }
            if (cmd.cmd === 'rc') {
                this.uiElementsRegistry.removeNode(cmd.id);
            }
            if (cmd.cmd === 'ac') {
                const data = cmd.data;
                const node = webpeerExt.uiHandler.createElement(data);
                this.uiElementsRegistry.addNode(node, cmd.id, cmd.insertAfterId);
            }
            if (cmd.cmd === 'uls') {
                const paramName = cmd.data.pn;
                const paramValue = cmd.data.pv;
                const data = JSON.parse(window.localStorage.getItem('webpeer') || '{}');
                if (paramValue !== undefined) {
                    data[paramName] = paramValue;
                } else {
                    delete data[paramName];
                }
                window.localStorage.setItem('webpeer', JSON.stringify(data));
            }
            if (cmd.cmd === 'reload') {
                window.location.reload();
            }
        });
    };

    private async processQueue() {
        if (this.processingQueue) {
            return;
        }
        const items = [...this.queue];
        for (const item of items) {
            if (!this.queue.length) {
                return;
            }
            try {
                const req = {
                    request: item.payload,
                    context: new Map(),
                } as Context;
                const res = await this.requestWithMiddleware(
                    req,
                    0,
                    item.initOverrides
                );
                const commands = (res.response || []) as any[];
                if (commands.find((it) => it.cmd === 'init')) {
                    if (item.resolve) {
                        item.resolve(res.response);
                    }
                    webpeerExt.uiHandler.drawUi(
                        commands.find((it) => it.cmd === 'init').data
                    );
                    break;
                }
                this.processCommands(commands);
                if (item.resolve) {
                    item.resolve(res.response);
                }
            } catch (e) {
                console.log(e);
                if (item.reject) {
                    item.reject(e);
                }
            } finally {
                const idx = this.queue.indexOf(item);
                if (idx !== -1) {
                    this.queue.splice(idx, 1);
                }
            }
        }
        if (this.queue.length) {
            this.processingQueue = false;
            await this.processQueue();
        }
    }

    async openWebSocket(initiatorId: string) {
        if (!this.configuration.webSocketUrl) {
            throw new Error('web socket url is not defined');
        }
        if (this.connecting) {
            await new Promise((resolve, reject) => {
                if (!this.connecting) {
                    if (this.socket) {
                        resolve(null);
                        return;
                    }
                    reject(new Error('unable to connect'));
                    return;
                }
                this.awaitingWS.push({ resolve, reject });
            });
        }
        if (!this.socket) {
            await new Promise<void>((resolve, reject) => {
                this.socket = new WebSocket(this.configuration.webSocketUrl!!);
                this.socket.onopen = () => {
                    resolve();
                    this.awaitingWS.forEach((it) => it.resolve(null));
                };
                this.socket.onclose = (event) => {
                    this.awaitingWS.forEach((it) => it.reject(event.reason));
                    this.awaitingWS.splice(0);
                    this.socket = null;
                };
                this.socket.onmessage = (event) => {
                    const content = event.data as string;
                    const commands = JSON.parse(content);
                    this.processCommands(commands);
                };
                this.socket.onerror = (error) => {
                    if (this.connecting) {
                        reject(error);
                    }
                    this.awaitingWS.forEach((it) => it.reject('Socket error'));
                    this.awaitingWS.splice(0);
                };
            });
        }
        this.activeSubscriptionsIds.push(initiatorId);
    }

    async closeWebSocket(initiatorId: string) {
        this.activeSubscriptionsIds = this.activeSubscriptionsIds.filter(
            (it) => it !== initiatorId && this.uiElementsRegistry.findNode(it)
        );
        if (this.activeSubscriptionsIds.length === 0) {
            this.socket?.close(0, 'No active subscriptions');
        }
    }

    private async requestWithMiddleware(
        context: Context,
        idx: number,
        initOverrides?: RequestInit | InitOverrideFunction
    ): Promise<Context> {
        if (isNull(context.context)) {
            context.context = new Map<string, any>();
        }
        if (
            !this.middleware ||
            this.middleware.length === 0 ||
            idx === this.middleware.length
        ) {
            const headers = Object.assign(
                {
                    'x-version': this.serverVersion,
                    'x-client-id': this.configuration.clientId,
                },
                this.configuration.headers || {}
            );
            const httpRequestInit = {
                method: 'POST',
                headers,
            } as HTTPRequestInit;
            httpRequestInit.body = JSON.stringify(context.request);
            const initParams = {
                ...httpRequestInit,
            } as RequestInit;
            let overrides = {};
            if (typeof initOverrides === 'function') {
                overrides = await initOverrides(httpRequestInit);
            } else if (initOverrides) {
                overrides = initOverrides;
            }
            const requestInit = { ...initParams, ...overrides };
            let result: Response;
            try {
                result = await fetch(this.configuration.restPath, requestInit);
            } catch (e) {
                throw new FetchError(e as Error, 'Response returned an error code');
            }
            context.rawResponse = result;
            context.response = await result.json();
            this.serverVersion = result.headers.get('x-version') ?? '-1';
            return context;
        }
        return await this.middleware[idx].advice(context, (request) =>
            this.requestWithMiddleware(request, idx + 1, initOverrides)
        );
    }
}

export const api = new API({
    clientId: generateUUID(),
    restPath: webpeerExt.parameters.restPath,
    webSocketUrl: webpeerExt.parameters.webSocketUrl,
    middleware: webpeerExt.middleware,
});
