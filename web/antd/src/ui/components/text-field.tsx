import * as React from 'react';
import { useEffect, useState } from 'react';
import Input from 'antd/lib/input/Input';
import debounce from 'debounce';
import { AntdUiElementFactory, BaseAntdUiElement } from '@/ui/components/common';

interface TextFieldComponentInternal {
    setValueSetter: (setter: (value: string | null) => void) => void;
    setReadonlySetter: (setter: (value: boolean) => void) => void;
    valueSetCallback: (value: string | null) => void
    onAfterInitialized: () => void
    id: string
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function TextFieldComponent(props: { component: TextFieldComponentInternal }): React.ReactNode {
  const [value, setValue] = useState<string | null>(null);
  const [readonly, setReadonly] = useState<boolean>(false);
  // if(!(setValue as any).forId){
  //     (setValue as any).forId = props.component.id;
  // }
  props.component.setValueSetter(setValue);
  props.component.setReadonlySetter(setReadonly);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (
    <Input
      key={props.component.id}
      autoFocus
      disabled={readonly}
      value={value ?? ''}
      allowClear
      onChange={(e) => {
        setValue(e.target.value);
        props.component.valueSetCallback(e.target.value);
      }}
    />
  );
}

export class AntdTextField extends BaseAntdUiElement implements TextFieldComponentInternal {
    serialize = () => ({
      id: this.id,
      value: this.value,
    })

    createReactElement(): React.ReactElement {
      return React.createElement(TextFieldComponent, { component: this, key: this.id });
    }

    private debounceTime:number = 0;

    constructor(model:any) {
      super(model);
      this.deferred = model.deferred ?? false;
      this.debounceTime = model.debounceTime ?? 0;
      this.value = model.value;
      const valueChanged = (value: string|null) => {
        super.sendPropertyChange('value', value, this.deferred);
      };
      this.reportValueChanged = this.debounceTime > 0 ? debounce(valueChanged, this.debounceTime) : valueChanged;
    }

    private reportValueChanged: (value: string|null) => void;

    private readonlySetter: ((value:boolean) => void) | null = null

    private value: string | null = null

    private readonly : boolean = false

    private valueSetter: ((value: string|null) => void) | null = null

    private deferred:boolean = false;

    setReadonlySetter(setter: (value: boolean) => void): void {
      this.readonlySetter = setter;
    }

    valueSetCallback(value: string | null): void {
      this.value = value;
      this.reportValueChanged(value);
    }

    setValueSetter(setter: (value: string|null) => void): void {
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
export class AntdTextFieldElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdTextField(node);
  }
}
