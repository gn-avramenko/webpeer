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

package com.gridnine.webpeer.antd.admin.ui.components.textField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntdTextField extends BaseUiElement {
    private String value;

    private final long id;
    private Map<String,Object> style = new HashMap<>();
    private UiElement parent;

    public AntdTextField() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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
        var result = (JsonObject) super.serialize();
        result.addProperty("type", "text-field");
        result.addProperty("value", value);
        result.add("style", WebPeerUtils.serialize(style));
        return result;
    }

    @Override
    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        if("v".equals(propertyName)){
            this.value = propertyValue.getAsString();
        }
    }

    public void setValue(String value, OperationUiContext context){
        context.sendElementPropertyChange(id, "v", value);
    }

    @Override
    public long getId() {
        return id;
    }
}
