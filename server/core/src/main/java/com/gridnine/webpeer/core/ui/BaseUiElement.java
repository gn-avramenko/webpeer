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
import com.gridnine.webpeer.core.utils.WebPeerException;
import com.gridnine.webpeer.core.utils.WebPeerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class BaseUiElement {

    private final long id;

    private final String tag;

    private BaseUiElement parent;

    private final String type;

    private final List<BaseUiElement> children = new ArrayList<>();

    private final String path;

    private final String clientId;

    private boolean initialized = false;

    private final Logger logger = LoggerFactory.getLogger(BaseUiElement.class);

    public BaseUiElement(String type, String tag, OperationUiContext ctx) {
        this.path = ctx.getParameter(OperationUiContext.PATH);
        this.clientId = ctx.getParameter(OperationUiContext.CLIENT_ID);
        this.type = type;
        this.id = Objects.requireNonNull(GlobalUiContext.getParameter( path, clientId, GlobalUiContext.ELEMENT_INDEX_PROVIDER)).incrementAndGet();
        this.tag = tag;
    }

    public void restoreFromState(JsonElement state, OperationUiContext ctx){
      // noops
    }

    public BaseUiElement getParent() {
        return parent;
    }

    public void destroy() throws Exception{
        //noops
    }

    public long getId() {
        return id;
    }

    public List<BaseUiElement> getUnmodifiableListOfChildren() {
        return Collections.unmodifiableList(children);
    }

    public void sendCommand(OperationUiContext ctx, String commandId, Object value){
        var command = new JsonObject();
        command.addProperty("id", String.valueOf(getId()));
        command.addProperty("cmd", commandId);
        WebPeerUtils.addProperty(command, "data", value);
        ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
    }

    public void sendPostProcessCommand(OperationUiContext ctx, String commandId, Object value){
        var command = new JsonObject();
        command.addProperty("id", String.valueOf(getId()));
        command.addProperty("cmd", commandId);
        WebPeerUtils.addProperty(command, "data", value);
        ctx.getParameter(OperationUiContext.POST_PROCESS_COMMANDS).add(command);
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

    public JsonObject buildState(OperationUiContext context) {
        var result = new JsonObject();
        initialized = true;
        result.addProperty("id", String.valueOf(getId()));
        if (tag != null) {
            result.addProperty("tag", tag);
        }
        result.addProperty("type", type);
        if (!this.children.isEmpty()) {
            var children = new JsonArray();
            result.add("children", children);
            getUnmodifiableListOfChildren().forEach(ch -> WebPeerUtils.wrapException(() -> children.add(ch.buildState(context))));
        }
        return result;
    }

    private void removeElements(BaseUiElement thisElement, BaseUiElement childElement) {
        thisElement.children.remove(childElement);
        Map<Long, BaseUiElement> elements = Objects.requireNonNull(GlobalUiContext.getParameter(path, clientId, GlobalUiContext.UI_ELEMENTS));
        elements.remove(childElement.getId());
        try{
            childElement.destroy();
        } catch (Throwable e) {
            logger.error("unable to destroy child", e);
        }
        WebPeerUtils.wrapException(childElement::destroy);
        new ArrayList<>(childElement.getUnmodifiableListOfChildren()).forEach(ch -> BaseUiElement.this.removeElements(childElement, ch));
    }

    public void notify(String notificationId, JsonElement data){
        var session = GlobalUiContext.getParameter(path, clientId, GlobalUiContext.WS_SESSION);
        if(session == null){
            throw new IllegalStateException("no session found");
        }
        var payload = new JsonObject();
        payload.addProperty("id", String.valueOf(getId()));
        payload.addProperty("cmd", notificationId);
        WebPeerUtils.addProperty(payload, "data", data);
        var content = payload.toString();
        session.getAsyncRemote().sendText(content);
    }

    public JsonElement doService(String commandId, JsonElement request, OperationUiContext context) throws Exception{
        throw new WebPeerException("not implemented");
    }

    public void addChild(OperationUiContext ctx, BaseUiElement child, int idx) {
        child.parent = this;
        children.add(idx, child);
        if(initialized) {
            var command = new JsonObject();
            command.addProperty("cmd", "ac");
            command.addProperty("id", String.valueOf(id));
            if(idx > 0){
                command.addProperty("insertAfterId", idx-2);
            }
            WebPeerUtils.wrapException(() -> command.add("data", child.buildState(ctx)));
            ctx.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
        }
        addElements(child);
    }

    private void addElements(BaseUiElement childElement) {
        Map<Long, BaseUiElement> elements = Objects.requireNonNull(GlobalUiContext.getParameter(path, clientId, GlobalUiContext.UI_ELEMENTS));
        elements.put(childElement.getId(), childElement);
        childElement.getUnmodifiableListOfChildren().forEach(BaseUiElement.this::addElements);
    }

    public void processCommand(OperationUiContext ctx, String commandId, JsonElement data) throws Exception{
        throw new WebPeerException("not implemented");
    }

    public boolean isInitialized() {
        return initialized;
    }
    public String getTag() {
        return tag;
    }
}
