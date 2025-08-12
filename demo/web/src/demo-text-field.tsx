import * as React from 'react';
import { useEffect, useState } from 'react';
import { BaseDemoUiElement, DemoUiElementFactory } from './common';

interface DemoTextFieldComponentInternal {
    setValueSetter: (setter: (value: string | null) => void) => void;
    setReadonlySetter: (setter: (value: boolean) => void) => void;
    valueSetCallback: (value: string | null) => void;
    onAfterInitialized: () => void;
    id: string;
}

function TextFieldComponent(props: {
    component: DemoTextFieldComponentInternal;
}): React.ReactNode {
    const [value, setValue] = useState<string | null>(null);
    const [readonly, setReadonly] = useState<boolean>(false);
    props.component.setValueSetter(setValue);
    props.component.setReadonlySetter(setReadonly);
    useEffect(() => {
        props.component.onAfterInitialized();
    }, [props.component]);
    return (
        <input
            type="text"
            className="form-control"
            key={props.component.id}
            disabled={readonly}
            value={value ?? ''}
            onChange={(e) => {
                setValue(e.target.value);
                props.component.valueSetCallback(e.target.value);
            }}
        />
    );
}

export class DemoTextField
    extends BaseDemoUiElement
    implements DemoTextFieldComponentInternal
{
    serialize() {
        return { value: this.value, ...super.serialize() };
    }

    createReactElement(): React.ReactElement {
        return React.createElement(TextFieldComponent, {
            component: this,
            key: this.id,
        });
    }

    constructor(model: any) {
        super(model);
        this.deferred = model.deferred ?? false;
        this.value = model.value;
    }

    private readonlySetter: ((value: boolean) => void) | null = null;

    private value: string | null = null;

    private readonly: boolean = false;

    private valueSetter: ((value: string | null) => void) | null = null;

    private deferred: boolean = false;

    setReadonlySetter(setter: (value: boolean) => void): void {
        this.readonlySetter = setter;
    }

    valueSetCallback(value: string | null): void {
        this.value = value;
        super.sendPropertyChange('value', value, this.deferred);
    }

    setValueSetter(setter: (value: string | null) => void): void {
        this.valueSetter = setter;
    }

    onAfterInitialized(): void {
        if (this.valueSetter) {
            this.valueSetter(this.value);
        }
        if (this.readonlySetter) {
            this.readonlySetter(this.readonly);
        }
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if (propertyName === 'value') {
            this.value = propertyValue;
            this.valueSetter!(propertyValue);
            return;
        }
        super.updatePropertyValue(propertyName, propertyValue);
    }
}
export class DemoTextFieldElementFactory implements DemoUiElementFactory {
    createElement(node: any): BaseDemoUiElement {
        return new DemoTextField(node);
    }
}
