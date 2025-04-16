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

package com.gridnine.webpeer.antd.admin.ui.components.router;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdRouter extends BaseAntdUiElement {

    private String path;

    private final AntdViewProvider viewProvider;

    public AntdRouter(JsonObject uiData, String initPath, AntdViewProvider viewProvider, OperationUiContext ctx) {
        super(ctx);
        path = initPath;
        this.viewProvider = viewProvider;
        WebPeerUtils.wrapException(() ->{
            var child = viewProvider.createElement(initPath, uiData, ctx);
            UiModel.addElement(child, AntdRouter.this);
        });
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        return super.buildElement(context);
    }

    public void setPath(String path, OperationUiContext ctx) {
        if(!this.path.equals(path)){
            this.path = path;
            WebPeerUtils.wrapException(()->{
                var newChild = viewProvider.createElement(path, null, ctx);
                ctx.upsertChild(getChildren().get(0), newChild, AntdRouter.this, true);
            });
            ctx.sendElementPropertyChange(getId(), "path", path);
        }
    }

    @Override
    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        if("path".equals(propertyName)){
            setPath(propertyValue.getAsString(), operationUiContext);
            return;
        }
        super.updatePropertyValue(propertyName, propertyValue, operationUiContext);
    }

    @Override
    public String getType() {
        return "router";
    }
}
