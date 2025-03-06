import {API, Middleware, SubscriptionHandler} from "./remoting/api.ts";
import {generateUUID} from "./utils/utils.ts";
import {UiModel} from "./model/model.ts";

export interface UiHandler{
    drawUi(model:any):void
}
export type WebPeerExtension = {
    parameters: any
    middleware?: Middleware[]
    uiHandler: UiHandler
    subscriptionHandler?:SubscriptionHandler
}
export const webpeerExt = (window as any).webPeer as WebPeerExtension

export const uiModel = new UiModel()

export const api = new API({
    clientId: generateUUID(),
    restPath: webpeerExt.parameters.restPath,
    webSocketUrl: webpeerExt.parameters.webSocketUrl,
    middleware: webpeerExt.middleware,
    subscriptionHandler: webpeerExt.subscriptionHandler
})

async function init(){
    await api.sendCommand({cmd: 'init'})
}

document.addEventListener("DOMContentLoaded", init);