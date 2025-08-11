import { UiElement } from './model';
import { Middleware } from './api';

export interface UiHandler {
    drawUi(model: any): void;
    createElement(model: any): UiElement;
}

export type WebPeerExtension = {
    parameters: any;
    middleware?: Middleware[];
    uiHandler: UiHandler;
    rootElementType: string;
};

export const webpeerExt = (window as any).webPeer as WebPeerExtension;
