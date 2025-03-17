//CustomTable.tsx - Need to pass the parent's ref
import { Table, TableProps } from "antd";
import  { useEffect, useRef, useState } from "react";
import * as React from "react";
import {onVisible} from "@/ui/components/common.tsx";

export type CustomTableProps = {
    parentRef?: React.RefObject<HTMLElement>;
    loadMore: () => Promise<void>|void;
} & TableProps<any>;

export default function CustomTable(props: CustomTableProps) {
    const { parentRef, ...tableProps } = props;
    const [tableHeight, setTableHeight] = useState<number | undefined>(parentRef?.current?.clientHeight);
    const tableRef = useRef<HTMLDivElement>(null as any);
    const [extraHeight, setExtraHeight] = useState(50);
    const [augmented, setAugmented] = useState(false);

    useEffect(() => {
        let newExtraHeight = 50;
        if (tableRef.current) {
            const title = tableRef.current.querySelector(".ant-table-title");
            if (title) newExtraHeight += title.clientHeight;
            const pagination = tableRef.current.querySelector(".ant-table-pagination");
            if (pagination) newExtraHeight += (pagination as HTMLElement).clientHeight;
            const footer = tableRef.current.querySelector(".ant-table-footer");
            if (footer) newExtraHeight += (footer as HTMLElement).clientHeight;
        }
        setExtraHeight(newExtraHeight);
    }, [(props as any).title, (props as any).pagination]);

    useEffect(() => {
        if (!parentRef?.current) return;
        const observeTarget = parentRef.current;
        let resizeObserver: ResizeObserver | undefined;
        setTimeout(() => {
            const handleResize = (entries: ResizeObserverEntry[]) => {
                for (const entry of entries) {
                    const newHeight = entry.contentRect.height;
                    setTableHeight(newHeight);
                }
            };

            resizeObserver = new ResizeObserver(handleResize);
            resizeObserver.observe(observeTarget);

            setTableHeight(observeTarget.clientHeight);
        }, 100);

        return () => {
            resizeObserver?.unobserve(observeTarget);
            resizeObserver?.disconnect();
        };
    }, [parentRef]);

    useEffect(()=>{
        setAugmented(false)
    }, [(props as any).dataSource])

    return (
        <div ref={tableRef} style={{ height: "100%", width: "100%" }}>
            <Table
                {...tableProps}
                style={{width: '100%'}}
                rowClassName={(_record, index) =>{
                    // const length = (props as any).dataSource.length as number
                    const length = 0;
                    if(length && index === length-1){
                        return "the-last-row"
                    }
                    return ""
                }}
                // scroll={{
                //     y: tableHeight ? tableHeight - extraHeight : 200,
                //     ...(tableProps as any).scroll,
                // }}
                onScroll = {() =>{
                    const elms = document.getElementsByClassName("the-last-row");
                    if(elms.length){
                        const lastRow = elms[elms.length-1]
                        if(!augmented){
                            setAugmented(true)
                            onVisible(lastRow, ()=>{
                                props.loadMore()
                            })
                        }
                    }
                }
                }
            />
        </div>
    );
}