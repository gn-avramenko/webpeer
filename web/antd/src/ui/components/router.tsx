import React, { useEffect, useState } from 'react';
import { AntdUiElementFactory, BaseAntdUiElement } from '@/ui/components/common.tsx';
import { api } from '../../../../core/src/index.ts';

type AntdRouterInternal = {
    id: string
    onPathChanged: (path:string) => void
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onAfterInitialized: () => void
}

function AntdRouter(props: { component: AntdRouterInternal }): React.ReactElement {
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  props.component.setChildrenSetter(setChildren);
  function handlePopEvent() {
    props.component.onPathChanged(window.location.pathname);
  }
  useEffect(() => {
    props.component.onAfterInitialized();
    window.addEventListener('popstate', handlePopEvent);
    return () => {
      window.removeEventListener('popstate', handlePopEvent);
    };
  }, [props.component]);
  return (<>{children?.map((it) => it.createReactElement())}</>);
}

class AntdRouterElement extends BaseAntdUiElement implements AntdRouterInternal {
    private path : string = ''

    constructor(model: any) {
      super(model);
      this.path = model.path;
    }

    onPathChanged =(path: string) => {
      if (this.path !== path) {
        this.path = path;
        api.sendPropertyChanged(this.id, 'path', window.location.pathname);
      }
    }

    serialize = () => {
      const result = {} as any;
      result.id = this.id;
      result.type = 'router';
      result.path = this.path;
      result.children = this.children.map((ch) => ch.serialize());
      return result;
    };

    updatePropertyValue(propertyName: string, propertyValue: any) {
      if (propertyName === 'path') {
        this.path = propertyValue;
        window.history.pushState(null, '', this.path);
      }
    }

    onAfterInitialized() {
        this.childrenSetter!(this.children || []);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdRouter, { component: this, key: this.id });
    }
}

export class AntdRouterElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdRouterElement(node);
  }
}
