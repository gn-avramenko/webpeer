import { generateUUID, isNull } from './utils';
import { webpeerExt } from './config';
import { BaseUiElement } from './model';

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
    private readonly elements: Map<string, BaseUiElement> = new Map();

    private rootElement?: BaseUiElement;

    getRootElement() {
        return this.rootElement;
    }

    findNode(id: string) {
        return this.elements.get(id);
    }

    private disposeAndDeleteId(node: BaseUiElement) {
        node.dispose();
        this.elements.delete(node.id);
        node.children?.forEach((ch) => this.disposeAndDeleteId(ch));
    }

    removeNode(nodeId: string) {
        const node = this.findNode(nodeId);
        const parent = node?.parent;
        if (!node || !parent) {
            return;
        }
        const idx = parent.children!.indexOf(node);
        parent.children!.splice(idx, 1);
        this.disposeAndDeleteId(node);
        parent.redraw();
    }

    private registerAndInitNode(node: BaseUiElement) {
        node.init();
        this.elements.set(node.id, node);
        node.children?.forEach((ch) => this.registerAndInitNode(ch));
    }

    addNode(node: BaseUiElement, parentId?: string, insertAfterId?: string) {
        if (!parentId) {
            this.rootElement = node;
            this.registerAndInitNode(node);
            return;
        }
        const parent = this.findNode(parentId)!;
        node.parent = parent;
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

    setMiddleware(middleware?: Middleware[]) {
        this.middleware = middleware;
        this.middleware?.sort((a, b) => a.priority - b.priority);
    }

    constructor(protected configuration: Configuration) {
        setInterval(() => this.ping(), 60 * 1000);
    }

    destroy() {
        navigator.sendBeacon(
            `${this.configuration.restPath}?action=destroy`,
            JSON.stringify({
                clientId: this.configuration.clientId,
            })
        );
    }

    async sendCommandAsync(
        elementId: string,
        commandId: string,
        commandData?: any,
        deferred?: boolean,
        initOverrides?: RequestInit | InitOverrideFunction
    ) {
        const cmd =
            commandId === 'init'
                ? {
                      cmd: 'init',
                      data: commandData,
                  }
                : {
                      cmd: commandId,
                      id: elementId,
                      data: commandData,
                  };
        if (deferred) {
            if (commandId === 'pc') {
                const existingCommand = this.deferredCommands.find(
                    (it) =>
                        it.id === elementId &&
                        it.cmd === 'pc' &&
                        it.data.pn == cmd.data?.pn
                );
                if (existingCommand) {
                    existingCommand.data.pv = cmd.data.pv;
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

    private async processCommands(commands: any[], item?: QueueItem, response?: any) {
        if (commands.find((it) => it.cmd === 'resync')) {
            window.localStorage.setItem(
                'webpeer-state',
                JSON.stringify(this.uiElementsRegistry.getRootElement()!.getState())
            );
            webpeerExt.uiHandler.handleServerUpdate();
            return;
        }
        for (const cmd of commands) {
            if (cmd.cmd === 'show-error') {
                const model = cmd.data;
                webpeerExt.uiHandler.handleRemotingError(model.message, model.details);
                continue;
            }
            const node = this.uiElementsRegistry.findNode(cmd.id)!;
            if (cmd.cmd === 'init') {
                const model = cmd.data;
                await this.doInit(model, item, response);
                continue;
            } else if (cmd.cmd === 'ac') {
                const model = cmd.data;
                await this.doLoadAdditionalModules(model);
                const childNode = webpeerExt.uiHandler.createElement(model);
                this.uiElementsRegistry.addNode(
                    childNode,
                    cmd.id,
                    cmd.insertAfterId
                );
                continue;
            }
            if (cmd.cmd === 'rc') {
                this.uiElementsRegistry.removeNode(cmd.id);
                continue;
            }
            node.processCommandFromServer(cmd.cmd, cmd.data);
        }
    }
    private async loadCss(url: string) {
        return new Promise((resolve, reject) => {
            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = url;

            link.onload = function () {
                resolve(null);
            };

            link.onerror = function () {
                reject();
            };

            document.head.appendChild(link);
        });
    }

    private async loadScript(url: string) {
        return new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = url;
            script.type = 'text/javascript';

            script.onload = function () {
                resolve(null);
            };

            script.onerror = function () {
                reject();
            };

            document.head.appendChild(script);
        });
    }

    private async doLoadAdditionalModules(model: any) {
        const newTypes = [] as string[];
        this.collectNewTypes(newTypes, model);
        if (!newTypes.length) {
            return;
        }
        const newType = newTypes[0];
        const req = {
            request: [
                {
                    cmd: 'get-module-for-type',
                    elementType: newType,
                },
            ],
            context: new Map(),
        } as Context;
        const res = await this.requestWithMiddleware(
            req,
            0,
            'action=get-module-for-type'
        );
        const command = res.response.response;
        const scripts = command.scripts ?? ([] as string[]);
        for (const resource of scripts) {
            await this.loadScript(
                `${this.configuration.restPath}/_resources/${resource}`
            );
        }
        const css = command.css ?? ([] as string[]);
        for (const resource of css) {
            await this.loadCss(`${this.configuration.restPath}/_resources/${resource}`);
        }
        await this.doLoadAdditionalModules(model);
    }

    private ping() {
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
        fetch(`${this.configuration.restPath}?action=ping`, httpRequestInit);
    }

    async makeRequest(
        elementId: string,
        commandId: string,
        commandData?: any,
        initOverrides?: RequestInit | InitOverrideFunction
    ) {
        const req = {
            request: {
                id: elementId,
                cmd: commandId,
                data: commandData,
            },
            context: new Map(),
        } as Context;
        const res = await this.requestWithMiddleware(
            req,
            0,
            'action=request',
            initOverrides
        );
        return res.response.result;
    }

    private collectNewTypes(newTypes: string[], model: any) {
        const type = model.type as any;
        webpeerExt.elementTypes = webpeerExt.elementTypes || [];
        if (
            webpeerExt.elementTypes.indexOf(type) === -1 &&
            newTypes.indexOf(type) === -1
        ) {
            newTypes.push(type);
        }
        (model.children || []).forEach((ch: any) => this.collectNewTypes(newTypes, ch));
    }

    private async doInit(model: any, queItem?: QueueItem, response?: any) {
        await this.doLoadAdditionalModules(model);
        if (queItem && queItem.resolve) {
            queItem.resolve(response);
        }
        const rootElm = webpeerExt.uiHandler.createElement(model);
        this.uiElementsRegistry.addNode(rootElm);
        window.localStorage.removeItem('webpeer-state');
        webpeerExt.uiHandler.drawUi(rootElm);
    }

    private async processQueue() {
        if (this.processingQueue) {
            return;
        }
        this.processingQueue = true;
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
                    undefined,
                    item.initOverrides
                );
                const commands = (res.response?.commands || []) as any[];
                await this.processCommands(commands, item, res.response);
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
        this.processingQueue = false;
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
                this.socket = new WebSocket(
                    `${this.configuration.webSocketUrl}?clientId=${this.configuration.clientId}&path=${this.configuration.restPath}`
                );
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
                    const command = JSON.parse(content);
                    this.processCommands([command]);
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
        queryString?: string,
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
            const requestInit = {
                ...initParams,
                ...overrides,
            } as any;
            let result: Response;
            try {
                result = await fetch(
                    `${this.configuration.restPath}${queryString ? `?${queryString}` : ''}`,
                    requestInit
                );
            } catch (e) {
                throw new FetchError(e as Error, 'Response returned an error code');
            }
            if (result.redirected) {
                window.location.href = result.url;
                return context;
            }
            context.rawResponse = result;
            context.response = await result.json();
            if (result.headers.has('x-version')) {
                this.serverVersion = result.headers.get('x-version') as string;
            }
            return context;
        }
        return await this.middleware[idx].advice(context, (request) =>
            this.requestWithMiddleware(request, idx + 1, queryString, initOverrides)
        );
    }
}

export const api = new API({
    clientId: generateUUID(),
    restPath: webpeerExt.parameters.restPath,
    webSocketUrl: webpeerExt.parameters.webSocketUrl,
});

webpeerExt.setMiddleware = (middleware) => api.setMiddleware(middleware);
