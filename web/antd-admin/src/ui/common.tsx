import React from "react";
import {UiNode} from "../../../core/src/ui/model.ts";
import * as webpeerCore from "@webpeer/core";

export type AntdWebpeerExtension = webpeerCore.WebPeerExtension &{
   elementHandlersFactories: Map<string, ReactElementHandlerFactory>
}
export const antdWebpeerExt = webpeerCore.webpeerExt as AntdWebpeerExtension
antdWebpeerExt.elementHandlersFactories = new Map()

export interface ReactElementHandler {
   createReactElement(): React.ReactElement
}

export interface ReactElementHandlerFactory {
   createHandler(node: UiNode):ReactElementHandler
}

export const emptyReactElementHandler:ReactElementHandler = {
   createReactElement(): React.ReactElement {
      return <div></div>;
   }
}