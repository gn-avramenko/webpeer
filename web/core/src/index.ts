import { api } from './api';

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
export { api };

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
            state: JSON.parse(window.localStorage.getItem('webpeer-state') || '{}'),
        },
        false
    );
}
window.addEventListener('unload', function () {
    api.destroy();
});
document.addEventListener('DOMContentLoaded', init);
