import React, {ReactElement} from "react";
import * as webpeerCore from "../../../../core/src/index.ts";
import {UiElement} from "../../../../core/src/model/model.ts";
import {generateUUID} from "../../../../core/src/utils/utils.ts";

export const BREAKPOINTS = { mobile: 0, desktop: 1024 }

export type AntdWebpeerExtension = webpeerCore.WebPeerExtension &{
   elementHandlersFactories: Map<string, AntdUiElementFactory>
   icons: Map<string, () => ReactElement>;
   lang?: string
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

export interface AntdUiDataComponent<T> extends AntdUiElement{
   setData(value:T|null):void;
   getData():T|null;
   setReadonly(value:boolean):void;
}

export type BaseDataFieldProperties = {
   id: string
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

export function onVisible(element:any, callback:any) {
   new IntersectionObserver((entries, observer) => {
      entries.forEach(entry => {
         if(entry.intersectionRatio > 0) {
            callback(element);
            observer.disconnect();
         }
      });
   }).observe(element);
   if(!callback) return new Promise(r => callback=r);
}