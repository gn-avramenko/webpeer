import {AntdUiDataComponent, AntdUiElement} from "@/ui/components/common.tsx";
import { UiElement } from "node_modules/@webpeer/core/src/model/model";
import React from "react";
import {theme} from "antd";

export interface EntitiesListFilter extends AntdUiElement {
    setData(value: any | null): void;

    getData(): any | null;
}

export type FilterDescription = {
    id: string,
    type: string,
    title: string
}

function StandardFilterElm(props: {
 comp:AntdUiDataComponent<unknown>,
 title: string
}) {
    const {token} = theme.useToken();
    return <div key={props.comp.id} style={{display: "flex", flexDirection:"column"}}>
        <div key="label" style={{padding: 5, fontWeight: token.fontWeightStrong}}>{props.title}</div>
        <div key="comp-wrapper" style={{padding: 5}}>{props.comp.createReactElement()}</div>
    </div>
}
export class StandardFilter implements EntitiesListFilter {

    private readonly comp: AntdUiDataComponent<any>
    private readonly title:string;
    constructor(title: string, comp: AntdUiDataComponent<unknown>) {
        this.comp = comp
        this.title = title;
        this.id = comp.id
    }

    setData(value: any | null): void {
        this.comp.setData(value)
    }
    getData() {
        return this.comp.getData()
    }
    createReactElement(): React.ReactElement {
        return React.createElement(StandardFilterElm, {
            comp: this.comp,
            title: this.title,
            key: this.id,
        })
    }
    id:string
    parent?: UiElement | undefined;
    children?: UiElement[] | undefined;
    serialize = () => {
        return {}
    }
    executeCommand = () => {
        //noops
    }

}