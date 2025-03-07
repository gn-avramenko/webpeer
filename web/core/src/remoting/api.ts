import { isNull} from "../utils/utils.ts";
import {uiModel, webpeerExt} from "../index.ts";

export type Context = {
    request?: any,
    rawResponse?: Response
    response?: any;
    context?: Map<string, any | null>
}

type QueueItem = {
    payload: any;
    initOverrides?: RequestInit | InitOverrideFunction;
    resolve?: (result: any | undefined) => void
    reject?: (error: any | undefined) => void
}

export type HTTPHeaders = { [key: string]: string };
export type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'HEAD';


export class FetchError extends Error {
    override name: "FetchError" = "FetchError";
    cause: Error

    constructor(public aCause: Error, msg?: string) {
        // noinspection TypeScriptValidateTypes
        super(msg);
        this.cause = aCause
    }
}

export type Middleware = {
    advice: (request: Context, callback: (request: Context) => Promise<Context>) => Promise<Context>
    priority: number
}

export class Configuration {
    clientId: string= "";
    restPath: string = ""; // override base path
    webSocketUrl?: string;
    middleware?: Middleware[]; // middleware to apply before/after fetch requests
    headers?: HTTPHeaders //header params we want to use on every request
    subscriptionHandler?: SubscriptionHandler
}


export interface SubscriptionHandler {
    onMessage(payload: any): Promise<void>
}

export type Json = any;
export type HTTPBody = Json | FormData | URLSearchParams;
export type HTTPRequestInit = { headers?: HTTPHeaders; method: HTTPMethod; body?: HTTPBody };

export type InitOverrideFunction = (requestContext: HTTPRequestInit) => Promise<RequestInit>

export class API {

    private serverVersion = "-1";

    private middleware?: Middleware[];

    private activeSubscriptionsIds: string[] = []

    private socket: WebSocket | null = null;

    private awaitingWS: { resolve: (value: unknown) => void, reject: (reason?: any) => void }[] = []

    private connecting = false

    private processingQueue = false;

    private queue: QueueItem[] = [];

    private sortMiddleware() {
        this.middleware?.sort((a, b) => a.priority - b.priority)
    }

    constructor(protected configuration: Configuration) {
        this.middleware = configuration.middleware
        this.sortMiddleware()
    }

    async sendPropertyChanged(elementId:string, propertyName: string, propertyValue: any|null|undefined){
        await this.sendCommand({
            cmd: 'ec',
            id: elementId,
            data: {
                cmd: 'pc',
                data: {
                    pn: propertyName,
                    pv: propertyValue
                }
            }
        })
    }


    async sendCommand(payload: any, initOverrides?: RequestInit | InitOverrideFunction): Promise<any> {
        const prom = new Promise((resolve, reject) => {
            this.queue.push({
                resolve,
                reject,
                initOverrides,
                payload:[payload]
            })
        })
        this.processQueue()
        return await prom
    }

    private async processQueue() {
        if (this.processingQueue) {
            return;
        }
        const items = [...this.queue]
        for (const item of items) {
            if (!this.queue.length) {
                return;
            }
            try {
                const req = {
                    request: item.payload,
                    context: new Map()
                } as Context
                const res = await this.requestWithMiddleware(req, 0, item.initOverrides)
                const commands = (res.response || []) as any[]
                if(commands.find(it => it.cmd === "resync")){
                    const queueItem = {
                        payload: {
                            command: 'resync',
                            model: uiModel.getRootElement()!.serialize()
                        },
                        reject: () => {
                            this.queue.forEach(it => {
                                if (it !== queueItem && it.reject) {
                                    it.reject("Unable to connect to server")
                                }
                            })
                            this.queue = [];
                        }
                    }
                    this.queue = [queueItem, ...this.queue]
                    break;
                }
                if(commands.find(it => it.cmd === "init")){
                    if (item.resolve) {
                        item.resolve(res.response)
                    }
                    webpeerExt.uiHandler.drawUi(commands.find(it => it.cmd === "init").data)
                    break;
                }
                commands.forEach((cmd:any) =>{
                    if(cmd.cmd === 'ec'){
                        const node = uiModel.findNode(cmd.id)!
                        node.executeCommand(cmd.data)
                    }
                    if(cmd.cmd === 'rc'){
                        const node = uiModel.findNode(cmd.id)!
                        uiModel.removeNode(node)
                    }
                    if(cmd.cmd === 'ac'){
                        const parent = uiModel.findNode(cmd.id)!
                        const data = cmd.data
                        const node = webpeerExt.uiHandler.createElement(data)
                        uiModel.addNode(node, parent)
                    }
                    if(cmd.cmd === 'uls'){
                        const paramName = cmd.data.pn
                        const paramValue = cmd.data.pv
                        const data = JSON.parse(window.localStorage.getItem("webpeer") || '{}');
                        if(paramValue !== undefined){
                            data[paramName] = paramValue
                        } else {
                            delete data[paramName]
                        }
                        window.localStorage.setItem("webpeer", JSON.stringify(data))
                    }
                    if(cmd.cmd === 'reload'){
                        window.location.reload()
                    }
                })
                if (item.resolve) {
                    item.resolve(res.response)
                }
            } catch (e) {
                if (item.reject) {
                    item.reject(e)
                }
            } finally {
                const idx = this.queue.indexOf(item)
                if (idx !== -1) {
                    this.queue.splice(idx, 1)
                }
            }
        }
        if (this.queue.length) {
            this.processingQueue = false
            await this.processQueue()
        }
    }

    async addActiveSubscription(id: string) {
        if (!this.configuration.subscriptionHandler) {
            throw new Error("subscription handler is not defined")
        }
        if (!this.configuration.webSocketUrl) {
            throw new Error("web socket url is not defined")
        }
        if (this.connecting) {
            await new Promise((resolve, reject) => {
                if (!this.connecting) {
                    if (this.socket) {
                        resolve(null);
                        return;
                    }
                    reject(new Error("unable to connect"))
                    return
                }
                this.awaitingWS.push({resolve, reject})
            });
        }
        if (!this.socket) {
            await new Promise<void>((resolve, reject) => {
                this.socket = new WebSocket(this.configuration.webSocketUrl!!)
                this.socket.onopen = () => {
                    resolve()
                    this.awaitingWS.forEach(it => it.resolve(null))
                }
                this.socket.onclose = (event) => {
                    this.awaitingWS.forEach(it => it.reject(event.reason))
                    this.awaitingWS.splice(0)
                    this.socket = null
                }
                this.socket.onmessage = (event) => {
                    const content = event.data as string
                    const data = JSON.parse(content)
                    this.configuration.subscriptionHandler!!.onMessage(data)
                }
                this.socket.onerror = (error) => {
                    if (this.connecting) {
                        reject(error)
                    }
                    this.awaitingWS.forEach(it => it.reject("Socket error"))
                    this.awaitingWS.splice(0)
                }
            })
        }
        this.activeSubscriptionsIds.push(id)
    }

    async removeActiveSubscription(id: string) {
        this.activeSubscriptionsIds.splice(this.activeSubscriptionsIds.indexOf(id), 1)
        if (this.activeSubscriptionsIds.length === 0) {
            this.socket?.close(0, "No active subscriptions")
        }
    }

    private async requestWithMiddleware(context: Context, idx: number, initOverrides?: RequestInit | InitOverrideFunction): Promise<Context> {
        if (isNull(context.context)) {
            context.context = new Map<string, any>()
        }
        if (!this.middleware || this.middleware.length === 0 || idx === this.middleware.length) {
            const headers = Object.assign({
                'x-version': this.serverVersion,
                'x-client-id': this.configuration.clientId,
            }, this.configuration.headers || {});
            const httpRequestInit = {
                method: "POST",
                headers,
            } as HTTPRequestInit
            httpRequestInit.body = JSON.stringify(context.request)
            const initParams = {
                ...httpRequestInit
            } as RequestInit;
            let overrides = {}
            if (typeof initOverrides === 'function') {
                overrides = await initOverrides(httpRequestInit)
            } else if (initOverrides) {
                overrides = initOverrides
            }
            const requestInit = {...initParams, ...overrides}
            let result: Response
            try {
                result = await fetch(this.configuration.restPath, requestInit)
            } catch (e) {
                throw new FetchError(e as Error, 'Response returned an error code');
            }
            context.rawResponse = result;
            context.response = await result.json()
            this.serverVersion = result.headers.get('x-version')??"-1"
            return context
        }
        return await this.middleware[idx].advice(context, (request) => this.requestWithMiddleware(request, idx + 1, initOverrides))
    }
}



