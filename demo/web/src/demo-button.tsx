import * as React from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common.tsx';

export class DemoButton extends BaseDemoUiElement {
    constructor(model: any) {
        super(['title'], [], model);
    }

    render(): React.ReactElement {
        return (
            <button
                type="button"
                className="btn btn-primary"
                key={this.id}
                onClick={() => this.sendCommand('click', null)}
            >
                {this.initParams.get('title')}
            </button>
        );
    }
}

export class DemoButtonElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoButton(node);
    }
}
