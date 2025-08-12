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

package com.gridnine.webpeer.demo.app.components;

import com.google.gson.JsonElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;

public class DemoTextFieldConfiguration extends BaseDemoElementConfiguration {

    private boolean deferred;

    private RunnableWithExceptionAndTwoArguments<String, OperationUiContext> valueChangedHandler;

    private final String initValue;

    public DemoTextFieldConfiguration(JsonElement uiData) {
        this.initValue = uiData != null && uiData.getAsJsonObject().has("value")? uiData.getAsJsonObject().get("value").getAsString(): null;
    }

    public String getInitValue() {
        return initValue;
    }

    public boolean isDeferred() {
        return deferred;
    }

    public void setDeferred(boolean deferred) {
        this.deferred = deferred;
    }

    public RunnableWithExceptionAndTwoArguments<String, OperationUiContext> getValueChangedHandler() {
        return valueChangedHandler;
    }

    public void setValueChangedHandler(RunnableWithExceptionAndTwoArguments<String, OperationUiContext> valueChangedHandler) {
        this.valueChangedHandler = valueChangedHandler;
    }
}
