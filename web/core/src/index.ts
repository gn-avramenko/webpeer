import {UiModel} from "./ui/model.ts";
import {API, Middleware, SubscriptionHandler} from "./remoting/api.ts";
import {generateUUID} from "./utils/utils.ts";

export interface UiHandler{
    drawUi(model:UiModel):void
}
export type WebPeerExtension = {
    parameters: any
    middleware?: Middleware[]
    uiHandler: UiHandler
    subscriptionHandler?:SubscriptionHandler
}
export const webpeerExt = (window as any).webPeer as WebPeerExtension

export const api = new API({
    clientId: generateUUID(),
    model: new UiModel(),
    restPath: webpeerExt.parameters.restPath,
    webSocketUrl: webpeerExt.parameters.webSocketUrl,
    middleware: webpeerExt.middleware,
    subscriptionHandler: webpeerExt.subscriptionHandler
})

async function init(){
    const initResp = await api.request({
        command: "init"
    })
    const uiModel = new UiModel();
    uiModel.deserialize(initResp.payload)
    webpeerExt.uiHandler.drawUi(uiModel)
}

document.addEventListener("DOMContentLoaded", init);