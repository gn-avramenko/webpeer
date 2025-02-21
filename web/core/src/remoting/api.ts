import {isBlank, isNull} from "@/utils/utils.ts";

export type RequestContext = {
  request?: any,
  rawResponse?: Response
  response?: any;
  context?: Map<string, any|null>
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
  advice: (request: RequestContext, callback: (request: RequestContext) => Promise<RequestContext>) => Promise<RequestContext>
  priority: number
}

export class Configuration {
  constructor(private configuration: ConfigurationParameters) {}

  get restPath(): string {
    if(isBlank(this.configuration.restPath)){
      throw new Error("base path is not defined");
    }
    return this.configuration.restPath!!;
  }

  get webSocketUrl(): string|undefined {
    return this.configuration.webSocketUrl
  }

  get subscriptionHandler(): SubscriptionHandler|undefined {
    return this.configuration.subscriptionHandler
  }

  get middleware(): Middleware[] {
    return this.configuration.middleware || [];
  }

  get headers(): HTTPHeaders | undefined {
    return this.configuration.headers;
  }
}


export interface ConfigurationParameters {
  restPath?: string; // override base path
  webSocketUrl?: string;
  middleware?: Middleware[]; // middleware to apply before/after fetch requests
  headers?: HTTPHeaders //header params we want to use on every request
  subscriptionHandler?: SubscriptionHandler
}

export interface SubscriptionHandler{
  onMessage(payload: any): Promise<void>
}

export type Json = any;
export type HTTPBody = Json | FormData | URLSearchParams;
export type HTTPRequestInit = { headers?: HTTPHeaders; method: HTTPMethod; body?: HTTPBody };

export type InitOverrideFunction = (requestContext: HTTPRequestInit) => Promise<RequestInit>

export class BaseAPI {

  private middleware: Middleware[];

  private activeSubscriptionsIds: string[] = []

  private socket: WebSocket|null = null;

  private awaiting: {resolve: (value: unknown)=>void, reject: (reason?:any)=>void}[] = []

  private connecting = false

  private sortMiddleware(){
    this.middleware.sort((a,b)=> a.priority-b.priority)
  }
  constructor(protected configuration: Configuration) {
    this.middleware = configuration.middleware
    this.sortMiddleware()
  }

  async request(payload: any, initOverrides?: RequestInit | InitOverrideFunction ){
    var req = {
      request: payload,
      context: new Map()
    } as RequestContext
    return await this.requestWithMiddleware(req, 0, initOverrides)
  }

  async addActiveSubscription(id: string){
    if(!this.configuration.subscriptionHandler){
      throw new Error("subscription handler is not defined")
    }
    if(!this.configuration.webSocketUrl){
      throw new Error("web socket url is not defined")
    }
    if (this.connecting){
      await new Promise((resolve, reject) =>{
        if(!this.connecting){
          if(this.socket){
            resolve(null);
            return;
          }
            reject(new Error("unable to connect"))
            return
        }
        this.awaiting.push({resolve, reject})
      });
    }
    if(!this.socket){
      await new Promise<void>((resolve, reject) => {
        this.socket = new WebSocket(this.configuration.webSocketUrl!!)
        this.socket.onopen = () => {
          resolve()
          this.awaiting.forEach(it => it.resolve(null))
        }
        this.socket.onclose = (event) => {
          this.awaiting.forEach(it => it.reject(event.reason))
          this.awaiting.splice(0)
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
          this.awaiting.forEach(it => it.reject("Socket error"))
          this.awaiting.splice(0)
        }
      })
    }
    this.activeSubscriptionsIds.push(id)
  }

  async removeActiveSubscription(id: string){
    this.activeSubscriptionsIds.splice(this.activeSubscriptionsIds.indexOf(id), 1)
    if(this.activeSubscriptionsIds.length === 0) {
      this.socket?.close(0, "No active subscriptions")
    }
  }

  private async requestWithMiddleware(request:RequestContext, idx: number, initOverrides?: RequestInit | InitOverrideFunction):Promise<RequestContext>{
    if(isNull(request.context)){
      request.context = new Map<string, any>()
    }
    if(this.middleware.length === 0 || idx === this.middleware.length){
      const headers = Object.assign({}, this.configuration.headers||{});
      const httpRequestInit = {
        method: "POST",
        headers,
      } as HTTPRequestInit
      httpRequestInit.body = JSON.stringify(request.request)
      const initParams = {
        ...httpRequestInit
      } as RequestInit;
      let overrides = {}
      if(typeof initOverrides === 'function'){
          overrides = await initOverrides(httpRequestInit)
      } else if (initOverrides){
        overrides = initOverrides
      }
      const requestInit = {...initParams, ...overrides}
      let result: Response
      try {
         result = await fetch(this.configuration.restPath, requestInit)
      } catch (e) {
        throw new FetchError(e as Error, 'Response returned an error code');
      }
      request.rawResponse = result;
      request.response = result.json()
      return request
    }
    return await this.middleware[idx].advice(request, (request) => this.requestWithMiddleware(request, idx+1, initOverrides) )
  }
}



