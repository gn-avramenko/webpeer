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

public class AntdRouter extends BaseAntdUiElement<AntdRouterConfiguration> {

    private String path;

    public AntdRouter(AntdRouterConfiguration config, JsonObject uiData, OperationUiContext ctx) {
        super(config, ctx);
        path = config.getInitPath();
        WebPeerUtils.wrapException(() ->{
            var child = config.getViewProvider().createElement(path, uiData, ctx);
            UiModel.addElement(child, AntdRouter.this);
        });
    }

    public void setPath(String path, OperationUiContext ctx) {
        if(!this.path.equals(path)){
            this.path = path;
            WebPeerUtils.wrapException(()->{
                var newChild = configuration.getViewProvider().createElement(path, null, ctx);
                OperationUiContext.upsertChild(getChildren().get(0), newChild, AntdRouter.this, ctx);
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
