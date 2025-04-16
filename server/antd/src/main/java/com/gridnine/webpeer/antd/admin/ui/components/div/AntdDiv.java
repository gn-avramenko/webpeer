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

package com.gridnine.webpeer.antd.admin.ui.components.div;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithException;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;


public class AntdDiv extends BaseAntdUiElement {

    private String content;

    private boolean hidden;

    private RunnableWithExceptionAndArgument<OperationUiContext> clickHandler;

    public AntdDiv(JsonObject uiData, OperationUiContext ctx) {
        super(ctx);
        hidden = uiData != null && uiData.has("hidden") && uiData.get("hidden").getAsBoolean();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden, OperationUiContext context) {
        this.hidden = hidden;
        context.sendElementPropertyChange(getId(), "hidden", hidden);
    }

    public void setClickHandler(RunnableWithExceptionAndArgument<OperationUiContext> clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if("click".equals(actionId)) {
            WebPeerUtils.wrapException(()->{
                this.clickHandler.run(operationUiContext);
            });
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    @Override
    public String getType() {
        return "div";
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result = super.buildElement(context);
        result.addProperty("handleClick", clickHandler != null);
        if(WebPeerUtils.isNotBlank(content)){
            result.addProperty("content", content);
            result.remove("children");
        }
        return result;
    }

}

