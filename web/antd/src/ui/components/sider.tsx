import React, { useEffect, useState } from 'react';
import { theme } from 'antd';
import Sider from 'antd/es/layout/Sider';
import { AntdUiElementFactory, BaseAntdUiElement, buildStyle } from '@/ui/components/common.tsx';

type AntdSiderInternal = {
    id: string
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
    setWidthSetter: (setter: (width: number) => void) => void
}

function AntdSider(props: { component: AntdSiderInternal }): React.ReactElement {
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  const [style, setStyle] = useState({} as any);
  const [width, setWidth] = useState<number>(200);
  props.component.setWidthSetter(setWidth);
  const { token } = theme.useToken();

  props.component.setChildrenSetter(setChildren);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (
    <Sider key={props.component.id} style={buildStyle(style, token)} width={width}>
      {children.map((ch) => ch.createReactElement())}
    </Sider>
  );
}

class AntdSiderElement extends BaseAntdUiElement implements AntdSiderInternal {
    private styleSetter?: (style: any) => void

    private widthSetter?: (width: number) => void

    private style: any = {}

    private width: number = 200

    constructor(model: any) {
      super(model);
      this.id = model.id;
      this.style = model.style || {};
      this.width = model.width ?? 200;
    }

    setWidthSetter = (setter: (width: number) => void) => {
      this.widthSetter = setter;
    }

    executeCommand = () => {
      // noops
    };

    serialize = () => {
      const result = {} as any;
      result.style = this.style;
      result.id = this.id;
      result.type = 'header';
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
        this.widthSetter!(this.width);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdSider, { component: this, key: this.id });
    }
}

export class AntdSiderElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdSiderElement(node);
  }
}
