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
import com.gridnine.webpeer.antd.admin.ui.div.AntdDiv;
import com.gridnine.webpeer.core.ui.*;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AntdMainFrame implements UiRootElement {

    private AntdMainFrameMenu menu = new AntdMainFrameMenu();

    private AntdDiv header;

    private final long id;

    private final UiModel model;

    private final List<UiElement> children = new ArrayList<>();

    private JsonObject theme;

    public static AntdMainFrame lookup(){
        return (AntdMainFrame) GlobalUiContext.getParameter(GlobalUiContext.UI_MODEL).getRootElement();
    }

    public AntdMainFrame(UiModel model, Consumer<AntdMainFrameBuilder> configurator){
        id =  GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
        this.model = model;
        var builder = new AntdMainFrameBuilder(this);
        configurator.accept(builder);
    }

    public void setMenu(AntdMainFrameMenu menu, OperationUiContext context) {
        this.menu = menu;
        if(context != null){
            WebPeerUtils.wrapException(()->context.sendElementPropertyChange(id, "menu",  menu.serialize()));
        }
    }

    public void setHeader(AntdDiv header, OperationUiContext context) {
        if(this.header != header){
            if(this.header != null){
                UiModel.removeElement(this.header);
                if(context != null){
                    context.sendRemoveChildCommand(this.header.getId());
                }
            }
            this.header = header;
            if(header != null){
                UiModel.addElement(header, this);
                if(context != null){
                    context.sendAddChildCommand(this.header.getId(), header, id);
                }
            }
        }
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("type", "root");
        result.addProperty("id", String.valueOf(id));
        if(this.theme != null){
            result.add("theme", this.theme);
        }
        if(menu != null) {
            result.add("menu", menu.serialize());
        }
        JsonArray children = new JsonArray();
        result.add("children", children);
        if(header != null) {
            children.add(header.serialize());
        }
        return result;
    }

    public void setTheme(JsonObject theme, OperationUiContext context) {
        this.theme = theme;
        if(context != null){
            context.sendElementPropertyChange(id, "theme", theme);
        }
    }

    @Override
    public void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception {

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
}
