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

package com.gridnine.webpeer.antd.admin.ui.components.layout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;

public class AntdDrawer extends BaseAntdUiElement<AntdDrawerConfiguration> {

    private boolean open;

    public AntdDrawer(AntdDrawerConfiguration config, OperationUiContext ctx) {
        super(config, ctx);
        this.open = config.isOpen();
    }


    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result =  super.buildElement(context);
        result.addProperty("closeIcon", configuration.getCloseIcon());
        result.addProperty("placement", configuration.getPlacement());
        result.addProperty("closable", true);
        result.addProperty("open", configuration.isOpen());
        var styles = new JsonObject();
        styles.add("body", configuration.getBodyStyle());
        styles.add("header", configuration.getHeaderStyle());
        result.add("styles", styles);
        return result;
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if("close".equals(actionId)) {
            open = false;
            operationUiContext.sendElementPropertyChange(getId(), "open", open);
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    public void setOpen(boolean open, OperationUiContext operationUiContext) {
        if(this.open != open) {
            this.open = open;
            operationUiContext.sendElementPropertyChange(getId(), "open", open);
        }
    }

    @Override
    public String getType() {
        return "drawer";
    }
}
