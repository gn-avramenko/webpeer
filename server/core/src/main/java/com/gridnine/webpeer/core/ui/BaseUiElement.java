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

package com.gridnine.webpeer.core.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseUiElement {

    private String tag;

    private final long id;

    private BaseUiElement parent;

    final List<BaseUiElement> children = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void destroy() throws Exception{
        //noops
    }

    public BaseUiElement findChildByTag(String tag){
        return  getChildren().stream().filter(it -> tag.equals(it.tag)).findFirst().orElse(null);
    }

    public BaseUiElement(OperationUiContext ctx) {
        this.id = ctx.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
        this.tag = tag;
    }

    public void setParent(BaseUiElement parent) {
        this.parent = parent;
    }

    public BaseUiElement getParent() {
        return parent;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception {
        var cmd = command.get("cmd").getAsString();
        if ("pc".equals(cmd)) {
            var data = command.get("data").getAsJsonObject();
            var propertyName = data.get("pn").getAsString();
            var propertyValue = data.has("pv") ? data.get("pv") : null;
            updatePropertyValue(propertyName, propertyValue, operationUiContext);
        }
        if ("ac".equals(cmd)) {
            var data = command.get("data").getAsJsonObject();
            var actionId = data.get("id").getAsString();
            var actionData = data.has("data") ? data.get("data") : null;
            executeAction(actionId, actionData, operationUiContext);
        }
    }

    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        throw new UnsupportedOperationException();
    }

    public JsonObject buildElement(OperationUiContext context) {
        var result = new JsonObject();
        result.addProperty("id", String.valueOf(getId()));
        if (tag != null) {
            result.addProperty("tag", tag);
        }
        if (!this.children.isEmpty()) {
            var children = new JsonArray();
            result.add("children", children);
            getChildren().forEach(ch -> {
                WebPeerUtils.wrapException(() -> children.add(ch.buildElement(context)));
            });
        }
        return result;
    }

    public List<BaseUiElement> getChildren() {
        return children;
    }


}
