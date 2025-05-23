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
import com.gridnine.webpeer.core.utils.RunnableWithException;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntdTextField extends BaseUiElement {
    private String value;

    private Map<String,Object> style = new HashMap<>();

    private int debounceTime;

    private boolean deferred;

    private RunnableWithExceptionAndTwoArguments<String, OperationUiContext> onValueChanged;

    public void setOnValueChanged( RunnableWithExceptionAndTwoArguments<String, OperationUiContext> onValueChanged) {
        this.onValueChanged = onValueChanged;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public boolean isDeferred() {
        return deferred;
    }

    public void setDeferred(boolean deferred) {
        this.deferred = deferred;
    }

    public String getValue() {
        return value;
    }

    public int getDebounceTime() {
        return debounceTime;
    }

    public void setDebounceTime(int debounceTime) {
        this.debounceTime = debounceTime;
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = (JsonObject) super.serialize();
        result.addProperty("type", "text-field");
        result.addProperty("value", value);
        result.addProperty("debounceTime", debounceTime);
        result.addProperty("deferred", deferred);
        result.add("style", WebPeerUtils.serialize(style));
        return result;
    }

    @Override
    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        if("value".equals(propertyName)){
            this.value = propertyValue.getAsString();
            if(onValueChanged != null){
                WebPeerUtils.wrapException(() -> onValueChanged.run(this.value, operationUiContext));
            }
        }
    }

    public void setValue(String value, OperationUiContext context){
        this.value = value;
        context.sendElementPropertyChange(getId(), "value", value);
    }

}
