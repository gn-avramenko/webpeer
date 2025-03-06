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
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntdDropDownIcon extends BaseUiElement {
    private List<IconMenuItem> menu = new ArrayList<IconMenuItem>();
    private String selectedItemId;
    private final long id;
    private UiElement parent;

    private Map<String,Object> style = new HashMap<>();

    public AntdDropDownIcon() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public List<IconMenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<IconMenuItem> menu) {
        this.menu = menu;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public Map<String, Object> getStyle() {
        return style;
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
        result.addProperty("id", String.valueOf(id));
        result.addProperty("type", "dropdown-icon");
        result.addProperty("selectedItemId", selectedItemId);
        result.add("style", WebPeerUtils.serialize(style));
        var its = new JsonArray();
        menu.forEach(it ->{
            var obj = new JsonObject();
            obj.addProperty("id", it.getId());
            obj.addProperty("icon", it.getIcon());
            obj.addProperty("name", it.getName());
            its.add(obj);
        });
        result.add("menu", its);
        return result;
    }

    @Override
    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        if("si".equals(propertyName)){
            String itemId = propertyValue.getAsString();
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

    @Override
    public long getId() {
        return id;
    }
}
