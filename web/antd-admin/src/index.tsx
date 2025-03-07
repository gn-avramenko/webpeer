import {createRoot} from "react-dom/client";
import {antdWebpeerExt} from "./ui/common.tsx";
import {AntdMainFrameElementFactory} from "@/ui/root.tsx";
import {AntdDivElementFactory} from "./ui/div.tsx";
import {AntdImgElementFactory} from "./ui/img.tsx";
import {SunOutlined, MoonFilled} from '@ant-design/icons';
import {AntdDropdownIconElementFactory} from "@/ui/dropdown-icon.tsx";
import {AntdDropdownImageElementFactory} from "@/ui/dropdown-image.tsx";
import {uiModel} from "../../core/src/index.ts";
import {UiElement} from "../../core/src/model/model.ts";


antdWebpeerExt.elementHandlersFactories.set("root", new AntdMainFrameElementFactory())
antdWebpeerExt.elementHandlersFactories.set("div", new AntdDivElementFactory())
antdWebpeerExt.elementHandlersFactories.set("img", new AntdImgElementFactory())
antdWebpeerExt.elementHandlersFactories.set("dropdown-icon", new AntdDropdownIconElementFactory())
antdWebpeerExt.elementHandlersFactories.set("dropdown-image", new AntdDropdownImageElementFactory())
antdWebpeerExt.icons.set("SUN_OUTLINED", () => <SunOutlined/>)
antdWebpeerExt.icons.set("MOON_FILLED", () => <MoonFilled/>)

antdWebpeerExt.uiHandler = {
    drawUi(model: any) {
        const root = createRoot(document.getElementById('root') as Element);
        const rootElement = antdWebpeerExt.elementHandlersFactories.get("root")!.createElement(model);
        uiModel.setRootElement(rootElement)
        root.render(rootElement.createReactElement());
    },
    createElement(model: any): UiElement {
        return antdWebpeerExt.elementHandlersFactories.get(model.type)!.createElement(model);
    }
}

