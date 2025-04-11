import {API, Middleware} from "./remoting/api.ts";
import {generateUUID} from "./utils/utils.ts";
import {BaseUiElement, UiModel} from "./model/model.ts";

export interface UiHandler{
    drawUi(model:any):void
    createElement(model:any): BaseUiElement
}
export type WebPeerExtension = {
    parameters: any
    middleware?: Middleware[]
    uiHandler: UiHandler
}
export const webpeerExt = (window as any).webPeer as WebPeerExtension

export const uiModel = new UiModel()

export const api = new API({
    clientId: generateUUID(),
    restPath: webpeerExt.parameters.restPath,
    webSocketUrl: webpeerExt.parameters.webSocketUrl,
    middleware: webpeerExt.middleware,
})

async function init(){
    await api.sendCommand({cmd: 'init', data: {
            ls: JSON.parse(window.localStorage.getItem("webpeer") || "{}"),
            params: (window as any).webPeer
        }})
}

document.addEventListener("DOMContentLoaded", init);