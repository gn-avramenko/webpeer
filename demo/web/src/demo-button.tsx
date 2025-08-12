import * as React from 'react';
import { useEffect } from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common';

interface DemoButtonComponentInternal {
    onClick: () => void;
    title: string;
    id: string;
    onAfterInitialized: () => void;
}

function DemoButtonComponent(props: {
    component: DemoButtonComponentInternal;
}): React.ReactNode {
    useEffect(() => {
        props.component.onAfterInitialized();
    }, [props.component]);
    return (
        <button
            type="button"
            className="btn btn-primary"
            key={props.component.id}
            onClick={() => props.component.onClick()}
        >
            {props.component.title}
        </button>
    );
}

export class DemoButton
    extends BaseDemoUiElement
    implements DemoButtonComponentInternal
{
    serialize = () => ({
        id: this.id,
    });

    createReactElement(): React.ReactElement {
        return React.createElement(DemoButtonComponent, {
            component: this,
            key: this.id,
        });
    }

    title: string;

    constructor(model: any) {
        super(model);
        this.title = model.title;
    }

    onClick = () => {
        super.sendCommandAsync('click');
    };

    onAfterInitialized() {}
}
export class DemoButtonElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoButton(node);
    }
}
