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
import com.gridnine.webpeer.core.utils.WebPeerUtils;


public class AntdDiv extends BaseAntdUiElement<AntdDivConfiguration> {

    private boolean hidden;

    public AntdDiv(AntdDivConfiguration config, OperationUiContext ctx) {
        super(config, ctx);
        hidden = config.isHidden();
    }

    public AntdDiv(JsonObject uiData, Object config, OperationUiContext ctx) {
        super(uiData, config, ctx);
    }

    @Override
    protected void updateFromConfig() {
        super.updateFromConfig();
        hidden = configuration.isHidden();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden, OperationUiContext context) {
        this.hidden = hidden;
        context.sendElementPropertyChange(getId(), "hidden", hidden);
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) throws Exception {
        if("click".equals(actionId)) {
            WebPeerUtils.wrapException(()-> this.configuration.getClickHandler().run(operationUiContext));
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
        if(configuration.getClientClickHandlerId() != null){
            result.addProperty("clientClickHandlerId", configuration.getClientClickHandlerId());
        } else {
            result.addProperty("handleClick", configuration.getClickHandler() != null);
        }
        if(WebPeerUtils.isNotBlank(configuration.getContent())){
            result.addProperty("content", configuration.getContent());
            result.remove("children");
        }
        return result;
    }

}

