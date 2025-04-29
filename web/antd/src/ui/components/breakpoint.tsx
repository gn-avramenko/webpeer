import React, { useEffect, useState } from 'react';
import useBreakpoint from 'use-breakpoint';
import { api } from 'webpeer-core';
import { AntdUiElementFactory, BaseAntdUiElement } from '@/ui/components/common';

type AntdBreakpointInternal = {
    id: string
    breakpoints: {}
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    updateBreakpoint: (bp:string) => void
    onAfterInitialized: () => void
}

function AntdBreakpoint(props: { component: AntdBreakpointInternal }): React.ReactElement {
  const [children, setChildren] = useState<BaseAntdUiElement[]>([]);
  props.component.setChildrenSetter(setChildren);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  const { breakpoint } = useBreakpoint(props.component.breakpoints);
  props.component.updateBreakpoint(breakpoint as any);
  return <>{children.map((it) => it.createReactElement())}</>;
}

class AntdBreakpointElement extends BaseAntdUiElement implements AntdBreakpointInternal {
    breakpoints = {};

    private breakpoint: string = ''

    constructor(model: any) {
      super(model);
      this.breakpoints = model.breakpoints;
      this.breakpoint = model.breakpoint;
    }

    updateBreakpoint = (bp:string) => {
      if (this.breakpoint !== bp) {
        this.breakpoint = bp;
        api.resync();
      }
    }

    executeCommand = () => {
      // noops
    };

    serialize = () => {
      const result = {} as any;
      result.id = this.id;
      result.type = 'breakpoint';
      result.breakpoint = this.breakpoint;
      result.children = this.children.map((ch) => ch.serialize());
      return result;
    };

    onAfterInitialized() {
        this.childrenSetter!(this.children);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdBreakpoint, { component: this, key: this.id });
    }
}

export class AntdBreakpointElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdBreakpointElement(node);
  }
}
