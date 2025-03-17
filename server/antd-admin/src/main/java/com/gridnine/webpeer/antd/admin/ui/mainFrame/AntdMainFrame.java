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

    private long headerId;

    private long contentId;

    private final long id;

    private final UiModel model;

    private final List<UiElement> children = new ArrayList<>();

    private JsonObject theme;

    private String lang;

    private String path;

    private AntdViewProvider viewProvider;

    public static AntdMainFrame lookup() {
        return (AntdMainFrame) GlobalUiContext.getParameter(GlobalUiContext.UI_MODEL).getRootElement();
    }

    public AntdMainFrame(UiModel model, Consumer<AntdMainFrameBuilder> configurator) {
        id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
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
            context.sendElementPropertyChange(id, "path", path);
        }
    }

    private void updateCenterContent(OperationUiContext context) {
        WebPeerUtils.wrapException(() -> {
            var existing = children.stream().filter(it -> it.getId() != this.headerId).findFirst().orElse(null);
            if (existing != null) {
                UiModel.removeElement(existing);
                if (context != null) {
                    context.sendRemoveChildCommand(existing.getId());
                }
            }
            var elm = this.viewProvider.createElement(this.path);
            this.contentId = elm.getId();
            UiModel.addElement(elm, this);
            if (context != null) {
                context.sendAddChildCommand(elm, this.id);
                context.sendElementPropertyChange(this.id, "contentId", String.valueOf(this.contentId));
            }
        });
    }

    public void setMenu(AntdMainFrameMenu menu, OperationUiContext context) {
        this.menu = menu;
        if (context != null) {
            WebPeerUtils.wrapException(() -> context.sendElementPropertyChange(id, "menu", menu.serialize()));
        }
    }

    public void setHeader(AntdDiv header, OperationUiContext context) {
        var currentHeader = this.children.stream().filter(it -> it.getId() == headerId).findFirst().orElse(null);
        if (currentHeader != header) {
            if (currentHeader != null) {
                UiModel.removeElement(currentHeader);
                if (context != null) {
                    context.sendRemoveChildCommand(currentHeader.getId());
                }
            }
            this.headerId = header.getId();
            UiModel.addElement(header, this);
            if (context != null) {
                context.sendAddChildCommand(header, id);
                context.sendElementPropertyChange(id, "headerId", String.valueOf(headerId));
            }
        }
    }

    public void setContent(UiElement content, OperationUiContext context) {
        var currentContent = this.children.stream().filter(it -> it.getId() == contentId).findFirst().orElse(null);
        if (currentContent != content) {
            if (currentContent != null) {
                UiModel.removeElement(currentContent);
                if (context != null) {
                    context.sendRemoveChildCommand(currentContent.getId());
                }
            }
            this.contentId = content.getId();
            UiModel.addElement(content, this);
            if (context != null) {
                context.sendAddChildCommand(content, id);
                context.sendElementPropertyChange(id, "contentId", String.valueOf(contentId));
            }
        }
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("type", "root");
        result.addProperty("id", String.valueOf(id));
        result.addProperty("path", path);
        result.addProperty("headerId", String.valueOf(headerId));
        result.addProperty("contentId", String.valueOf(contentId));
        if (this.theme != null) {
            result.add("theme", this.theme);
        }
        if (menu != null) {
            result.add("menu", menu.serialize());
        }
        JsonArray chs = new JsonArray();
        result.add("children", chs);
        children.forEach(child -> {
            WebPeerUtils.wrapException(() -> {
                chs.add(child.serialize());
            });
        });
        return result;
    }

    public void setTheme(JsonObject theme, OperationUiContext context) {
        this.theme = theme;
        if (context != null) {
            context.sendElementPropertyChange(id, "theme", theme);
        }
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public UiModel getModel() {
        return this.model;
    }

    @Override
    public void setParent(UiElement parent) {
        //noops
    }

    @Override
    public UiElement getParent() {
        return null;
    }

    @Override
    public List<UiElement> getChildren() {
        return children;
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
