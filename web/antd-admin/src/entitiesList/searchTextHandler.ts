import { AntdTableElement, AntdTextField, antdWebpeerExt } from 'webpeer-antd';

antdWebpeerExt.handlers.set('entities-list-search-field-change-handler', (data: {elm:AntdTextField}) => {
  console.log('handler');
  const entitiesList = data.elm.findParentByTag('entities-list')!;
  const drawer = entitiesList.findChildByTag('entities-list-table') as AntdTableElement;
  drawer.refreshData();
});
