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
import com.gridnine.webpeer.core.utils.TypedParameter;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.Map;

public class OperationUiContext extends HashMap<String, Object> {
    public final static TypedParameter<JsonArray> RESPONSE_COMMANDS = new TypedParameter<>("response-commands") ;

    public<T> void setParameter(TypedParameter<T> param, T value) {
        super.put(param.name, value);
    }

    public <T> T getParameter(TypedParameter<T> param) {
        return (T)super.get(param.name);
    }

    public void sendElementPropertyChange(long elementId, String property, Object value){
        var command = new JsonObject();
        command.addProperty("cmd", "ec");
        command.addProperty("id", String.valueOf(elementId));
        var data = new JsonObject();
        data.addProperty("cmd", "pc");
        data.addProperty("pn", property);
        if(value != null){
            if(value instanceof String){
                data.addProperty("pv", (String) value);
            } else if (value instanceof Number){
                data.addProperty("pv", (Number) value);
            } else if (value instanceof Boolean){
                data.addProperty("pv", (Boolean) value);
            } else if (value instanceof JsonElement){
                data.add("pv", (JsonElement) value);
            }
        }
        command.add("data", data);
        getParameter(RESPONSE_COMMANDS).add(command);
    }

    public void sendElementCommand(long elementId, JsonObject data){
        var command = new JsonObject();
        command.addProperty("cmd", "ec");
        command.addProperty("id", String.valueOf(elementId));
        command.add("data", data);
        getParameter(RESPONSE_COMMANDS).add(command);
    }

    public String getCommand(JsonObject command) {
        return command.get("cmd").getAsString();
    }

    public String getChangedPropertyName(JsonObject command) {
        return command.get("data").getAsJsonObject().get("pn").getAsString();
    }

    public String getChangedPropertyStringValue(JsonObject command) {
        var data = command.get("data").getAsJsonObject();
        if(!data.has("pv")){
            return null;
        }
        return data.get("pv").getAsString();
    }


    public void sendRemoveChildCommand(long id) {
        var command = new JsonObject();
        command.addProperty("cmd", "rc");
        command.addProperty("id", String.valueOf(id));
        getParameter(RESPONSE_COMMANDS).add(command);
    }

    public void sendAddChildCommand(long id, UiElement child, long parentId) {
        var command = new JsonObject();
        command.addProperty("cmd", "ac");
        command.addProperty("id", parentId);
        WebPeerUtils.wrapException(()->{
            command.add("data", child.serialize());
        });
        getParameter(RESPONSE_COMMANDS).add(command);
    }
}
