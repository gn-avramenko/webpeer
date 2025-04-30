import React, { useEffect, useState } from 'react';
import { Layout, theme } from 'antd';
import { AntdUiElementFactory, buildStyle, BaseAntdUiElement } from './common';

type AntdLayoutInternal = {
    id: string
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
}

function AntdLayout(props: { component: AntdLayoutInternal }): React.ReactElement {
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  const [style, setStyle] = useState({} as any);
  const { token } = theme.useToken();

  props.component.setChildrenSetter(setChildren);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (
    <Layout key={props.component.id} style={buildStyle(style, token)}>
      {children.map((ch) => ch.createReactElement())}
    </Layout>
  );
}

class AntdLayoutElement extends BaseAntdUiElement implements AntdLayoutInternal {
    private styleSetter?: (style: any) => void

    private style: any = {}

    constructor(model: any) {
      super(model);
      this.id = model.id;
      this.style = model.style || {};
    }

    executeCommand = () => {
      // noops
    };

    serialize = () => {
      const result = {} as any;
      result.style = this.style;
      result.id = this.id;
      result.type = 'layout';
      result.tag = this.tag;
      result.children = this.children.map((ch) => ch.serialize());
      return result;
    };

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    onAfterInitialized() {
        this.styleSetter!(this.style);
        this.childrenSetter!(this.children);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdLayout, { component: this, key: this.id });
    }
}

export class AntdLayoutElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdLayoutElement(node);
  }
}
