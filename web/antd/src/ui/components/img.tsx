import React, { useEffect, useState } from 'react';
import { theme } from 'antd';
import { AntdUiElementFactory, BaseAntdUiElement, buildStyle } from '@/ui/components/common.tsx';
import { generateUUID } from '../../../../core/src/utils/utils.ts';

type AntdImgInternal = {
    id: string
    setSrcSetter: (setter: (src: string) => void) => void
    setWidthSetter: (setter: (width: string | undefined) => void) => void
    setHeightSetter: (setter: (height: string | undefined) => void) => void
    setStyleSetter: (setter: (style: any) => void) => void
    onAfterInitialized: () => void
}

function AntdImg(props: { component: AntdImgInternal }): React.ReactElement {
  const [src, setSrc] = useState<string | undefined>(undefined);
  const [style, setStyle] = useState({});
  const [width, setWidth] = useState<string | undefined>(undefined);
  const [height, setHeight] = useState<string | undefined>(undefined);
  const { token } = theme.useToken();
  props.component.setSrcSetter(setSrc);
  props.component.setWidthSetter(setWidth);
  props.component.setHeightSetter(setHeight);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, []);
  return (
    <img
      alt=""
      key={props.component.id || generateUUID()}
      src={src}
      style={buildStyle(style, token)}
      width={width}
      height={height}
    />
  );
}

class ImgAntdElement extends BaseAntdUiElement implements AntdImgInternal {
    private srcSetter?: (src: string) => void

    private widthSetter?: (width: string | undefined) => void

    private heightSetter?: (height: string | undefined) => void

    private styleSetter?: (setter: (style: any) => void) => void

    private src: string = ''

    private width: string | undefined

    private height: string | undefined

    private style: any | undefined

    setSrcSetter = (setter: (src: string) => void) => {
      this.srcSetter = setter;
    }

    setWidthSetter = (setter: (width: string | undefined) => void) => {
      this.widthSetter = setter;
    }

    setHeightSetter = (setter: (height: string | undefined) => void) => {
      this.heightSetter = setter;
    }

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    constructor(model: any) {
      super(model);
      this.src = model.src;
      this.width = model.width;
      this.height = model.height;
      this.style = model.style;
    }

    executeCommand= () => {
      // noops
    }

    serialize = () => {
      const result = {} as any;
      result.style = this.style;
      result.id = this.id;
      result.src = this.src;
      result.width = this.width;
      result.height = this.height;
      return result;
    }

    onAfterInitialized() {
        this.styleSetter!(this.style);
        this.srcSetter!(this.src);
        this.widthSetter!(this.width);
        this.heightSetter!(this.height);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdImg, { component: this, key: this.id });
    }
}

export class AntdImgElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new ImgAntdElement(node);
  }
}
