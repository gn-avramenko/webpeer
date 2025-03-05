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

package com.gridnine.webpeer.antd.admin.ui.dropdown;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntdDropDownImage implements UiElement {
    private List<ImageMenuItem> menu = new ArrayList<>();
    private String selectedItemId;
    private long id;
    private Map<String,Object> style = new HashMap<>();
    private UiElement parent;

    public AntdDropDownImage() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public List<ImageMenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<ImageMenuItem> menu) {
        this.menu = menu;
    }

    @Override
    public void setParent(UiElement parent) {
        this.parent = parent;
    }

    @Override
    public UiElement getParent() {
        return parent;
    }

    @Override
    public List<UiElement> getChildren() {
        return List.of();
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("id", id);
        result.addProperty("type", "dropdown-image");
        result.addProperty("index", this.id);
        result.addProperty("selectedItemId", selectedItemId);
        result.add("style", WebPeerUtils.serialize(style));
        var its = new JsonArray();
        menu.forEach(it ->{
            var obj = new JsonObject();
            obj.addProperty("id", it.getId());
            obj.addProperty("image", String.format("/_resources/%s", it.getImage()));
            if(it.getImageWidth() != null) {
                obj.addProperty("imageWidth", it.getImageWidth());
            }
            if(it.getImageHeight() != null) {
                obj.addProperty("imageHeight", it.getImageWidth());
            }
            obj.addProperty("name", it.getName());
            its.add(obj);
        });
        result.add("menu", its);
        return result;
    }

    @Override
    public void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception {
        var cmd = operationUiContext.getCommand(command);
        if("pc".equals(cmd)){
            var pn = operationUiContext.getChangedPropertyName(command);
            if("si".equals(pn)){
                String itemId = operationUiContext.getChangedPropertyStringValue(command);
                if(!itemId.equals(selectedItemId)){
                    menu.stream().filter(it -> it.getId().equals(itemId)).findFirst().ifPresent(it -> {
                        WebPeerUtils.wrapException(()->{
                            it.getOnClick().run(operationUiContext);
                            selectedItemId = itemId;
                            operationUiContext.sendElementPropertyChange(id, "si", itemId);
                        });
                    });
                }
            }
        }
    }

    @Override
    public long getId() {
        return 0;
    }
}
