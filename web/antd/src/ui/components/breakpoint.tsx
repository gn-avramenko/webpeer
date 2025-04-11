import {AntdUiElementFactory, BaseAntdUiElement} from "@/ui/components/common.tsx";
import React, {useEffect, useState} from "react";
import useBreakpoint from "use-breakpoint";
import {api} from "../../../../core/src/index";


type AntdBreakpointInternal = {
    id: string
    breakpoints: {}
    setContentSetter: (setter: (content: BaseAntdUiElement) => void) => void
    updateBreakpoint: (bp:string) => void
    onAfterInitialized: () => void
}

function AntdBreakpoint(props: { component: AntdBreakpointInternal }): React.ReactElement {
    const [content, setContent] = useState<BaseAntdUiElement|null>(null)
    props.component.setContentSetter(setContent)
    useEffect(() => {
        props.component.onAfterInitialized()
    }, []);
    const {breakpoint} = useBreakpoint(props.component.breakpoints);
    props.component.updateBreakpoint(breakpoint as any)
    return content?.createReactElement()??<></>
}

class AntdBreakpointElement extends BaseAntdUiElement implements AntdBreakpointInternal {

    breakpoints = {};
    private breakpoint: string = ""
    private contentSetter:  (content: BaseAntdUiElement) => void = ()=>{}

    setContentSetter = (setter: (content: BaseAntdUiElement) => void) =>{
        this.contentSetter = setter
    }

    constructor(model: any) {
        super(model)
        this.breakpoints = model.breakpoints
        this.breakpoint = model.breakpoint
    }

    updateBreakpoint = (bp:string) => {
       if(this.breakpoint !== bp){
           this.breakpoint = bp;
           api.resync()
       }
    }

    executeCommand = () => {
        //noops
    };


    serialize = () => {
        const result = {} as any;
        result.id = this.id;
        result.type = "breakpoint";
        result.breakpoint = this.breakpoint
        result.children = this.children.map(ch =>ch.serialize())
        return result;
    };

    onAfterInitialized() {
        this.contentSetter!(this.children?.[0])
    }

    createReactElement(): React.ReactElement {
        return React.createElement(AntdBreakpoint, {component: this, key: this.id})
    }

}

export class AntdBreakpointElementFactory implements AntdUiElementFactory {
    createElement(node: any): BaseAntdUiElement {
        return new AntdBreakpointElement(node)
    }
}