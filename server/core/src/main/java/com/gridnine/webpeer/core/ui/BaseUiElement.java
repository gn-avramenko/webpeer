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

public abstract class BaseUiElement implements UiElement {

    private String tag;

    private String key;

    private final long id;

    private BaseUiElement parent;

    private List<UiElement> children = new ArrayList<UiElement>();

    public BaseUiElement() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setParent(UiElement parent) {
        this.parent = (BaseUiElement)parent;
    }

    @Override
    public UiElement getParent() {
        return parent;
    }

    @Override
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

    @Override
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        throw new UnsupportedOperationException();
    }

    protected void updatePropertyValue(String propertyName, JsonElement propertyValue, OperationUiContext operationUiContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("id", String.valueOf(getId()));
        if (getTag() != null) {
            result.addProperty("tag", getTag());
        }
        if (!getChildren().isEmpty()) {
            var chs = new JsonArray();
            result.add("children", chs);
            getChildren().forEach(ch -> {
                WebPeerUtils.wrapException(() -> chs.add(ch.serialize()));
            });
        }
        return result;
    }

    @Override
    public List<UiElement> getChildren() {
        return children;
    }

    public void setChildren(List<UiElement> children) {
        this.children = children;
    }
}
