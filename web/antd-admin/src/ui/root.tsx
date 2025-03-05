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

type MenuItem = {
    icon?: string,
    type: 'GROUP' | 'LEAF',
    name: string,
    id?: string
    children?: MenuItem[]
}
type AntdMainFrameInternal = {
    setMenuSetter: (setter: (menu: MenuItem[]) => void) => void
    setHeaderSetter: (setter: (header:AntdUiElement) => void) => void
    onAfterInitialized: () => void
}

function AntdMainFrame(props: { component: AntdMainFrameInternal }): React.ReactElement {
    const [menuData, setMenuData] = useState<MenuItem[]>([])
    const [header, setHeader] = useState<AntdUiElement>(emptyAntdUiElement)
    props.component.setMenuSetter(setMenuData)
    props.component.setHeaderSetter(setHeader)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    const {
        token: {colorBgContainer, borderRadiusLG},
    } = theme.useToken();
    let headerStyle = ((header as any)?.style || {}) as any
    if(!headerStyle.background){
        headerStyle.background = colorBgContainer
    }
    if(!headerStyle.width){
        headerStyle.width = '100%'
    }

    return (<ConfigProvider>
            <Layout
                style={{background: colorBgContainer, borderRadius: borderRadiusLG, height: '100%'}}
            >
                <Header style={headerStyle}>
                    {((header?.children || []) as AntdUiElement[]).map(ch => ch.createReactElement())}
                </Header>
                <Content style={{height: '100%'}}>
                    <Layout style={{height: '100%'}}>
                        <Sider style={{background: colorBgContainer}} width={200}>
                                        Hello
                        </Sider>
                        <Content style={{width: '100%', height: '100%'}}>Center content {menuData.length}</Content>
                    </Layout>
                </Content>
            </Layout>
        </ConfigProvider>)
}

class AntdMainFrameElement implements AntdUiElement, AntdMainFrameInternal {
    private menuSetter?: (menu: MenuItem[]) => void
    private headerSetter?: (header:AntdUiElement) => void
    private menu: MenuItem[] = []
    children: any[] = []

    constructor(model: any) {
        this.menu = model.menu
        this.id = model.id
        this.index = model.index
        this.children = (model.children || []).map((ch:any)=>{
            return antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch)!
        })

    }

    id: string;
    index: number;
    serialize= () => {
        const result = {} as any;
        result.menu = this.menu;
        result.id = this.id;
        result.index = this.index;
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
        const header = this.children.find((it) => it.id === "header");
        this.headerSetter!(header);
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