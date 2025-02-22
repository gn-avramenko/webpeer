import * as webpeerCore from '../../core/src/index';
import {UiModel} from "../../core/src/ui/model.ts";
import {createRoot} from "react-dom/client";
import { ReactElementHandlerFactory} from "@/ui/common.ts";
import {RootAdminAntdElementHandlerFactory} from "@/ui/root.tsx";

type AntdWebpeerExtension = webpeerCore.WebPeerExtension &{
    elementHandlersFactories: Map<string, ReactElementHandlerFactory>
}
const antdWebpeerExt = webpeerCore.webpeerExt as AntdWebpeerExtension
antdWebpeerExt.elementHandlersFactories = new Map()

antdWebpeerExt.elementHandlersFactories.set("root", new RootAdminAntdElementHandlerFactory())

antdWebpeerExt.uiHandler = {
 drawUi(model: UiModel) {
     const root = createRoot(document.getElementById('root') as Element);
     root.render(antdWebpeerExt.elementHandlersFactories.get("root")!.createHandler(model).createReactElement());
 }
}

