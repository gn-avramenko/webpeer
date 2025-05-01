import React, { useEffect, useState } from 'react';
import { Table, theme } from 'antd';
import { api, generateUUID, uiModel } from 'webpeer-core';
import {
  AntdUiElementFactory, BaseAntdUiElement, buildStyle, onVisible,
} from './common';

type ColumnType = 'STANDARD'| 'CUSTOM'
type ColumnAlignment = 'LEFT' | 'RIGHT'

type Sorting = {
    propertyName: string,
    desc: boolean
}
type ColumnDescription = {
    id: string,
    type: ColumnType,
    alignment: ColumnAlignment,
    name: string,
    sortable: boolean,
    width?: number,
}

type AntdTableInternal = {
    id: string,
    setColumnsSetter: (setter: (columns: ColumnDescription[]) => void) => void
    setDataSetter: (setter: (data:any[]) => void) => void
    setHasMoreSetter: (setter: (hasMore: boolean) => void) => void
    setSortSetter: (setter: (sorting: Sorting) => void) => void
    setLoadingSetter: (setter: (loading: boolean) => void) => void
    loadMore: ()=>Promise<void>
    init: ()=>Promise<void>
    changeSort: (sort:Sorting)=>Promise<void>
    setStyleSetter: (setter: (style: any) => void) => void
    onAfterInitialized: () => void
}

function AntdTable(props: { component: AntdTableInternal }): React.ReactElement {
  const [columns, setColumns] = useState<ColumnDescription[]>([]);
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(false);
  const [style, setStyle] = useState({} as any);
  const parentRef = React.createRef() as any;
  const [tableHeight, setTableHeight] = useState<number | undefined>(parentRef?.current?.clientHeight);
  const [tableWidth, setTableWidth] = useState<number | undefined>(1000);
  const [augmented, setAugmented] = useState(false);
  const [sort, setSort] = useState<Sorting>({
    desc: false,
    propertyName: '',
  });
  const col = columns.find((it) => it.id === sort.propertyName);
  if (col) {
    (col as any).sortOrder = sort.desc ? 'descend' : 'ascend';
  }
  props.component.setStyleSetter(setStyle);
  props.component.setColumnsSetter(setColumns);
  props.component.setDataSetter(setData);
  props.component.setHasMoreSetter(setHasMore);
  props.component.setSortSetter(setSort);
  props.component.setLoadingSetter(setLoading);
  useEffect(() => {
    if (!parentRef?.current) return;
    setTableHeight(parentRef.current.clientHeight - 70);
    setTableWidth(parentRef.current.clientWidth);
  }, [parentRef]);
  useEffect(() => {
    props.component.onAfterInitialized();
    props.component.init();
  }, []);
  const { token } = theme.useToken();
  return (
    <div ref={parentRef as any} key={props.component.id} style={buildStyle(style, token)}>
      <Table
        key={props.component.id}
        loading={loading}
        dataSource={data}
        pagination={false}
        rowKey="id"
        rowSelection={
                        {
                          type: 'checkbox',
                          hideSelectAll: false,
                        }
                    }
        style={{ width: tableWidth }}
        scroll={{
          y: data.length > 10 ? (tableHeight || 200) : undefined,
          x: 'max-content',
        }}
        columns={columns.map((c) => ({
          title: c.name,
          align: c.alignment === 'LEFT' ? 'left' : 'right',
          dataIndex: c.id,
          sorter: c.sortable,
          sortOrder: sort.propertyName === c.id ? (sort.desc ? 'descend' : 'ascend') : undefined,
          width: c.width,
          render: (value) => {
            if (c.type === 'CUSTOM') {
              const node = uiModel.findNode(value) as BaseAntdUiElement;
              return node.createReactElement();
            }
            return value;
          },
        }))}
        onScroll={() => {
          const elms = document.getElementsByClassName('the-last-row');
          if (elms.length) {
            const lastRow = elms[elms.length - 1];
            if (!augmented) {
              setAugmented(true);
              onVisible(lastRow, () => {
                if (hasMore) {
                  setTimeout(async () => {
                    setLoading(true);
                    await props.component.loadMore();
                    setTimeout(() => setAugmented(false), 10);
                  });
                }
              });
            }
          }
        }}
        rowClassName={(_record, index) => {
          const length = data.length as number;
          if (length && index === length - 1) {
            return 'the-last-row';
          }
          return '';
        }}
        onChange={(_pagination, _filter, sorter:any) => {
          setTimeout(async () => {
            setLoading(true);
            await props.component.changeSort({ propertyName: sorter.field ?? sort.propertyName, desc: sorter.order === 'descend' });
            setTimeout(() => setAugmented(false), 10);
          });
        }}
      />
    </div>
  );
}

export class AntdTableElement extends BaseAntdUiElement implements AntdTableInternal {
    private style: any = {}

    private styleSetter?: (style: any) => void

    setColumnsSetter = (setter: (columns: ColumnDescription[]) => void) => {
      this.columnsSetter = setter;
    }

    setDataSetter = (setter: (data: any[]) => void) => {
      this.dataSetter = setter;
    };

    setHasMoreSetter = (setter: (hasMore: boolean) => void) => {
      this.hasMoreSetter = setter;
    }

    setSortSetter = (setter: (sorting: Sorting) => void) => {
      this.sortSetter = setter;
    }

    setLoadingSetter = (setter: (loading: boolean) => void) => {
      this.loadingSetter = setter;
    }

    onAfterInitialized = () => {
      this.columnsSetter(this.columns);
      this.dataSetter(this.data);
      this.hasMoreSetter(this.hasMore);
      this.sortSetter(this.sort);
      this.loadingSetter(true);
      this.styleSetter!(this.style);
    }

    private columnsSetter: (columns: ColumnDescription[]) => void = () => {}

    private dataSetter: (data:any[]) => void = () => {}

    private hasMoreSetter: (hasMore: boolean) => void = () => {}

    private sortSetter: (sort:Sorting) => void = () => {}

    private loadingSetter: (loading:boolean) => void = () => {}

    private requestId?:string;

    private columns:ColumnDescription[] = []

    private data: any[] = []

    private hasMore = false;

    private sort: Sorting = {
      propertyName: '',
      desc: false,
    }

    constructor(model: any) {
      super(model);
      this.columns = model.columns;
      this.data = model.data || [];
      this.hasMore = model.hasMore || true;
      this.style = model.style;
      this.sort = model.sort;
    }

    loadMore() {
      this.requestId = generateUUID();
      return api.sendAction(this.id, 'increaseLimit', this.requestId);
    }

    init() {
      this.requestId = generateUUID();
      return api.sendAction(this.id, 'init', this.requestId);
    }

    refreshData() {
      this.loadingSetter(true);
      this.init();
    }

    changeSort(sort: Sorting) {
      this.requestId = generateUUID();
      return api.sendAction(this.id, 'changeSort', { ...sort, requestId: this.requestId });
    }

    setStyleSetter = (setter: (style: any) => void) => {
      this.styleSetter = setter;
    }

    updatePropertyValue(propertyName: string, propertyValue: any) {
      if (propertyName === 'data') {
        if (this.requestId !== propertyValue.requestId) {
          return;
        }
        this.data = propertyValue.data;
        this.hasMore = propertyValue.hasMore;
        this.dataSetter(this.data);
        this.loadingSetter(false);
        this.hasMoreSetter(this.hasMore);
        this.sortSetter({
          propertyName: propertyValue.sort.propertyName,
          desc: propertyValue.sort.desc,
        });
      }
    }

    createReactElement(): React.ReactElement {
      return React.createElement(AntdTable, { component: this, key: this.id });
    }
}

export class AntdTableElementFactory implements AntdUiElementFactory {
  createElement(node: any): BaseAntdUiElement {
    return new AntdTableElement(node);
  }
}
