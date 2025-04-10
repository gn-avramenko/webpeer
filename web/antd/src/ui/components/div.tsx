import { AntdUiElementFactory, updateStyle, BaseAntdUiElement} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import {isBlank} from "../../../../core/src/utils/utils.ts";
import {theme} from "antd";

type AntdDivInternal = {
    id: string
    setContentSetter: (setter: (content: string | undefined | null) => void) => void
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
}

function AntdDiv(props: { component: AntdDivInternal }): React.ReactElement {
    const [content, setContent] = useState<string | undefined | null>()
    const [children, setChildren] = useState<BaseAntdUiElement[]>([])
    const [style, setStyle] = useState({} as any)
    const { token } = theme.useToken()
    const padding = token.padding;
    const hs = ((style && {...style}) || {}) as any
    if(!hs.padding){
        hs.padding = padding
    }
    updateStyle(hs, token, true)
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

class AntdDivElement extends BaseAntdUiElement implements AntdDivInternal {
    private contentSetter?: (content: string | undefined | null) => void
    private childrenSetter?: (children: BaseAntdUiElement[]) => void
    private styleSetter?: (style: any) => void
    private style: any = {}
    private content: string|undefined
    constructor(model: any) {
        super(model)
        this.id = model.id;
        this.style = model.style || {};
        this.content = model.content;
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

    setChildrenSetter = (setter: (children: BaseAntdUiElement[]) => void) => {
        this.childrenSetter = setter
    }
    setStyleSetter = (setter: (style: any) => void) => {
        this.styleSetter = setter
    }

    onAfterInitialized() {
        this.styleSetter!(this.style)
        this.contentSetter!(this.content)
        this.childrenSetter!(this.children)
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdDiv, {component: this, key: this.id})
    }

}

export class AntdDivElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdDivElement(node)
    }
}