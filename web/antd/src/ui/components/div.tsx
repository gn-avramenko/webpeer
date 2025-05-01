import React, { useEffect, useState } from 'react';
import { theme } from 'antd';
import { isBlank } from 'webpeer-core';
import {
  AntdUiElementFactory, buildStyle, BaseAntdUiElement, antdWebpeerExt,
} from './common';

type AntdDivInternal = {
    id: string
    setContentSetter: (setter: (content: string | undefined | null) => void) => void
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onClickHandler?: ()=>void
    onAfterInitialized: () => void
    className?:string;
    setStyleSetter: (setter: (style: any) => void) => void
}

function AntdDiv(props: { component: AntdDivInternal }): React.ReactElement {
  const [content, setContent] = useState<string | undefined | null>();
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  const [style, setStyle] = useState({} as any);
  const { token } = theme.useToken();

  props.component.setContentSetter(setContent);
  props.component.setChildrenSetter(setChildren);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  if (isBlank(content)) {
    return (
    // eslint-disable-next-line jsx-a11y/click-events-have-key-events,jsx-a11y/no-static-element-interactions
      <div
        className={props.component.className}
        onClick={() => {
          props.component.onClickHandler?.();
        }}
        key={props.component.id}
        style={buildStyle(style, token)}
      >
        {children.map((ch) => ch.createReactElement())}
      </div>
    );
  }
  // eslint-disable-next-line react/no-danger
  return <div key={props.component.id} style={buildStyle(style, token)} dangerouslySetInnerHTML={{ __html: content!! }} />;
}

export class AntdDivElement extends BaseAntdUiElement implements AntdDivInternal {
    private contentSetter?: (content: string | undefined | null) => void

    private styleSetter?: (style: any) => void

    private clientClickHandlerId?: string

    private style: any = {}

    private content: string|undefined

    onClickHandler?: ()=>void

    constructor(model: any) {
      super(model);
      this.id = model.id;
      this.style = model.style || {};
      this.content = model.content;
      this.clientClickHandlerId = model.clientClickHandlerId;
      if (model.handleClick) {
        this.onClickHandler = () => {
          super.executeAction('click');
        };
      } else if (this.clientClickHandlerId) {
        this.onClickHandler = () => {
              antdWebpeerExt.handlers.get(this.clientClickHandlerId!)!({
                elm: this,
              });
        };
      }
    }

    executeCommand = () => {
      // noops
    };

    serialize = () => {
      const result = {} as any;
      result.style = this.style;
      result.id = this.id;
      result.content = this.content;
      result.type = 'div';
      result.children = this.children.map((ch) => ch.serialize());
      return result;
    };

    setContentSetter = (setter: (content: string | undefined | null) => void) => {
      this.contentSetter = setter;
    }

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    onAfterInitialized() {
        this.styleSetter!(this.style);
        this.contentSetter!(this.content);
        this.childrenSetter!(this.children);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdDiv, { component: this, key: this.id });
    }
}

export class AntdDivElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdDivElement(node);
  }
}
