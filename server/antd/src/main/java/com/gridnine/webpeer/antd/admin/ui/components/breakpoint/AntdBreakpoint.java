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
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.TypedParameter;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.Map;

public class AntdBreakpoint extends BaseAntdUiElement {

    public final static TypedParameter<String> BREAKPOINT = new TypedParameter<>("breakpoint") ;

    private Map<String, Object> breakpoints = new HashMap<String, Object>();

    private final String breakPoint;

    public AntdBreakpoint(JsonObject uiData, OperationUiContext ctx) {
        super(ctx);
        breakPoint = uiData == null || !uiData.has("breakpoint")? null : uiData.get("breakpoint").getAsString();
        ctx.setParameter(BREAKPOINT, breakPoint);
    }

    public void setBreakpoints(Map<String, Object> breakpoints) {
        this.breakpoints = breakpoints;
    }

    public Map<String, Object> getBreakpoints() {
        return breakpoints;
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        if(breakPoint == null) {
            var result = new JsonObject();
            result.addProperty("type", "breakpoint");
            result.addProperty("id", getId());
            result.addProperty("tag", getTag());
            result.add("breakpoints", WebPeerUtils.serialize(breakpoints));
            return result;
        }

        var result =  super.buildElement(context);
        result.add("breakpoints", WebPeerUtils.serialize(breakpoints));
        result.addProperty("breakpoint", breakPoint);
        return result;
    }

    @Override
    public String getType() {
        return "breakpoint";
    }
}
