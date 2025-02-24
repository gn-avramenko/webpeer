import {
    antdWebpeerExt,
    emptyReactElementHandler,
    ReactElementHandler,
    ReactElementHandlerFactory
} from "@/ui/common.tsx";
import React, { useEffect, useState} from "react";
import {UiModel, UiNode} from "../../../core/src/ui/model.ts";

type MenuItem = {
    icon?: string,
    type: 'GROUP' | 'LEAF',
    name: string,
    id?: string
    children?: MenuItem[]
}
type RootAdminAntdElementInternal = {
    setMenuSetter: (setter: (menu: MenuItem[]) => void) => void
    setHeaderSetter: (setter: (header:ReactElementHandler) => void) => void
    onAfterInitialized: () => void
}

function RootAdminAntdElement(props: { component: RootAdminAntdElementInternal }): React.ReactElement {
    const [menuData, setMenuData] = useState<MenuItem[]>([])
    const [header, setHeader] = useState<ReactElementHandler>(emptyReactElementHandler)
    props.component.setMenuSetter(setMenuData)
    props.component.setHeaderSetter(setHeader)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    return <div><div>Hello world {menuData.length}</div>
        {header && header.createReactElement()}
    </div>
}

class RootAdminAntdElementHandler implements ReactElementHandler, RootAdminAntdElementInternal {
    private menuSetter?: (menu: MenuItem[]) => void
    private headerSetter?: (header:ReactElementHandler) => void

    private readonly rootNode: UiModel

    constructor(model: UiNode) {
        model.uiElement = this
        this.rootNode = model
    }

    setHeaderSetter= (setter: (header: ReactElementHandler) => void) => {
        this.headerSetter = setter
    };

    setMenuSetter(setter: (menu: MenuItem[]) => void) {
        this.menuSetter = setter
    }

    onAfterInitialized() {
        this.menuSetter!((this.rootNode.properties.menu.items || []) as MenuItem[])
        const header = this.rootNode.children.find((it) => it.id === "header");
        if(header){
            const handler =antdWebpeerExt.elementHandlersFactories.get(header.type)?.createHandler(header)!
            this.headerSetter!(handler)
        }
    }

    createReactElement(): React.ReactElement {
        return React.createElement(RootAdminAntdElement, {component: this})
    }

}

export class RootAdminAntdElementHandlerFactory implements ReactElementHandlerFactory {
    createHandler(node: UiNode): ReactElementHandler {
        return new RootAdminAntdElementHandler(node)
    }
}