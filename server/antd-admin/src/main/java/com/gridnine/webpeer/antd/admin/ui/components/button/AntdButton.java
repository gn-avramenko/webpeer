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
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.Map;

public class AntdButton extends BaseUiElement {

    private String title;

    private Map<String,Object> style = new HashMap<>();

    private RunnableWithExceptionAndArgument<OperationUiContext> onClicked;

    public void setOnClicked(RunnableWithExceptionAndArgument<OperationUiContext> onClicked) {
        this.onClicked = onClicked;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = (JsonObject) super.serialize();
        result.addProperty("type", "button");
        result.addProperty("title", title);
        result.add("style", WebPeerUtils.serialize(style));
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
