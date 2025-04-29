import React, { useEffect, useState } from 'react';
import { Dropdown, MenuProps, theme } from 'antd';
import { api, generateUUID } from 'webpeer-core';
import { AntdUiElementFactory, BaseAntdUiElement, buildStyle } from '@/ui/components/common';

type AntdMenuItem = {
    id: string,
    name: string,
    image: string
    imageWidth?: string
    imageHeight?: string;
}

type AntdDropdownImageInternal = {
    id: string
    setSelectedMenuItemIdSetter: (setter: (id: string) => void) => void
    setMenuSetter: (setter: (menu: AntdMenuItem[]) => void) => void
    setStyleSetter: (setter: (style: any) => void) => void
    onAfterInitialized: () => void
}

function AntdDropdownImage(props: { component: AntdDropdownImageInternal }): React.ReactElement {
  const [menu, setMenu] = useState<AntdMenuItem[]>([]);
  const [selectedMenuItemId, setSelectedMenuItemId] = useState('');
  const [style, setStyle] = useState({});
  props.component.setSelectedMenuItemIdSetter(setSelectedMenuItemId);
  props.component.setMenuSetter(setMenu);
  props.component.setStyleSetter(setStyle);
  useEffect(() => {
    props.component.onAfterInitialized();
  }, [props.component]);
  const { token } = theme.useToken();
  const hs = buildStyle(style, token);

  if (!hs.display) {
    hs.display = 'flex';
    hs.flexDirection = 'row';
    hs.alignItems = 'center';
  }
  hs.verticalAlign = 'center';
  const items: MenuProps['items'] = menu.map((mi) => ({
    label: (<div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
      <img
        style={{ display: 'inline-block' }}
        alt=""
        key={props.component.id || generateUUID()}
        width={mi.imageWidth}
        src={mi.image}
        height={mi.imageHeight}
      />
      {' '}
      <div style={{ display: 'inline-block', padding: 5 }}>{mi.name}</div>
    </div>),
    key: mi.id,
  }));
  const selectedItem = menu.find((it) => it.id === selectedMenuItemId);
  return (
    <Dropdown
      placement="bottomLeft"
      menu={{
        items,
        onClick: (item) => {
          api.sendPropertyChanged(props.component.id, 'si', item.key);
        },
      }}
    >
      {selectedItem ? (
        <div style={hs}>
          <img
            alt=""
            key={props.component.id || generateUUID()}
            width={selectedItem.imageWidth}
            src={selectedItem.image}
            height={selectedItem.imageHeight}
          />
          {' '}

        </div>
      ) : (<span style={hs}>Not Selected</span>)}
    </Dropdown>
  );
}

class AntdDropdownImageElement extends BaseAntdUiElement implements AntdDropdownImageInternal {
    private selectedMenuItemIdSetter?: (id: string) => void;

    private menuSetter?: (menu: AntdMenuItem[]) => void

    private styleSetter?: (setter: (style: any) => void) => void

    private menu: AntdMenuItem[] = []

    private selectedItemId: string = ''

    private style: any | undefined

    index: number;

    constructor(model: any) {
      super(model);
      this.index = model.index;
      this.selectedItemId = model.selectedItemId || '';
      this.menu = model.menu;
      this.style = model.style;
    }

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    setSelectedMenuItemIdSetter = (setter: (id: string) => void) => {
      this.selectedMenuItemIdSetter = setter;
    }

    setMenuSetter = (setter: (menu: AntdMenuItem[]) => void) => {
      this.menuSetter = setter;
    }

    serialize = () => {
      const result = {} as any;
      result.id = this.id;
      result.index = this.index;
      result.type = 'dropdown-image';
      result.selectedItemId = this.selectedItemId;
      result.menu = this.menu;
      result.style = this.style;
      return result;
    }

    onAfterInitialized() {
        this.menuSetter!(this.menu);
        this.selectedMenuItemIdSetter!(this.selectedItemId);
        this.menuSetter!(this.menu);
        this.styleSetter!(this.style);
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
      if (propertyName === 'si') {
        this.selectedItemId = propertyValue;
            this.selectedMenuItemIdSetter!(propertyValue);
      }
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdDropdownImage, { component: this, key: this.id });
    }
}

export class AntdDropdownImageElementFactory implements AntdUiElementFactory {
  createElement(node: any): AntdDropdownImageElement {
    return new AntdDropdownImageElement(node);
  }
}
