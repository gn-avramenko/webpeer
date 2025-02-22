import React from "react";
import {UiNode} from "../../../core/src/ui/model.ts";

export interface ReactElementHandler {
   createReactElement(): React.ReactElement
}

export interface ReactElementHandlerFactory {
   createHandler(node: UiNode):ReactElementHandler
}