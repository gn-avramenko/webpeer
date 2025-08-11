import { api } from './api';

export type { UiElement } from './model';
export { BaseUiElement } from './model';

export type {
    Middleware,
    HTTPBody,
    HTTPHeaders,
    HTTPMethod,
    HTTPRequestInit,
    InitOverrideFunction,
} from './api';

export type { UiHandler, WebPeerExtension } from './config';
export { webpeerExt } from './config';

export type { PreloaderHandler, PreloaderParams } from './preloader';
export { PreloaderMiddleware } from './preloader';
export { isNull, generateUUID } from './utils';

async function init() {
    await api.sendCommandAsync(
        'root',
        'init',
        {
            ls: JSON.parse(window.localStorage.getItem('webpeer') || '{}'),
            params: {
                windowWidth: window.innerWidth,
                windowHeight: window.innerHeight,
                ...((window as any).webPeer?.parameters || {}),
            },
        },
        false
    );
}

document.addEventListener('DOMContentLoaded', init);
