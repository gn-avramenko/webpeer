import * as React from 'react';
import { useEffect, useState } from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common';

type DemoMessage = {
    user: string;
    message: string;
};

interface DemoMessagesComponentInternal {
    setMessagesSetter: (setter: (messages: DemoMessage[]) => void) => void;
    onAfterInitialized: () => void;
    id: string;
}

function DemoMessages(props: {
    component: DemoMessagesComponentInternal;
}): React.ReactNode {
    const [messages, setMessages] = useState<DemoMessage[]>([]);
    props.component.setMessagesSetter(setMessages);
    useEffect(() => {
        props.component.onAfterInitialized();
    }, [props.component]);
    return (
        <div className="mb-3" style={{ flexGrow: 1 }}>
            <label htmlFor="messagesArea" className="form-label">
                Messages
            </label>
            <textarea
                className="form-control"
                disabled
                value={messages
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

export class DemoMessagesComponent
    extends BaseDemoUiElement
    implements DemoMessagesComponentInternal
{
    createReactElement(): React.ReactElement {
        return React.createElement(DemoMessages, { component: this, key: this.id });
    }

    constructor(model: any) {
        super(model);
        this.messages = model.messages;
    }

    serialize(): any {
        return { messages: this.messages, ...super.serialize() };
    }

    private messages: DemoMessage[];

    private messagesSetter?: (messages: DemoMessage[]) => void;

    setMessagesSetter(setter: (messages: DemoMessage[]) => void): void {
        this.messagesSetter = setter;
    }

    onAfterInitialized(): void {
        if (this.messagesSetter) {
            this.messagesSetter(this.messages);
        }
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if (propertyName === 'messages') {
            this.messages = propertyValue;
            this.messagesSetter!(propertyValue);
            return;
        }
        super.updatePropertyValue(propertyName, propertyValue);
    }

    executeCommand(commandId: string, commandData: any) {
        if (commandId === 'refresh-messages') {
            this.sendCommandAsync('refresh-messages');
            return;
        }
        super.executeCommand(commandId, commandData);
    }
}

export class DemoMessagesElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoMessagesComponent(node);
    }
}
