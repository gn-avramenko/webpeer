import {UiModel} from "../../core/src/ui/model.ts";
import {createRoot} from "react-dom/client";
import {antdWebpeerExt} from "@/ui/common.tsx";
import {RootAdminAntdElementHandlerFactory} from "@/ui/root.tsx";
import {DivAntdElementHandlerFactory} from "@/ui/div.tsx";


antdWebpeerExt.elementHandlersFactories.set("root", new RootAdminAntdElementHandlerFactory())
antdWebpeerExt.elementHandlersFactories.set("div", new DivAntdElementHandlerFactory())

antdWebpeerExt.uiHandler = {
 drawUi(model: UiModel) {
     const root = createRoot(document.getElementById('root') as Element);
     root.render(antdWebpeerExt.elementHandlersFactories.get("root")!.createHandler(model).createReactElement());
 }
}

