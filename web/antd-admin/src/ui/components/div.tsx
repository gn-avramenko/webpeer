import {antdWebpeerExt, AntdUiElement, AntdUiElementFactory, updateStyle} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import {isBlank} from "../../../../core/src/utils/utils.ts";
import {theme} from "antd";

type AntdDivInternal = {
    id: string
    setContentSetter: (setter: (content: string | undefined | null) => void) => void
    setChildrenSetter: (setter: (children: AntdUiElement[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
}

function AntdDiv(props: { component: AntdDivInternal }): React.ReactElement {
    const [content, setContent] = useState<string | undefined | null>()
    const [children, setChildren] = useState<AntdUiElement[]>([])
    const [style, setStyle] = useState({} as any)
    const { token } = theme.useToken()
    const padding = token.padding;
    const hs = ((style && {...style}) || {}) as any
    if(!hs.padding){
        hs.padding = padding
    }
    updateStyle(hs, token)
    props.component.setContentSetter(setContent)
    props.component.setChildrenSetter(setChildren)
    props.component.setStyleSetter(setStyle)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    if (isBlank(content)) {
        return <div key={props.component.id} style={hs}>
            {children.map(ch => ch.createReactElement())}
        </div>
    }
    return <div key={props.component.id} style={hs} dangerouslySetInnerHTML={{__html: content!!}}/>
}

class AntdDivElement implements AntdUiElement, AntdDivInternal {
    id = ""
    private contentSetter?: (content: string | undefined | null) => void
    private childrenSetter?: (children: AntdUiElement[]) => void
    private styleSetter?: (style: any) => void
    private style: any = {}
    private content: string|undefined
    children: AntdUiElement[] = []
    parent?: AntdUiElement
    constructor(model: any) {
        this.id = model.id;
        this.style = model.style || {};
        this.content = model.content;
        (model.children || []).forEach((ch:any) =>{
            const elm = antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch)
            elm.parent = this
            this.children.push(elm)
        })
    }

    executeCommand = () => {
        //noops
    };


    serialize = () => {
        const result = {} as any;
        result.style = this.style;
        result.id = this.id;
        result.content = this.content;
        result.type = "div";
        result.children = this.children.map(ch =>ch.serialize())
        return result;
    };

    setContentSetter = (setter: (content: string | undefined | null) => void) => {
        this.contentSetter = setter
    }

    setChildrenSetter = (setter: (children: AntdUiElement[]) => void) => {
        this.childrenSetter = setter
    }
    setStyleSetter = (setter: (style: any) => void) => {
        this.styleSetter = setter
    }

    onAfterInitialized() {
        this.styleSetter!(this.style)
        this.contentSetter!(this.content)
        const children: AntdUiElement[] = []
        this.childrenSetter!(children)
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdDiv, {component: this, key: this.id})
    }

}

export class AntdDivElementFactory implements AntdUiElementFactory {
    createElement(node: any): AntdUiElement {
        return new AntdDivElement(node)
    }
}