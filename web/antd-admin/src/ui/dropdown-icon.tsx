import {AntdUiElement, AntdUiElementFactory, antdWebpeerExt, updateStyle} from "@/ui/common.tsx";
import React, {useEffect, useState} from "react";
import {Dropdown, MenuProps, theme} from "antd";
import {BaseUiElement} from "../../../core/src/model/model.ts";
import {api} from "../../../core/src/index.ts"

type AntdMenuItem = {
    id: string,
    name: string,
    icon: string
}

type AntdDropdownIconInternal = {
    id: string
    setSelectedMenuItemIdSetter: (setter: (id: string) => void) => void
    setMenuSetter: (setter: (menu: AntdMenuItem[]) => void) => void
    setStyleSetter: (setter: (style: any) => void) => void
    onAfterInitialized: () => void
}

function AntdDropdownIcon(props: { component: AntdDropdownIconInternal }): React.ReactElement {
    const [menu, setMenu] = useState<AntdMenuItem[]>([])
    const [selectedMenuItemId, setSelectedMenuItemId] = useState("")
    const [style, setStyle] = useState({})
    props.component.setSelectedMenuItemIdSetter(setSelectedMenuItemId)
    props.component.setMenuSetter(setMenu)
    props.component.setStyleSetter(setStyle)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    const { token } = theme.useToken()
    const hs = (style && {...style} || {}) as any
    updateStyle(hs, token)
    if(!hs.display){
        hs.display = "inline-block"
    }
    const items: MenuProps['items'] = menu.map(mi => ({
        label: (<span>{antdWebpeerExt.icons.get(mi.icon)!()} {mi.name}</span>),
        key: mi.id
    }))
    const selectedItem = menu.find(it => it.id === selectedMenuItemId)
    return (<Dropdown placement="bottomLeft" menu={{
        items,
        onClick: (item) => {
            api.sendPropertyChanged(props.component.id, "si", item.key)
        }
    }
    }>
        {selectedItem? (<div style={hs}>{antdWebpeerExt.icons.get(selectedItem.icon)!()}</div>) : (<span>Not selected</span>)}
    </Dropdown>)
}

class AntdDropdownIconElement extends BaseUiElement implements AntdDropdownIconInternal {

    private selectedMenuItemIdSetter?: (id: string) => void;
    private menuSetter?: (menu: AntdMenuItem[]) => void
    private styleSetter?: (setter: (style: any) => void) => void

    private  menu: AntdMenuItem[] = []
    private selectedItemId: string = ""
    private style: any | undefined
    parent?: AntdUiElement

    id = "";

    constructor(model: any) {
        super()
        this.id = model.id
        this.selectedItemId = model.selectedItemId || ""
        this.menu = model.menu
    }

    setSelectedMenuItemIdSetter = (setter: (id: string) => void) => {
        this.selectedMenuItemIdSetter = setter
    }
    setMenuSetter =  (setter: (menu: AntdMenuItem[]) => void) => {
        this.menuSetter = setter
    }

    setStyleSetter = (setter: (style: any) => void) => {
        this.styleSetter = setter
    }

    children = undefined


    serialize = () => {
        const result = {} as any
        result.id = this.id;
        result.type = "dropdown-icon";
        result.selectedItemId = this.selectedItemId;
        result.menu = this.menu
        result.style = this.style
        return result;
    }

    onAfterInitialized() {
        this.menuSetter!(this.menu)
        this.selectedMenuItemIdSetter!(this.selectedItemId)
        this.styleSetter!(this.style)
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if("si" === propertyName){
            this.selectedItemId = propertyValue
            this.selectedMenuItemIdSetter!(propertyValue)
        }
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdDropdownIcon, {component: this, key: this.id})
    }

}

export class AntdDropdownIconElementFactory implements AntdUiElementFactory {
    createElement(node: any): AntdDropdownIconElement {
        return new AntdDropdownIconElement(node)
    }
}