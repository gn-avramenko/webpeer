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

package com.gridnine.webpeer.antd.admin.ui.components.dropdown;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public class AntdDropDownIcon extends BaseAntdUiElement {
    private List<IconMenuItem> menu = new ArrayList<IconMenuItem>();
    private String selectedItemId;

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

    @Override
    public JsonObject buildElement(JsonObject uiData, OperationUiContext context) {
        var result = super.buildElement(uiData, context);
        result.addProperty("type", "dropdown-icon");
        result.addProperty("selectedItemId", selectedItemId);
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
                        operationUiContext.sendElementPropertyChange(getId(), "si", itemId);
                    });
                });
            }
        }
    }

    @Override
    public String getType() {
        return "dropdown-icon";
    }
}
