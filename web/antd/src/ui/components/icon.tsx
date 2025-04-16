import React from 'react';
import { AntdUiElementFactory, antdWebpeerExt, BaseAntdUiElement } from '@/ui/components/common.tsx';

class AntdIconElement extends BaseAntdUiElement {
    private icon: string;

    constructor(model: any) {
      super(model);
      this.icon = model.icon;
    }

    serialize = () => {
      const result = {} as any;
      return result;
    }

    createReactElement(): React.ReactElement {
      return <div key={this.icon}>{antdWebpeerExt.icons.get(this.icon)!()}</div>;
    }
}

export class AntdIconElementFactory implements AntdUiElementFactory {
  createElement(node: any): AntdIconElement {
    return new AntdIconElement(node);
  }
}
