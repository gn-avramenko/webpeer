import React, { useEffect, useState } from 'react';
import { theme } from 'antd';
import { Content } from 'antd/es/layout/layout';
import { AntdUiElementFactory, BaseAntdUiElement, buildStyle } from '@/ui/components/common.tsx';

type AntdContentInternal = {
    id: string
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
}

function AntdContent(props: { component: AntdContentInternal }): React.ReactElement {
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  const [style, setStyle] = useState({} as any);
  const { token } = theme.useToken();
  props.component.setChildrenSetter(setChildren);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (
    <Content key={props.component.id} style={buildStyle(style, token)}>
      {children.map((ch) => ch.createReactElement())}
    </Content>
  );
}

class AntdContentElement extends BaseAntdUiElement implements AntdContentInternal {
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
      result.type = 'content';
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
      return React.createElement(AntdContent, { component: this, key: this.id });
    }
}

export class AntdContentElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdContentElement(node);
  }
}
