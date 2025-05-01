import { AntdDivElement, AntdDrawerElement, antdWebpeerExt } from 'webpeer-antd';

antdWebpeerExt.handlers.set('main-frame-open-drawer', (data: {elm:AntdDivElement}) => {
  const layout = data.elm.findParentByTag('main-layout')!;
  const drawer = layout.findChildByTag('main-frame-drawer') as AntdDrawerElement;
  drawer.setOpen(true, true);
});
