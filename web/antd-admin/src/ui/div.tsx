import {antdWebpeerExt, ReactElementHandler, ReactElementHandlerFactory} from "@/ui/common.tsx";
import React, {useEffect, useState} from "react";
import {UiNode} from "../../../core/src/ui/model.ts";
import {isBlank} from "../../../core/src//utils/utils.ts";

type DivAntdElementInternal = {
    setContentSetter: (setter: (content: string | undefined | null) => void) => void
    setChildrenSetter: (setter: (children: ReactElementHandler[]) => void) => void
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
}

function DivAntdElement(props: { component: DivAntdElementInternal }): React.ReactElement {
    const [content, setContent] = useState<string | undefined | null>()
    const [children, setChildren] = useState<ReactElementHandler[]>([])
    const [style, setStyle] = useState({})
    props.component.setContentSetter(setContent)
    props.component.setChildrenSetter(setChildren)
    props.component.setStyleSetter(setStyle)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    if (isBlank(content)) {
        return <div style={style}>
            {children.map(ch => ch.createReactElement())}
        </div>
    }
    return <div style={style} dangerouslySetInnerHTML={{__html: content!!}}/>
}

class DivAntdElementHandler implements ReactElementHandler, DivAntdElementInternal {
    private contentSetter?: (content: string | undefined | null) => void
    private childrenSetter?: (children: ReactElementHandler[]) => void
    private styleSetter?: (style: any) => void
    private node: UiNode;

    constructor(model: UiNode) {
        model.uiElement = this
        this.node = model;
    }

    setContentSetter = (setter: (content: string | undefined | null) => void) => {
        this.contentSetter = setter
    }

    setChildrenSetter = (setter: (children: ReactElementHandler[]) => void) => {
        this.childrenSetter = setter
    }
    setStyleSetter = (setter: (style: any) => void) => {
        this.styleSetter = setter
    }

    onAfterInitialized() {
        const style = {} as any
        Object.keys(this.node.properties || {}).forEach(prop => {
            if (prop.startsWith("style:")) {
                const stylePropName = prop.substring(5)
                style[stylePropName] = this.node.properties[prop]
            }
        })
        this.styleSetter!(style)
        this.contentSetter!(this.node.properties.content)
        const children: ReactElementHandler[] = []
        this.node.children.forEach(ch => {
            children.push(antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createHandler(ch))
        })
        this.childrenSetter!(children)
    }

    createReactElement(): React.ReactElement {
        return React.createElement(DivAntdElement, {component: this})
    }

}

export class DivAntdElementHandlerFactory implements ReactElementHandlerFactory {
    createHandler(node: UiNode): ReactElementHandler {
        return new DivAntdElementHandler(node)
    }
}