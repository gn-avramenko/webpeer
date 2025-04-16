import React, { useEffect, useState } from 'react';
import { Menu, MenuProps, theme } from 'antd';
import {
  AntdUiElementFactory, antdWebpeerExt, BaseAntdUiElement, buildStyle,
} from '@/ui/components/common.tsx';
import { api } from '../../../../core/src/index.ts';

type AntdMenuInternal = {
    id: string
    onAfterInitialized: () => void
    setStyleSetter: (setter: (style: any) => void) => void
    setMenuItemsSetter: (setter: (items: MenuProps['items']) => void) => void
}

function AntdMenu(props: { component: AntdMenuInternal }): React.ReactElement {
  const [style, setStyle] = useState({} as any);
  const [menuItems, setMenuItems] = useState<MenuProps['items']>();
  const { token } = theme.useToken();
  props.component.setStyleSetter(setStyle);
  props.component.setMenuItemsSetter(setMenuItems);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  return (
    <Menu
      mode="inline"
      defaultSelectedKeys={['']}
      defaultOpenKeys={['group-0']}
      style={buildStyle(style, token)}
      items={menuItems}
    />
  );
}

class AntdMenuElement extends BaseAntdUiElement implements AntdMenuInternal {
    private styleSetter?: (style: any) => void

    private menuItemsSetter?: (menuItems: MenuProps['items']) => void

    private style: any = {}

    private menuItems: MenuProps['items'] = []

    constructor(model: any) {
      super(model);
      this.id = model.id;
      this.style = model.style || {};
      this.menuItems = this.buildMenuItems(model.menuItems);
    }

    private buildMenuItems(menuItems:any) {
      return ((menuItems || []) as any[]).map((it, idx) => ({
        key: `group-${idx}`,
        icon: it.icon && antdWebpeerExt.icons.get(it.icon)!(),
        label: it.name,
        children: (it.children || []).map((ch:any, idx2:any) => {
          const elementKey = `element-${idx}-${idx2}`;
          return {
            key: elementKey,
            label: ch.name,
            link: ch.link,
            onClick: () => {
              api.sendAction(this.id, 'click', `${idx}-${idx2}`);
            },
          };
        }),
      }
      ));
    }

    executeCommand = () => {
      // noops
    };

    setMenuItemsSetter = (setter: (menuItems: MenuProps['items']) => void) => {
      this.menuItemsSetter = setter;
    }

    serialize = () => {
      const result = {} as any;
      result.style = this.style;
      result.id = this.id;
      result.type = 'menu';
      result.tag = this.tag;
      return result;
    };

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    onAfterInitialized() {
        this.styleSetter!(this.style);
        this.menuItemsSetter!(this.menuItems);
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdMenu, { component: this, key: this.id });
    }
}

export class AntdMenuElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdMenuElement(node);
  }
}
