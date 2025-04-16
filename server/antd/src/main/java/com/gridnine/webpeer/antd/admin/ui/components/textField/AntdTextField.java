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
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdTextField extends BaseAntdUiElement {
    private String value;

    private int debounceTime;

    private boolean deferred;

    private RunnableWithExceptionAndTwoArguments<String, OperationUiContext> onValueChanged;

    public AntdTextField(JsonObject uiData, OperationUiContext ctx) {
        super(ctx);
        this.value = uiData != null && uiData.has("value")? uiData.get("value").getAsString(): null;
    }

    public void setOnValueChanged( RunnableWithExceptionAndTwoArguments<String, OperationUiContext> onValueChanged) {
        this.onValueChanged = onValueChanged;
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
    public JsonObject buildElement(OperationUiContext context) {
        var result = super.buildElement(context);
        result.addProperty("value", value);
        result.addProperty("debounceTime", debounceTime);
        result.addProperty("deferred", deferred);
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
        if(context.findRootElement(this) != null){
            context.sendElementPropertyChange(getId(), "value", value);
        }
    }

    @Override
    public String getType() {
        return "text-field";
    }
}
