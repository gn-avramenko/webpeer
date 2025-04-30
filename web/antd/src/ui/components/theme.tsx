import React, { useEffect, useState } from 'react';
import { ConfigProvider, theme } from 'antd';
import { AntdUiElementFactory, BaseAntdUiElement } from './common';

type AntdThemeInternal = {
    id: string
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    setThemeSetter: (setter: (th: any) => void) => void
    onAfterInitialized: () => void
}

function AntdTheme(props: { component: AntdThemeInternal }): React.ReactElement {
  const [th, setTheme] = useState<any>({});
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  props.component.setThemeSetter(setTheme);
  props.component.setChildrenSetter(setChildren);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, []);
  const t = { ...th };
  if (t.algorithm) {
    // @ts-ignore
    t.algorithm = t.algorithm.map((a) => theme[a]);
  }
  return (
    <ConfigProvider theme={t}>
      {children.map((ch) => ch.createReactElement())}
    </ConfigProvider>
  );
}

class AntdThemeElement extends BaseAntdUiElement implements AntdThemeInternal {
    private theme:any = {}

    private themeSetter?: (th: any) => void

    constructor(model: any) {
      super(model);
      this.theme = model.theme;
    }

    setThemeSetter = (setter: (th: any) => void) => {
      this.themeSetter = setter;
    }

    serialize = () => {
      const result = {} as any;
      result.id = this.id;
      result.type = 'theme';
      result.children = this.children.map((ch) => ch.serialize());
      return result;
    };

    updatePropertyValue(propertyName: string, propertyValue: any) {
      if (propertyName === 'theme') {
        this.themeSetter!(propertyValue);
        return;
      }
      super.updatePropertyValue(propertyName, propertyValue);
    }

    onAfterInitialized() {
        this.childrenSetter!(this.children);
        this.themeSetter!(this.theme);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdTheme, { component: this, key: this.id });
    }
}

export class AntdThemeElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdThemeElement(node);
  }
}
