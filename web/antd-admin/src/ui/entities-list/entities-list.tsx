import {AntdUiElementFactory, antdWebpeerExt, BaseAntdUiElement, onVisible} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import {Drawer, Table, theme} from "antd";
import {api} from "../../../../core/src/index.ts";
import debounce from "debounce";
import {FilterOutlined, MenuUnfoldOutlined} from "@ant-design/icons";

type ColumnType = 'TEXT' | 'LINK'| 'CUSTOM'
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
    icon?: string,
}

type AntdEntitiesListInternal = {
    id: string,
    setColumnsSetter: (setter: (columns: ColumnDescription[]) => void) => void
    setDataSetter: (setter: (data:any[]) => void) => void
    setHasMoreSetter: (setter: (hasMore: boolean) => void) => void
    setTitleSetter: (setter: (title: string) => void) => void
    setSortSetter: (setter: (sorting: Sorting) => void) => void
    setLoadingSetter: (setter: (loading: boolean) => void) => void
    filters: BaseAntdUiElement
    searchField: BaseAntdUiElement
    filtersFooter: BaseAntdUiElement
    updateSearchText: (text: string|null) => void,
    onAfterInitialized: () => void
}


function AntdEntitiesList(props: { component: AntdEntitiesListInternal }): React.ReactElement {
    const [columns, setColumns] = useState<ColumnDescription[]>([])
    const [data, setData] = useState<any[]>([]);
    const [loading, setLoading] = useState(false)
    const [hasMore, setHasMore] = useState(false);
    const [title, setTitle] = useState("")
    const parentRef = React.createRef() as any
    const [tableHeight, setTableHeight] = useState<number | undefined>(parentRef?.current?.clientHeight);
    const [tableWidth, setTableWidth] = useState<number | undefined>(1000);
    const [augmented, setAugmented] = useState(false);
    const [filtersCollapsed, setFiltersCollapsed] = useState(true)
    const [sort, setSort] = useState<Sorting>({
        desc: false,
        propertyName:''
    })
    const col = columns.find(it => it.id === sort.propertyName);
    if(col){
        (col as any).sortOrder = sort.desc?  "descend": "ascend"
    }
    props.component.setColumnsSetter(setColumns)
    props.component.setDataSetter(setData)
    props.component.setHasMoreSetter(setHasMore)
    props.component.setTitleSetter(setTitle)
    props.component.setSortSetter(setSort)
    props.component.setLoadingSetter(setLoading)
    const {token} = theme.useToken();
    useEffect(() => {
        if (!parentRef?.current) return;
        setTableHeight(parentRef.current.clientHeight-70)
        setTableWidth(parentRef.current.clientWidth)
    }, [parentRef]);
    useEffect(() => {
       props.component.onAfterInitialized()
        api.sendAction(props.component.id, "init", undefined)
    }, [])
        return <div style={{display: "flex", width: '100%', height: '100%', flexDirection: 'column'}}>
            <div key="header" style={{display: "flex", flexDirection: "row", alignItems: 'center'}}>
                <div key="title" style={{
                    flexGrow: 0,
                    padding: token.padding,
                    fontSize: token.fontSizeHeading2,
                    fontWeight: token.fontWeightStrong,
                    marginRight: token.padding
                }}>{title}</div>
                <div key="glue" style={{flexGrow: 1}}/>
                <div key="search-field" style={{padding: token.padding}}>
                    {props.component.searchField.createReactElement()}
                </div>
                <div key="filters-icon" style={{paddingRight: token.padding}}>
                    <FilterOutlined onClick={() => setFiltersCollapsed(false)}/>
                </div>
            </div>
            <div ref={parentRef as any} key='content' style={{flexGrow: 1}}>
                <Drawer
                    closeIcon={<MenuUnfoldOutlined/>}
                    title={antdWebpeerExt.lang == "ru" ? "Фильтры" : "Filters"}
                    placement="right"
                    closable={true}
                    open={!filtersCollapsed}
                    onClose={() => setFiltersCollapsed(true)}
                    key="filters-drawer"
                    styles={{
                        body: {padding: 0},
                        header: {padding: 0},
                    }}
                    footer = {props.component.filtersFooter.createReactElement()}
                >
                    {props.component.filters.createReactElement()}
                </Drawer>
                <Table
                    loading={loading}
                    dataSource={data}
                    pagination={false}
                    rowKey={'id'}
                    rowSelection={
                        {
                            type: 'checkbox',
                            hideSelectAll: false,
                        }
                    }
                    style={{width: tableWidth}}
                    scroll={{
                        y: data.length > 10? (tableHeight ? tableHeight : 200): undefined,
                        x: 'max-content'
                    }}
                    columns={columns.map(c =>({
                        title: c.name,
                        align: c.alignment == "LEFT"? "left": "right",
                        dataIndex: c.id,
                        sorter: c.sortable,
                        sortOrder:sort.propertyName == c.id? (sort.desc? 'descend': 'ascend'): undefined,
                        width: c.width,
                        render: (value) =>{
                            if(c.type == 'LINK'){
                                return antdWebpeerExt.icons.get(c.icon!)!();
                            }
                            return value
                        }
                    }))}
                    onScroll = {() =>{
                        const elms = document.getElementsByClassName("the-last-row");
                        if(elms.length){
                            const lastRow = elms[elms.length-1]
                            if(!augmented){
                                setAugmented(true)
                                onVisible(lastRow, ()=>{
                                    if(hasMore) {
                                        setAugmented(false)
                                        api.sendAction(props.component.id, "increaseLimit", undefined)
                                    }
                                })
                            }
                        }
                    }
                    }
                    rowClassName={(_record, index) =>{
                        const length = data.length as number
                        if(length && index === length-1){
                            return "the-last-row"
                        }
                        return ""
                    }}
                    onChange={(_pagination, _filter, sorter:any) => {
                        api.sendAction(props.component.id, "changeSort", {propertyName: sorter.field??sort.propertyName, desc: sorter.order == 'descend'})
                    }
                    }
                />
            </div>
        </div>
}

class AntdEntitiesListElement extends BaseAntdUiElement implements AntdEntitiesListInternal {
    serialize = () => {
        //noops
    }
    setColumnsSetter = (setter: (columns: ColumnDescription[]) => void) => {
        this.columnsSetter = setter
    }
    setDataSetter = (setter: (data: any[]) => void) => {
        this.dataSetter = setter
    };
    setHasMoreSetter = (setter: (hasMore: boolean) => void) => {
        this.hasMoreSetter = setter;
    }
    setTitleSetter = (setter: (title: string) => void) => {
        this.titleSetter = setter;
    }

    setSortSetter = (setter: (sorting: Sorting) => void) => {
        this.sortSetter = setter
    }

    setLoadingSetter = (setter: (loading: boolean) => void) => {
        this.loadingSetter = setter
    }

    onAfterInitialized = () => {
        this.columnsSetter(this.columns)
        this.dataSetter(this.data)
        this.hasMoreSetter(this.hasMore)
        this.titleSetter(this.title)
        this.sortSetter(this.sort)
        this.loadingSetter(true)
    }

    private columnsSetter: (columns: ColumnDescription[]) => void = ()=>{}

    private dataSetter: (data:any[]) => void  = ()=>{}

    private hasMoreSetter: (hasMore: boolean) => void = ()=>{}

    private titleSetter: (title: string)=> void = ()=>{}

    private sortSetter: (sort:Sorting) => void = ()=>{}

    private loadingSetter: (loading:boolean) => void = ()=>{}

    private columns:ColumnDescription[] = []

    private data: any[] = []

    private title: string = ""

    private hasMore = false;

    private sort: Sorting = {
        propertyName: "",
        desc: false
    }
    constructor(model: any) {
        super(model);
        this.columns = model.columns
        this.data = model.data
        this.title = model.title
        this.hasMore = model.hasMore
        this.sort = model.sort
        this.filters = this.findByTag("filtersContent")!
        this.filtersFooter = this.findByTag("filtersFooter")!
        this.searchField =this.findByTag("search")!
    }

    filters: BaseAntdUiElement;

    searchField: BaseAntdUiElement;
    filtersFooter: BaseAntdUiElement;

    updateSearchText = debounce((text: string | null) => {
        api.sendAction(this.id, "updateSearchText", text)
    }, 500);

    updatePropertyValue(propertyName: string, propertyValue: any) {
        if(propertyName === 'data'){
            this.data = propertyValue.data
            this.hasMore = propertyValue.hasMore
            this.dataSetter(this.data)
            this.loadingSetter(false)
            this.hasMoreSetter(this.hasMore)
            this.sortSetter({
                propertyName: propertyValue.sort.propertyName,
                desc: propertyValue.sort.desc,
            })
        }
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdEntitiesList, {component: this})
    }

}

export class AntdEntitiesListElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdEntitiesListElement(node)
    }
}
