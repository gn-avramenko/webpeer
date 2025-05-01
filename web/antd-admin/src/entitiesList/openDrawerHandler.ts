import { AntdDivElement, AntdDrawerElement, antdWebpeerExt } from 'webpeer-antd';

antdWebpeerExt.handlers.set('entities-list-filters-open-handler', (data: {elm:AntdDivElement}) => {
  const layout = data.elm.findParentByTag('entities-list')!;
  const drawer = layout.findChildByTag('entities-list-drawer') as AntdDrawerElement;
  drawer.setOpen(true, true);
});
