import {
    antdWebpeerExt,
    emptyAntdUiElement,
    AntdUiElement,
    AntdUiElementFactory
} from "@/ui/common.tsx";
import React, { useEffect, useState} from "react";
import {ConfigProvider, Layout, theme} from "antd";
import {Content, Header} from "antd/es/layout/layout";
import Sider from "antd/es/layout/Sider";
import { UiElement } from "node_modules/@webpeer/core/src/model/model";
import {BaseUiElement} from "../../../core/src/model/model.ts";

type MenuItem = {
    icon?: string,
    type: 'GROUP' | 'LEAF',
    name: string,
    id?: string
    children?: MenuItem[]
}
type AntdMainFrameInternal = {
    setThemeSetter: (setter: (theme: any) => void) => void
    setMenuSetter: (setter: (menu: MenuItem[]) => void) => void
    setHeaderSetter: (setter: (header: AntdUiElement) => void) => void
    onAfterInitialized: () => void
}

function AntdMainFrame(props: { component: AntdMainFrameInternal }): React.ReactElement {
    const [menuData, setMenuData] = useState<MenuItem[]>([])
    const [header, setHeader] = useState<AntdUiElement>(emptyAntdUiElement)
    const [customTheme, setCustomTheme] = useState<any>({})
    props.component.setMenuSetter(setMenuData)
    props.component.setHeaderSetter(setHeader)
    props.component.setThemeSetter(setCustomTheme)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    const {token} = theme.useToken();
    let headerStyle = ((header as any)?.style || {}) as any
    if (!headerStyle.width) {
        headerStyle.width = '100%'
    }
    const ct = customTheme;
    if(ct.algorithm){
        ct.algorithm = (ct.algorithm as string[]).map(a => (theme as any)[a])
    }
    return (<ConfigProvider theme={ct}>
        <Layout
            style={{borderRadius: token.borderRadiusLG, height: '100%'}}
        >
            <Header style={headerStyle}>
                {((header?.children || []) as AntdUiElement[]).map(ch => ch.createReactElement())}
            </Header>
            <Content style={{height: '100%'}}>
                <Layout style={{height: '100%'}}>
                    <Sider width={200}>
                        Hello
                    </Sider>
                    <Content style={{width: '100%', height: '100%'}}>Center content {menuData.length}</Content>
                </Layout>
            </Content>
        </Layout>
    </ConfigProvider>)
}

class AntdMainFrameElement extends BaseUiElement implements AntdMainFrameInternal {
    private menuSetter?: (menu: MenuItem[]) => void
    private headerSetter?: (header: AntdUiElement) => void
    private themeSetter?: (token: any) => void
    private menu: MenuItem[] = []
    private theme: any = {}
    private headerId = "";
    children: any[] = []

    constructor(model: any) {
        super();
        this.menu = model.menu
        this.id = model.id
        this.theme = model.theme
        this.children = (model.children || []).map((ch: any, idx:number) => {
            const elm = antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch)!
            if(idx == 0){
                this.headerId = ch.id;
            }
            elm.parent = this
            return elm
        })

    }

    setThemeSetter = (setter: (theme: any) => void) => {
        this.themeSetter = setter
    }

    parent?: UiElement | undefined;

    id: string;
    serialize= () => {
        const result = {} as any;
        result.menu = this.menu;
        result.id = this.id;
        result.children = this.children.map(ch =>ch.serialize())
    };

    setHeaderSetter= (setter: (header: AntdUiElement) => void) => {
        this.headerSetter = setter
    };

    setMenuSetter(setter: (menu: MenuItem[]) => void) {
        this.menuSetter = setter
    }

    onAfterInitialized() {
        this.menuSetter!(this.menu)
        const header = this.children.find((it) => it.id === this.headerId);
        this.headerSetter!(header);
        this.themeSetter!(this.theme)
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if("theme" == propertyName){
            this.theme = propertyValue
            this.themeSetter!(this.theme)
        }
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdMainFrame, {component: this})
    }

}

export class AntdMainFrameElementFactory implements AntdUiElementFactory {
    createElement(node: any): AntdUiElement {
        return new AntdMainFrameElement(node)
    }
}