import { createRoot, Root } from 'react-dom/client';
import {SunOutlined, MoonFilled, RightOutlined, MenuFoldOutlined} from '@ant-design/icons';
import { antdWebpeerExt } from './ui/components/common.tsx';
import { AntdDivElementFactory } from './ui/components/div.tsx';
import { AntdImgElementFactory } from './ui/components/img.tsx';
import { AntdDropdownIconElementFactory } from '@/ui/components/dropdown-icon.tsx';
import { AntdDropdownImageElementFactory } from '@/ui/components/dropdown-image.tsx';
import { AntdTextFieldElementFactory } from '@/ui/components/text-field.tsx';
import { AntdButtonElementFactory } from '@/ui/components/button.tsx';
import { AntdBreakpointElementFactory } from '@/ui/components/breakpoint.tsx';
import { AntdLayoutElementFactory } from '@/ui/components/layout.tsx';
import { AntdContentElementFactory } from '@/ui/components/content.tsx';
import { AntdDrawerElementFactory } from '@/ui/components/drawer.tsx';
import { AntdHeaderElementFactory } from '@/ui/components/header.tsx';
import { AntdSiderElementFactory } from '@/ui/components/sider.tsx';
import { AntdMenuElementFactory } from '@/ui/components/menu.tsx';
import { AntdThemeElementFactory } from '@/ui/components/theme.tsx';
import { uiModel } from '../../core/src/index.ts';
import { BaseUiElement } from '../../core/src/model/model.ts';
import {AntdRouterElementFactory} from "@/ui/components/router.tsx";
import {AntdIconElementFactory} from "@/ui/components/icon.tsx";

antdWebpeerExt.elementHandlersFactories.set('div', new AntdDivElementFactory());
antdWebpeerExt.elementHandlersFactories.set('img', new AntdImgElementFactory());
antdWebpeerExt.elementHandlersFactories.set('text-field', new AntdTextFieldElementFactory());
antdWebpeerExt.elementHandlersFactories.set('dropdown-icon', new AntdDropdownIconElementFactory());
antdWebpeerExt.elementHandlersFactories.set('dropdown-image', new AntdDropdownImageElementFactory());
antdWebpeerExt.elementHandlersFactories.set('button', new AntdButtonElementFactory());
antdWebpeerExt.elementHandlersFactories.set('breakpoint', new AntdBreakpointElementFactory());
antdWebpeerExt.elementHandlersFactories.set('layout', new AntdLayoutElementFactory());
antdWebpeerExt.elementHandlersFactories.set('content', new AntdContentElementFactory());
antdWebpeerExt.elementHandlersFactories.set('drawer', new AntdDrawerElementFactory());
antdWebpeerExt.elementHandlersFactories.set('header', new AntdHeaderElementFactory());
antdWebpeerExt.elementHandlersFactories.set('sider', new AntdSiderElementFactory());
antdWebpeerExt.elementHandlersFactories.set('menu', new AntdMenuElementFactory());
antdWebpeerExt.elementHandlersFactories.set('theme', new AntdThemeElementFactory());
antdWebpeerExt.elementHandlersFactories.set('router', new AntdRouterElementFactory());
antdWebpeerExt.elementHandlersFactories.set('icon', new AntdIconElementFactory());

antdWebpeerExt.icons.set('SUN_OUTLINED', () => <SunOutlined />);
antdWebpeerExt.icons.set('MOON_FILLED', () => <MoonFilled />);
antdWebpeerExt.icons.set('RightOutlined', () => <RightOutlined />);
antdWebpeerExt.icons.set('MENU_FOLD_OUTLINED', () => <MenuFoldOutlined />);

antdWebpeerExt.lang = JSON.parse(window.localStorage.getItem('webpeer') || '{}').lang ?? 'en';

let root: Root|null = null;

antdWebpeerExt.uiHandler = {
  drawUi(model: any) {
    if (!root) {
      root = createRoot(document.getElementById('root') as Element);
    }
    const rootElement = antdWebpeerExt.elementHandlersFactories.get(model.type)!.createElement(model);
    uiModel.setRootElement(rootElement);
    root.render(rootElement.createReactElement());
  },
  createElement(model: any): BaseUiElement {
    return antdWebpeerExt.elementHandlersFactories.get(model.type)!.createElement(model);
  },
};
