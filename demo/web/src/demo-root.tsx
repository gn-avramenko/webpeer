import React from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common.tsx';

class DemoRootElement extends BaseDemoUiElement {
    constructor(model: any) {
        super([], [], model);
    }
    render(): React.ReactElement {
        return (
            <div
                style={{
                    width: '600px',
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                }}
            >
                <div key={this.id} style={{ flexGrow: 0 }}>
                    <header className="d-flex justify-content-between align-items-md-center pb-3 mb-5 border-bottom">
                        <h1 className="h4">Demo Web Peer Application</h1>
                    </header>
                </div>
                <div className="input-group mb-3" style={{ flexGrow: 0 }}>
                    <div className="input-group">
                        <span className="input-group-text" id="basic-addon3">
                            User
                        </span>
                        {this.findByTag('user').createReactElement()}
                    </div>
                </div>
                {this.findByTag('messages')!.createReactElement()}
                <div className="input-group mb-3" style={{ flexGrow: 0 }}>
                    <div className="input-group">
                        {this.findByTag('message').createReactElement()}
                        {this.findByTag('send').createReactElement()}
                    </div>
                </div>
            </div>
        );
    }
    init() {
        this.openWebSocket();
    }

    dispose() {
        this.closeWebSocket();
    }
}

export class DemoRootElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoRootElement(node);
    }
}
