import {ReactElementHandler, ReactElementHandlerFactory} from "@/ui/common.ts";
import React, {useEffect, useState} from "react";
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
    onAfterInitialized: () => void
}

function RootAdminAntdElement(props: { component: RootAdminAntdElementInternal }): React.ReactElement {
    const [menuData, setMenuData] = useState<MenuItem[]>([])
    props.component.setMenuSetter(setMenuData)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    return <div>Hello world {menuData.length}</div>
}

class RootAdminAntdElementHandler implements ReactElementHandler, RootAdminAntdElementInternal {
    private menuSetter?: (menu: MenuItem[]) => void

    private readonly rootNode: UiModel

    constructor(model: UiNode) {
        model.uiElement = this
        this.rootNode = model
    }

    setMenuSetter(setter: (menu: MenuItem[]) => void) {
        this.menuSetter = setter
    }

    onAfterInitialized() {
        this.menuSetter!((this.rootNode.properties.menu.items || []) as MenuItem[])
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