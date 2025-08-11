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
import java.util.Map;
import java.util.Objects;

public abstract class BaseUiElement {

    private String tag;

    private final long id;

    private BaseUiElement parent;

    private final List<BaseUiElement> children = new ArrayList<>();

    private final String path;

    private final String clientId;

    private boolean initialized = false;

    public BaseUiElement(OperationUiContext ctx) {
        this.path = ctx.getParameter(OperationUiContext.PATH);
        this.clientId = ctx.getParameter(OperationUiContext.CLIENT_ID);
        this.id = Objects.requireNonNull(GlobalUiContext.getParameter( path, clientId, GlobalUiContext.ELEMENT_INDEX_PROVIDER)).incrementAndGet();
    }

    public void destroy() throws Exception{
        //noops
    }

    public long getId() {
        return id;
    }

    public void setParent(BaseUiElement parent) {
        this.parent = parent;
    }

    public BaseUiElement getParent() {
        return parent;
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
        initialized = true;
        return result;
    }

    public List<BaseUiElement> getChildren() {
        return children;
    }

    public void sendElementPropertyChange(OperationUiContext ctx, long elementId, String property, Object value){
        var command = new JsonObject();
        command.addProperty("cmd", "ec");
        command.addProperty("id", String.valueOf(elementId));
        var data = new JsonObject();
        data.addProperty("cmd", "pc");
        var commandData = new JsonObject();
        data.add("data", commandData);
        commandData.addProperty("pn", property);
        if(value != null){
            if(value instanceof String){
                commandData.addProperty("pv", (String) value);
            } else if (value instanceof Number){
                commandData.addProperty("pv", (Number) value);
            } else if (value instanceof Boolean){
                commandData.addProperty("pv", (Boolean) value);
            } else if (value instanceof JsonElement){
                commandData.add("pv", (JsonElement) value);
            }
        }
        command.add("data", data);
        ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
    }


    public void setLocalStorageParam(OperationUiContext ctx,String param, Object value){
        var command = new JsonObject();
        command.addProperty("cmd", "uls");
        var data = new JsonObject();
        command.add("data", data);
        data.addProperty("pn", param);
        if(value instanceof String){
            data.addProperty("pv", (String) value);
        } else if (value instanceof Number){
            data.addProperty("pv", (Number) value);
        } else if (value instanceof Boolean) {
            data.addProperty("pv", (Boolean) value);
        } else if (value instanceof JsonElement) {
            data.add("pv", (JsonElement) value);
        }
        ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
    }

    public void reload(OperationUiContext ctx){
        var command = new JsonObject();
        command.addProperty("cmd", "reload");
        ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
    }

    public String getStringLocalStorageParam(OperationUiContext ctx, String paramName){
        var data = ctx.getParameter(OperationUiContext.LOCAL_STORAGE_DATA);
        if(data == null){
            return null;
        }
        return data.has(paramName)? data.get(paramName).getAsString(): null;
    }

    public void removeChild(OperationUiContext ctx, BaseUiElement child) {
        if(initialized) {
            var command = new JsonObject();
            command.addProperty("cmd", "rc");
            command.addProperty("id", String.valueOf(child.getId()));
            ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
        }
        removeElements(this, child);
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

    private void removeElements(BaseUiElement thisElement, BaseUiElement childElement) {
        thisElement.getChildren().remove(childElement);
        Map<Long, BaseUiElement> elements = Objects.requireNonNull(GlobalUiContext.getParameter(path, clientId, GlobalUiContext.UI_ELEMENTS));
        elements.remove(childElement.getId());
        WebPeerUtils.wrapException(childElement::destroy);
        childElement.getChildren().forEach(ch -> BaseUiElement.this.removeElements(childElement, ch));
    }

    public void addChild(OperationUiContext ctx, BaseUiElement child, int idx) {
        child.setParent(this);
        children.add(idx, child);
        if(initialized) {
            var command = new JsonObject();
            command.addProperty("cmd", "ac");
            command.addProperty("id", String.valueOf(id));
            if(idx > 0){
                command.addProperty("insertAfterId", idx-2);
            }
            WebPeerUtils.wrapException(() -> {
                command.add("data", child.buildElement(ctx));
            });
            ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
        }
        addElements(child);
    }

    private void addElements(BaseUiElement childElement) {
        Map<Long, BaseUiElement> elements = Objects.requireNonNull(GlobalUiContext.getParameter(path, clientId, GlobalUiContext.UI_ELEMENTS));
        elements.put(childElement.getId(), childElement);
        childElement.getChildren().forEach(BaseUiElement.this::addElements);
    }

}
