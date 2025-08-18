import * as React from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common.tsx';

type DemoMessage = {
    user: string;
    message: string;
};

export class DemoMessagesComponent extends BaseDemoUiElement {
    constructor(model: any) {
        super([], ['messages'], model);
    }
    render(): React.ReactElement {
        return (
            <div className="mb-3" style={{ flexGrow: 1 }}>
                <label htmlFor="messagesArea" className="form-label">
                    Messages
                </label>
                <textarea
                    className="form-control"
                    disabled
                    value={(this.state.get('messages') || [])
                        .reduce(
                            (pv: string, cv: DemoMessage) =>
                                `${pv}\r\n${cv.user}: ${cv.message}`,
                            ''
                        )
                        .trim()}
                    id="messagesArea"
                    style={{ height: '100%', width: '100%' }}
                />
            </div>
        );
    }
    processCommandFromServer(commandId: string, data?: any) {
        if (commandId === 'refresh-messages') {
            this.sendCommand('refresh-messages', null);
            return;
        }
        super.processCommandFromServer(commandId, data);
    }
}

export class DemoMessagesElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoMessagesComponent(node);
    }
}
