import React, { ReactElement } from 'react';
import {
  webpeerExt, BaseUiElement, WebPeerExtension, generateUUID,
} from 'webpeer-core';

export type AntdWebpeerExtension = WebPeerExtension & {
    elementHandlersFactories: Map<string, AntdUiElementFactory>
    icons: Map<string, () => ReactElement>;
    handlers: Map<string, (ctx: any) => void>;
    lang?: string
}
export const antdWebpeerExt = webpeerExt as AntdWebpeerExtension;
antdWebpeerExt.elementHandlersFactories = new Map();
antdWebpeerExt.icons = new Map();
antdWebpeerExt.handlers = new Map();

export abstract class BaseAntdUiElement extends BaseUiElement {
    abstract createReactElement(): React.ReactElement

    className?:string;

    protected childrenSetter?: (children: BaseAntdUiElement[]) => void

    children: BaseAntdUiElement[] = []

    setChildrenSetter = (setter: (children: BaseAntdUiElement[]) => void) => {
      this.childrenSetter = setter;
    }

    onChildrenChanged = () => {
      this.childrenSetter?.([...this.children]);
    }

    constructor(model: any) {
      super(model);
      this.className = model.className;
      (model.children || []).forEach((ch: any) => {
        const elm = antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch);
        elm.parent = this;
        this.children.push(elm);
      });
    }

    findChildByTag(tag: string): BaseAntdUiElement | undefined {
      if (this.tag === tag) {
        return this;
      }
      for (const child of (this.children || [])) {
        const res = child.findChildByTag(tag);
        if (res) {
          return res;
        }
      }
      return undefined;
    }

    findParentByTag(tag: string): BaseAntdUiElement | undefined {
      if (this.tag === tag) {
        return this;
      }
      if (!this.parent) {
        return undefined;
      }
      return (this.parent as BaseAntdUiElement).findParentByTag(tag);
    }
}

export interface AntdUiElementFactory {
    createElement(model: any): BaseAntdUiElement
}

export const buildStyle = (style: any, token: any) => {
  const result = { ...(style || {}) };
  Object.keys(result).forEach((prop) => {
    let value = style[prop];
    if (typeof value === 'string') {
      if (value.indexOf('token:') > -1) {
        value = token[value.substring(6)];
      }
      result[prop] = value;
    }
  });
  return result;
};

export function onVisible(element: any, callback: any) {
  new IntersectionObserver((entries, observer) => {
    entries.forEach((entry) => {
      if (entry.intersectionRatio > 0) {
        callback(element);
        observer.disconnect();
      }
    });
  }).observe(element);
}
