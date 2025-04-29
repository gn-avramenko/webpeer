import {API, Middleware} from "./api";
import {generateUUID} from "./utils";
import {BaseUiElement, UiModel} from "./model";

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

export * from './model';
export  * from './utils'
export  * from './preloader'
export  * from './api'

async function init(){
    await api.sendCommand({cmd: 'init', data: {
            ls: JSON.parse(window.localStorage.getItem("webpeer") || "{}"),
            params: (window as any).webPeer
        }})
}

document.addEventListener("DOMContentLoaded", init);