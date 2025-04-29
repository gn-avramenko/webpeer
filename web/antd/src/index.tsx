import { createRoot, Root } from 'react-dom/client';
import {
  SunOutlined, MoonFilled, RightOutlined, MenuFoldOutlined, FilterOutlined,
} from '@ant-design/icons';
import { BaseUiElement, uiModel } from 'webpeer-core';
import { antdWebpeerExt } from '@/ui/components/common';
import { AntdDivElementFactory } from '@/ui/components/div';
import { AntdImgElementFactory } from '@/ui/components/img';
import { AntdTextFieldElementFactory } from '@/ui/components/text-field';
import { AntdDropdownIconElementFactory } from '@/ui/components/dropdown-icon';
import { AntdDropdownImageElementFactory } from '@/ui/components/dropdown-image';
import { AntdButtonElementFactory } from '@/ui/components/button';
import { AntdBreakpointElementFactory } from '@/ui/components/breakpoint';
import { AntdLayoutElementFactory } from '@/ui/components/layout';
import { AntdContentElementFactory } from '@/ui/components/content';
import { AntdDrawerElementFactory } from '@/ui/components/drawer';
import { AntdHeaderElementFactory } from '@/ui/components/header';
import { AntdSiderElementFactory } from '@/ui/components/sider';
import { AntdMenuElementFactory } from '@/ui/components/menu';
import { AntdThemeElementFactory } from '@/ui/components/theme';
import { AntdRouterElementFactory } from '@/ui/components/router';
import { AntdIconElementFactory } from '@/ui/components/icon';
import { AntdTableElementFactory } from '@/ui/components/table';

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
antdWebpeerExt.elementHandlersFactories.set('table', new AntdTableElementFactory());

antdWebpeerExt.icons.set('SUN_OUTLINED', () => <SunOutlined />);
antdWebpeerExt.icons.set('MOON_FILLED', () => <MoonFilled />);
antdWebpeerExt.icons.set('RightOutlined', () => <RightOutlined />);
antdWebpeerExt.icons.set('MENU_FOLD_OUTLINED', () => <MenuFoldOutlined />);
antdWebpeerExt.icons.set('FILTER_OUTLINED', () => <FilterOutlined />);

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
