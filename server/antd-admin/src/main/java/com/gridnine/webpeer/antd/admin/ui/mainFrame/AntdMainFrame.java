/*
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.core.ui.*;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AntdMainFrame extends BaseUiElement implements UiRootElement {

    private AntdMainFrameMenu menu = new AntdMainFrameMenu();

    private final UiModel model;

    private JsonObject theme;

    private String lang;

    private String path;

    private AntdViewProvider viewProvider;

    public static AntdMainFrame lookup() {
        return (AntdMainFrame) GlobalUiContext.getParameter(GlobalUiContext.UI_MODEL).getRootElement();
    }

    public AntdMainFrame(UiModel model, Consumer<AntdMainFrameBuilder> configurator) {
        this.model = model;
        var builder = new AntdMainFrameBuilder(this);
        configurator.accept(builder);
    }

    public void setViewProvider(AntdViewProvider viewProvider) {
        this.viewProvider = viewProvider;
        if (path != null) {
            updateCenterContent(null);
        }
    }

    public void setPath(String path, OperationUiContext context) {
        this.path = path;
        if (this.viewProvider != null) {
            updateCenterContent(context);
        }
        if (context != null) {
            context.sendElementPropertyChange(getId(), "path", path);
        }
    }

    private void updateCenterContent(OperationUiContext context) {
        WebPeerUtils.wrapException(() -> {
            var elm = this.viewProvider.createElement(this.path);
            elm.setTag("content");
            UiModel.upsertElement(elm,  this);
        });
    }

    public void setMenu(AntdMainFrameMenu menu, OperationUiContext context) {
        this.menu = menu;
        if (context != null) {
            WebPeerUtils.wrapException(() -> context.sendElementPropertyChange(getId(), "menu", menu.serialize()));
        }
    }

    public void setHeader(AntdDiv header, OperationUiContext context) {
        header.setTag("header");
        UiModel.upsertElement(header, this);
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = (JsonObject)super.serialize();
        result.addProperty("type", "root");
        result.addProperty("path", path);
        if (this.theme != null) {
            result.add("theme", this.theme);
        }
        if (menu != null) {
            result.add("menu", menu.serialize());
        }
        return result;
    }

    public void setTheme(JsonObject theme, OperationUiContext context) {
        this.theme = theme;
        if (context != null) {
            context.sendElementPropertyChange(getId(), "theme", theme);
        }
    }

    @Override
    public UiModel getModel() {
        return this.model;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang, OperationUiContext context) {
        this.lang = lang;
        if (context != null) {
            context.setLocalStorageParam("lang", lang);
            context.reload();
        }
    }

    @Override
    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        if ("path".equals(propertyName)) {
            this.setPath(propertyValue.getAsString(), operationUiContext);
        }
    }
}
