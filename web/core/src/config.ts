import { BaseUiElement } from './model';
import { Middleware } from './api';

export interface UiHandler {
    drawUi(rootElm: BaseUiElement): void;
    createElement(model: any): BaseUiElement;
    handleServerUpdate(): void;
}

export type WebPeerExtension = {
    parameters: any;
    setMiddleware: (middleware?: Middleware[]) => void;
    uiHandler: UiHandler;
    elementTypes: string[];
};

export const webpeerExt = (window as any).webPeer as WebPeerExtension;
