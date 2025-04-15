import {AntdUiElementFactory, antdWebpeerExt, BaseAntdUiElement} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import {Drawer} from "antd";
import {DrawerStyles} from "antd/es/drawer/DrawerPanel";

type AntdDrawerInternal = {
    id: string
    setChildrenSetter: (setter: (children: BaseAntdUiElement[]) => void) => void
    setIconSetter: (setter: (icon: string|null) => void) => void
    setPlacementSetter: (setter: (placement: string) => void) => void
    setStylesSetter: (setter: (styles: DrawerStyles) => void) => void
    setOpenSetter: (setter: (open: boolean) => void) => void
    onClose: () => void
    onAfterInitialized: () => void

}

function AntdDrawer(props: { component: AntdDrawerInternal }): React.ReactElement {
    const [children, setChildren] = useState<BaseAntdUiElement[]>([])
    const [icon, setIcon] = useState<string|null>()
    props.component.setIconSetter(setIcon)
    const [placement, setPlacement] = useState<string>("left")
    props.component.setPlacementSetter(setPlacement)
    const [open, setOpen] = useState<boolean>(false)
    props.component.setOpenSetter(setOpen)
    const [styles, setStyles] = useState<DrawerStyles>({})
    props.component.setStylesSetter(setStyles)
    props.component.setChildrenSetter(setChildren)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    return (<Drawer
        closeIcon={icon && antdWebpeerExt.icons.get(icon)?.()}
        placement={placement as any}
        closable={true}
        open={open}
        onClose={() => props.component.onClose()}
        key={props.component.id}
        styles={styles}
    >
        {children.map(ch => ch.createReactElement())}
    </Drawer>)
}

class AntdDrawerElement extends BaseAntdUiElement implements AntdDrawerInternal {
    private childrenSetter?: (children: BaseAntdUiElement[]) => void
    private openSetter?: (open: boolean) => void
    private open: boolean = false;
    private icon: string|null = null
    private placement: string = 'left'
    private styles: DrawerStyles = {}
    private iconSetter?: (icon: string|null) => void
    private placementSetter?: (placement: string) => void
    private stylesSetter?: (styles: DrawerStyles) => void

    constructor(model: any) {
        super(model)
        this.open = model.open??false
        this.icon = model.icon
        this.placement = model.placement
        this.styles = model.styles;

    }

    setIconSetter = (setter: (icon: string | null) => void) => {
        this.iconSetter = setter
    }
    setPlacementSetter = (setter: (placement: string) => void) => {
        this.placementSetter = setter
    };
    setStylesSetter = (setter: (styles: DrawerStyles) => void) => {
        this.stylesSetter = setter
    }
    setOpenSetter = (setter: (open: boolean) => void) => {
        this.openSetter = setter
    }

    onClose = ()=>{
        super.sendPropertyChange("close", true)
    };

    executeCommand = () => {
        //noops
    };


    serialize = () => {
        const result = {} as any;
        result.id = this.id;
        result.type = "drawer";
        result.tag = this.tag
        result.open = this.open
        result.children = this.children.map(ch =>ch.serialize())
        return result;
    };

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if("open" === propertyName){
            this.open = propertyValue
            this.openSetter!(propertyValue)
        }
    }

    setChildrenSetter = (setter: (children: BaseAntdUiElement[]) => void) => {
        this.childrenSetter = setter
    }

    onAfterInitialized() {
        this.childrenSetter!(this.children)
        this.openSetter!(this.open)
        this.iconSetter!(this.icon)
        this.placementSetter!(this.placement)
        this.stylesSetter!(this.styles)
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdDrawer, {component: this, key: this.id})
    }

}

export class AntdDrawerElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdDrawerElement(node)
    }
}
