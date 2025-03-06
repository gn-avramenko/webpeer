import React, {ReactElement} from "react";
import * as webpeerCore from "../../../core/src/index.ts";
import {UiElement} from "../../../core/src/model/model.ts";
import {generateUUID} from "../../../core/src/utils/utils.ts";

export type AntdWebpeerExtension = webpeerCore.WebPeerExtension &{
   elementHandlersFactories: Map<string, AntdUiElementFactory>
   icons: Map<string, () => ReactElement>;
}
export const antdWebpeerExt = webpeerCore.webpeerExt as AntdWebpeerExtension
antdWebpeerExt.elementHandlersFactories = new Map()
antdWebpeerExt.icons = new Map()

export interface AntdUiElement extends UiElement{
   createReactElement(): React.ReactElement
}

export interface AntdUiElementFactory {
   createElement(model: any):AntdUiElement
}

export const emptyAntdUiElement:AntdUiElement = {
   executeCommand(): void {
   },
   id: "0",
   serialize(): any {
   },
   createReactElement(): React.ReactElement {
      return <div key={generateUUID()}></div>;
   }
}

export const updateStyle = (style:any, token:any)=>{
   Object.keys(style).forEach(prop => {
      var value =style[prop]
      if(typeof value ==='string'){
         if(value.indexOf("token:") > -1){
            value = token[value.substring(6)]
         }
         style[prop] = value;
      }
   })
   if(!style.padding){
      style.padding = token.padding
   }
}