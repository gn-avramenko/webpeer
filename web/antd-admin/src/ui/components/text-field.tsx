import * as React from "react";
import {useEffect, useState} from "react";
import Input from "antd/lib/input/Input";
import {AntdUiElementFactory, BaseAntdUiElement} from "@/ui/components/common.tsx";

interface TextFieldComponentInternal {
    setValueSetter: (setter: (value: string | null) => void) => void;
    setReadonlySetter: (setter: (value: boolean) => void) => void;
    valueSetCallback: (value: string | null) => void
    onAfterInitialized: () => void
}


// eslint-disable-next-line @typescript-eslint/no-unused-vars
function TextFieldComponent(props: { component: TextFieldComponentInternal }): React.ReactNode {
    const [value, setValue] = useState<string | null>(null)
    const [readonly, setReadonly] = useState<boolean>(false)
    props.component.setValueSetter(setValue as any)
    props.component.setReadonlySetter(setReadonly)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, [props.component])
    return (<Input autoFocus disabled={readonly} value={value ?? ""} allowClear onChange={(e) => {
        setValue(e.target.value)
        props.component.valueSetCallback(e.target.value)
    }
    }/>)
}

export class AntdTextField extends BaseAntdUiElement implements TextFieldComponentInternal {
    serialize = ()=>{
        return {
            id: this.id,
            value: this.value
        }
    }
    createReactElement(): React.ReactElement {
        return React.createElement(TextFieldComponent, {component:this})
    }
    constructor(model:any) {
        super(model);
        this.id = model.id
    }

    private readonlySetter: ((value:boolean) => void) | null = null

    private value: string | null = null

    private readonly : boolean = false

    private valueSetter: ((value: string|null) => void) | null = null

    private valueChanged: (value: string | null) => void = (value: string | null) => {
        this.value = value
    };

    setReadonlySetter(setter: (value: boolean) => void): void {
        this.readonlySetter = setter
    }

    valueSetCallback(value: string | null): void {
        this.valueChanged(value)
    }

    setValueSetter(setter: (value: string|null) => void): void {
        this.valueSetter = setter
    }
    onAfterInitialized(): void {
        if(this.valueSetter){
            this.valueSetter(this.value)
        }
        if(this.readonlySetter){
            this.readonlySetter(this.readonly)
        }
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if(propertyName == "value"){
            this.value = propertyValue
            this.setValueSetter!(propertyValue)
            return;
        }
        super.updatePropertyValue(propertyName, propertyValue)
    }

}
export class AntdTextFieldElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdTextField(node)
    }
}