import React from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common';

type DemoRootElementInternal = {
    id: string;
    findChild: (tag: string) => BaseDemoUiElement;
};

function DemoRoot(props: { component: DemoRootElementInternal }): React.ReactElement {
    return (
        <div
            style={{
                width: '600px',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
            }}
        >
            <div key={props.component.id} style={{ flexGrow: 0 }}>
                <header className="d-flex justify-content-between align-items-md-center pb-3 mb-5 border-bottom">
                    <h1 className="h4">Demo Web Peer Application</h1>
                </header>
            </div>
            <div className="input-group mb-3" style={{ flexGrow: 0 }}>
                <div className="input-group">
                    <span className="input-group-text" id="basic-addon3">
                        User
                    </span>
                    {props.component.findChild('user').createReactElement()}
                </div>
            </div>
            {props.component.findChild('messages').createReactElement()}
            <div className="input-group mb-3" style={{ flexGrow: 0 }}>
                <div className="input-group">
                    {props.component.findChild('message').createReactElement()}
                    {props.component.findChild('send-button').createReactElement()}
                </div>
            </div>
        </div>
    );
}

class DemoRootElement extends BaseDemoUiElement implements DemoRootElementInternal {
    constructor(model: any) {
        super(model);
        this.id = model.id;
    }

    init() {
        this.openWebSocket();
    }

    dispose() {
        this.closeWebSocket();
    }

    findChild = (tag: string) => this.findByTag(tag)!;

    createReactElement(): React.ReactElement {
        return React.createElement(DemoRoot, { component: this, key: this.id });
    }
}

export class DemoRootElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoRootElement(node);
    }
}
