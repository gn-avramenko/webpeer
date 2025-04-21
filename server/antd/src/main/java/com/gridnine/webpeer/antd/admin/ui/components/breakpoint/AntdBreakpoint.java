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

package com.gridnine.webpeer.antd.admin.ui.components.breakpoint;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AntdBreakpoint extends BaseAntdUiElement<AntdBreakpointConfiguration> {

    public AntdBreakpoint(AntdBreakpointConfiguration config, OperationUiContext ctx) {
        super(config, ctx);
    }

    public AntdBreakpoint(JsonObject uiData, Object config, OperationUiContext ctx) {
        super(uiData, config, ctx);
    }

    @Override
    protected void updateFromConfig() {
        if(configuration.getBreakPoint() == null){
            configuration.getChildren().clear();
        }
        super.updateFromConfig();
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result =  super.buildElement(context);
        result.add("breakpoints", WebPeerUtils.serialize(configuration.getBreakPoints()));
        if(configuration.getBreakPoint() != null) {
            result.addProperty("breakpoint", configuration.getBreakPoint());
        }
        return result;
    }

    @Override
    public List<BaseUiElement> getChildren() {
        return configuration.getBreakPoint() != null? super.getChildren(): Collections.emptyList();
    }

    @Override
    public String getType() {
        return "breakpoint";
    }
}
