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

package com.gridnine.webpeer.antd.admin.ui.components.button;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.Map;

public class AntdButton extends BaseAntdUiElement {

    private String title;

    private RunnableWithExceptionAndArgument<OperationUiContext> onClicked;

    public void setOnClicked(RunnableWithExceptionAndArgument<OperationUiContext> onClicked) {
        this.onClicked = onClicked;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getType() {
        return "button";
    }

    @Override
    public JsonObject buildElement(JsonObject uiData, OperationUiContext context) {
        var result = super.buildElement(uiData, context);
        result.addProperty("title", title);
        return result;
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if(actionId.equals("click")){
            WebPeerUtils.wrapException(() ->onClicked.run(operationUiContext));
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }
}
