import * as React from 'react';
import { Button, theme } from 'antd';
import { useEffect, useState } from 'react';
import { AntdUiElementFactory, BaseAntdUiElement, buildStyle } from '@/ui/components/common.tsx';

interface ButtonComponentInternal {
    onClick: ()=>void
    title: string
    id: string
    setStyleSetter: (setter: (style: any) => void) => void
    onAfterInitialized: () => void
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function ButtonComponent(props: { component: ButtonComponentInternal }): React.ReactNode {
  const [style, setStyle] = useState({} as any);
  props.component.setStyleSetter(setStyle);
  const { token } = theme.useToken();
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (<Button style={buildStyle(style, token)} key={props.component.id} onClick={() => props.component.onClick()}>{props.component.title}</Button>);
}

export class AntdButton extends BaseAntdUiElement implements ButtonComponentInternal {
    private styleSetter?: (style: any) => void

    private style: any = {}

    serialize = () => ({
      id: this.id,
    })

    createReactElement(): React.ReactElement {
      return React.createElement(ButtonComponent, { component: this, key: this.id });
    }

    title: string

    constructor(model:any) {
      super(model);
      this.title = model.title;
      this.style = model.style || {};
    }

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    };

    onClick = () => {
      super.executeAction('click');
    }

    onAfterInitialized() {
        this.styleSetter!(this.style);
    }
}
export class AntdButtonElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdButton(node);
  }
}
