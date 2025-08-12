import { BaseUiElement, webpeerExt, WebPeerExtension } from 'webpeer-core';
import React from 'react';
import { createRoot, Root } from 'react-dom/client';

export interface DemoUiElementFactory {
    createElement(model: any): BaseDemoUiElement;
}

export type DemoWebPeerExtension = WebPeerExtension & {
    elementHandlersFactories: Map<string, DemoUiElementFactory>;
};
export const demoWebPeerExt = webpeerExt as DemoWebPeerExtension;
demoWebPeerExt.elementHandlersFactories = new Map();

export abstract class BaseDemoUiElement extends BaseUiElement {
    private marker = 0;

    abstract createReactElement(): React.ReactElement;

    protected markerSetter?: (marker: number) => void;

    setMarkerSetter = (setter: (marker: number) => void) => {
        this.markerSetter = setter;
    };

    redraw() {
        this.marker += 1;
        this.markerSetter!(this.marker);
    }

    constructor(model: any) {
        super(model);
        (model.children || []).forEach((ch: any) => {
            const elm = demoWebPeerExt.elementHandlersFactories
                .get(ch.type)!
                .createElement(ch);
            elm.parent = this;
            elm.init();
            this.children = this.children || [];
            this.children.push(elm);
        });
    }

    findByTag(tag: string) {
        return this.children?.find((it) => it.tag === tag) as
            | BaseDemoUiElement
            | undefined;
    }
}

let root: Root | null = null;

demoWebPeerExt.uiHandler = {
    drawUi(rootElm: BaseDemoUiElement) {
        if (!root) {
            root = createRoot(document.getElementById('root') as Element);
        }
        root.render(rootElm.createReactElement());
    },
    createElement(model: any): BaseUiElement {
        return demoWebPeerExt.elementHandlersFactories
            .get(model.type)!
            .createElement(model);
    },
};
