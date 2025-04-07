import {
    AntdUiElementFactory,
    antdWebpeerExt, BaseAntdUiElement, BREAKPOINTS, emptyAntdUiElement,
} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import {ConfigProvider, Drawer, Layout, Menu, MenuProps, theme} from "antd";
import {Content, Header} from "antd/es/layout/layout";
import Sider from "antd/es/layout/Sider";
import {api} from "../../../../core/src/index.ts";
import useBreakpoint from "use-breakpoint";
import {MenuFoldOutlined, MenuUnfoldOutlined} from "@ant-design/icons";

type Menu = {
    items: MenuItem[]
}
type MenuItem = {
    icon?: string,
    type: 'GROUP' | 'LEAF',
    name: string,
    link?: string,
    children?: MenuItem[]
}
type AntdMainFrameInternal = {
    id: string,
    path?: string,
    setThemeSetter: (setter: (theme: any) => void) => void
    setMenuSetter: (setter: (menu: Menu) => void) => void
    setContentSetter: (setter: (header: BaseAntdUiElement) => void) => void
    setHeaderSetter: (setter: (header: BaseAntdUiElement) => void) => void
    onAfterInitialized: () => void
}

function AntdMainFrame(props: { component: AntdMainFrameInternal }): React.ReactElement {
    const [menuData, setMenuData] = useState<Menu>({items: []})
    const [header, setHeader] = useState<BaseAntdUiElement>(emptyAntdUiElement)
    const [content, setContent] = useState<BaseAntdUiElement>(emptyAntdUiElement)
    const [customTheme, setCustomTheme] = useState<any>({})
    const [drawerCollapsed, setDrawerCollapsed] = useState(true)
    props.component.setMenuSetter(setMenuData)
    props.component.setHeaderSetter(setHeader)
    props.component.setThemeSetter(setCustomTheme)
    props.component.setContentSetter(setContent)

    function handlePopEvent() {
        if (location.pathname !== props.component.path) {
            api.sendPropertyChanged(props.component.id, "path", location.pathname)
        }
    }

    useEffect(() => {
        props.component.onAfterInitialized()
        window.addEventListener('popstate', handlePopEvent)
        return () => {
            window.removeEventListener('popstate', handlePopEvent)
        }
    }, []);
    const {token} = theme.useToken();
    let headerStyle = ((header as any)?.style || {}) as any
    if (!headerStyle.width) {
        headerStyle.width = '100%'
    }
    const defaultPadding = 5
    const {breakpoint} = useBreakpoint(BREAKPOINTS)

    let algorithm = []
    if (breakpoint == 'mobile') {
        if (customTheme.mobileAlgorithm) {
            algorithm = (customTheme.mobileAlgorithm as string[]).map(a => (theme as any)[a])
        }
    } else if (customTheme.desktopAlgorithm) {
        algorithm = (customTheme.desktopAlgorithm as string[]).map(a => (theme as any)[a])
    }
    const menuItems: MenuProps['items'] = menuData.items.map(
        (item, index) => {
            const groupKey = `group-${index}`

            return {
                key: groupKey,
                icon: item.icon && antdWebpeerExt.icons.get(item.icon)!(),
                label: item.name,
                children: (item.children || []).map((ch, idx2) => {
                    const elementKey = `element-${index}-${idx2}`
                    return {
                        key: elementKey,
                        label: ch.name,
                        link: ch.link,
                        onClick: () => {
                            setDrawerCollapsed(true)
                            api.sendPropertyChanged(props.component.id, "path", ch.link)
                        }
                    }
                })
            };
        },
    );
    return (<ConfigProvider theme={{...customTheme, algorithm}}>
        <Layout
            style={{borderRadius: token.borderRadiusLG, height: '100%'}}
        >
            <Header style={headerStyle}>
                {breakpoint === 'mobile' && drawerCollapsed ? (<div style={{padding: defaultPadding}} onClick={() => {
                    console.log("colapsed = false");
                    setDrawerCollapsed(false)
                }
                }><MenuUnfoldOutlined/>
                </div>) : null}
                {((header?.children || []) as BaseAntdUiElement[]).map(ch => ch.createReactElement())}
            </Header>
            <Content style={{height: '100%'}}>
                {breakpoint === "mobile" && !drawerCollapsed ? (
                    <Drawer
                        closeIcon={<MenuFoldOutlined/>}
                        placement="left"
                        closable={true}
                        open={!drawerCollapsed}
                        onClose={() => setDrawerCollapsed(true)}
                        key="menu-drawer"
                        styles={{
                            body: {padding: 0},
                            header: {padding: 0},
                        }}
                    >
                        <Menu
                            mode="inline"
                            defaultSelectedKeys={['']}
                            defaultOpenKeys={['group-0']}
                            style={{height: '100%', overflowY: 'auto'}}
                            items={menuItems}
                        />
                    </Drawer>) : null}
                <Layout style={{height: '100%'}}>
                    {breakpoint === 'desktop' ? (
                        <Sider width={200}>
                            <Menu
                                mode="inline"
                                defaultSelectedKeys={['']}
                                defaultOpenKeys={['group-0']}
                                style={{height: '100%', overflowY: 'auto'}}
                                items={menuItems}
                            />
                        </Sider>) : null
                    }
                    <Content style={{width: '100%', height: '100%'}}>{content && content.createReactElement()}</Content>
                </Layout>
            </Content>
        </Layout>
    </ConfigProvider>)
}

class AntdMainFrameElement extends BaseAntdUiElement implements AntdMainFrameInternal {
    private menuSetter?: (menu: Menu) => void
    private headerSetter?: (header: BaseAntdUiElement) => void
    private contentSetter?: (header: BaseAntdUiElement) => void
    private themeSetter?: (token: any) => void
    path: string = ""
    private menu: Menu = {items: []}
    private theme: any = {}
    children: any[] = []

    constructor(model: any) {
        super(model);
        this.menu = model.menu
        this.theme = model.theme
        this.path = model.path;
        this.children = (model.children || []).map((ch: any) => {
            const elm = antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch)!
            elm.parent = this
            return elm
        })

    }

    setThemeSetter = (setter: (theme: any) => void) => {
        this.themeSetter = setter
    }

    serialize = () => {
        const result = {} as any;
        result.menu = this.menu;
        result.id = this.id;
        result.children = this.children.map(ch => ch.serialize())
    };

    setHeaderSetter = (setter: (header: BaseAntdUiElement) => void) => {
        this.headerSetter = setter
    };

    setContentSetter = (setter: (header: BaseAntdUiElement) => void) => {
        this.contentSetter = setter
    };

    setMenuSetter(setter: (menu: Menu) => void) {
        this.menuSetter = setter
    }

    onAfterInitialized() {
        this.menuSetter!(this.menu)
        const header = this.findByTag("header")!;
        const content = this.findByTag("content")!;
        this.headerSetter!(header);
        this.contentSetter!(content)
        this.themeSetter!(this.theme)
        history.pushState(null, "", this.path)

    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if ("theme" == propertyName) {
            this.theme = propertyValue
            this.themeSetter!(this.theme)
        }
        if ("path" == propertyName) {
            this.path = propertyValue
            history.pushState(null, "", this.path)
        }
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdMainFrame, {component: this})
    }

}

export class AntdMainFrameElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdMainFrameElement(node)
    }
}