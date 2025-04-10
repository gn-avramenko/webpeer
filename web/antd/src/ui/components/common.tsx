import React, {ReactElement} from "react";
import * as webpeerCore from "../../../../core/src/index.ts";
import {BaseUiElement} from "../../../../core/src/model/model.ts";
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

export abstract class BaseAntdUiElement extends BaseUiElement{

   abstract createReactElement(): React.ReactElement

   children: BaseAntdUiElement[] = []

   constructor(model:any) {
      super(model);
      (model.children || []).forEach((ch:any) =>{
         const elm = antdWebpeerExt.elementHandlersFactories.get(ch.type)!.createElement(ch)
         elm.parent = this
         this.children.push(elm);
      })
   }

   findByTag(tag: string) {
      return this.children?.find(it => it.tag === tag) as BaseAntdUiElement | undefined
   }
}

export interface AntdUiElementFactory {
   createElement(model: any):BaseAntdUiElement
}

export const emptyAntdUiElement:BaseAntdUiElement = {
   async executeAction(): Promise<void> {
      return Promise.resolve(undefined);
   },
   findByTag(): BaseAntdUiElement | undefined {
      return undefined;
   },
   id: "0",
   children: [],
   createReactElement(): React.ReactElement {
      return <div key={generateUUID()}></div>;
   },


   sendPropertyChange: async function () {
   },
   executeCommand: function (): void {
      //noops
   },
   updatePropertyValue: function (): void {
      //noops
   },
   serialize: function (): {} {
      return {}
   }
}

export const updateStyle = (style:any, token:any, addDefaultPadding: boolean)=>{
   Object.keys(style).forEach(prop => {
      var value =style[prop]
      if(typeof value ==='string'){
         if(value.indexOf("token:") > -1){
            value = token[value.substring(6)]
         }
         style[prop] = value;
      }
   })
   if(!style.padding && addDefaultPadding){
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