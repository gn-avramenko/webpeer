import {createRoot} from "react-dom/client";
import {antdWebpeerExt} from "./ui/components/common.tsx";
import {AntdMainFrameElementFactory} from "@/ui/main-frame/main-frame.tsx";
import {AntdDivElementFactory} from "./ui/components/div.tsx";
import {AntdImgElementFactory} from "./ui/components/img.tsx";
import {SunOutlined, MoonFilled, RightOutlined} from '@ant-design/icons';
import {AntdDropdownIconElementFactory} from "@/ui/components/dropdown-icon.tsx";
import {AntdDropdownImageElementFactory} from "@/ui/components/dropdown-image.tsx";
import {uiModel} from "../../core/src/index.ts";
import {UiElement} from "../../core/src/model/model.ts";
import {AntdEntitiesListElementFactory} from "@/ui/entities-list/entities-list.tsx";
import { AntdTextFieldElementFactory} from "@/ui/components/text-field.tsx";
import {AntdButtonElementFactory} from "@/ui/components/button.tsx";


antdWebpeerExt.elementHandlersFactories.set("root", new AntdMainFrameElementFactory())
antdWebpeerExt.elementHandlersFactories.set("div", new AntdDivElementFactory())
antdWebpeerExt.elementHandlersFactories.set("img", new AntdImgElementFactory())
antdWebpeerExt.elementHandlersFactories.set("text-field", new AntdTextFieldElementFactory())
antdWebpeerExt.elementHandlersFactories.set("dropdown-icon", new AntdDropdownIconElementFactory())
antdWebpeerExt.elementHandlersFactories.set("dropdown-image", new AntdDropdownImageElementFactory())
antdWebpeerExt.elementHandlersFactories.set("entities-list", new AntdEntitiesListElementFactory())
antdWebpeerExt.elementHandlersFactories.set("button", new AntdButtonElementFactory())
antdWebpeerExt.icons.set("SUN_OUTLINED", () => <SunOutlined/>)
antdWebpeerExt.icons.set("MOON_FILLED", () => <MoonFilled/>)
antdWebpeerExt.icons.set("RightOutlined", () => <RightOutlined/>)
antdWebpeerExt.lang = JSON.parse(window.localStorage.getItem("webpeer") || '{}').lang??"en"

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


