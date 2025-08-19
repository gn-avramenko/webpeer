import { BaseUiElement, webpeerExt, WebPeerExtension } from 'webpeer-core';
import React, { ReactElement, useEffect, useState } from 'react';
import { createRoot, Root } from 'react-dom/client';

export interface DemoUiElementFactory {
    createElement(model: any): BaseDemoUiElement;
}

export type DemoWebPeerExtension = WebPeerExtension & {
    elementHandlersFactories: Map<string, DemoUiElementFactory>;
};
export const demoWebPeerExt = webpeerExt as DemoWebPeerExtension;
demoWebPeerExt.elementHandlersFactories = new Map();

function DemoUiComponent(props: { element: BaseDemoUiElement }) {
    for (const prop of props.element.state.keys()) {
        const [value, setValue] = useState(props.element.state.get(prop));
        props.element.state.set(prop, value);
        props.element.stateSetters.set(prop, setValue);
    }
    useEffect(() => {
        props.element.state.forEach((value, key) => {
            props.element.stateSetters.get(key)?.(value);
        });
    }, [props.element]);
    return props.element.render();
}

export abstract class BaseDemoUiElement extends BaseUiElement {
    initParams = new Map<string, any | null | undefined>();

    state = new Map<string, any | null | undefined>();

    abstract render(): ReactElement;

    stateSetters: Map<string, (value?: any) => void> = new Map<
        string,
        (value?: any) => void
    >();

    createReactElement() {
        return React.createElement(DemoUiComponent, {
            element: this,
            key: this.id,
        });
    }

    protected constructor(initParams: string[], stateParams: string[], model: any) {
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
        initParams.forEach((key) => this.initParams.set(key, model[key]));
        stateParams.forEach((key) => this.state.set(key, model[key]));
    }

    protected findByTag(tag: string) {
        return this.children?.find((it) => it.tag === tag) as BaseDemoUiElement;
    }

    getState(): any {
        const result = super.getState();
        this.state.forEach((value, key) => {
            result[key] = value;
        });
        return result;
    }

    redraw() {
        //noops
    }

    processCommandFromServer(commandId: string, data?: any) {
        if (commandId === 'pc') {
            this.updatePropertyValue(data!.pn, data!.pv);
            return;
        }
        super.processCommandFromServer(commandId, data);
    }

    protected updatePropertyValue(pn: string, pv: any) {
        this.stateSetters.get(pn)!(pv);
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
    handleServerUpdate() {
        if (confirm('Server was updated, reload?')) {
            window.location.reload();
        }
    },
};
