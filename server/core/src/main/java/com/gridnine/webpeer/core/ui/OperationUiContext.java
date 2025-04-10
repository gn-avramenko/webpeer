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
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;

public class OperationUiContext extends HashMap<String, Object> {
    public final static TypedParameter<HttpServletRequest> REQUEST = new TypedParameter<>("request") ;
    public final static TypedParameter<JsonArray> RESPONSE_COMMANDS = new TypedParameter<>("response-commands") ;
    public final static TypedParameter<JsonObject> LOCAL_STORAGE_DATA = new TypedParameter<>("local-storage-data") ;
    public final static TypedParameter<JsonObject> PARAMS = new TypedParameter<>("params") ;

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
        getParameter(RESPONSE_COMMANDS).add(command);
    }

    public void setLocalStorageParam(String param, Object value){
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
        getParameter(RESPONSE_COMMANDS).add(command);
    }


    public void reload(){
        var command = new JsonObject();
        command.addProperty("cmd", "reload");
        getParameter(RESPONSE_COMMANDS).add(command);
    }

    public String getStringLocalStorageParam(String paramName){
        var data = getParameter(LOCAL_STORAGE_DATA);
        if(data == null){
            return null;
        }
        return data.has(paramName)? data.get(paramName).getAsString(): null;
    }


    public void upsertChildCommand(BaseUiElement child, long parentId, OperationUiContext context) {
        var command = new JsonObject();
        command.addProperty("cmd", "uc");
        command.addProperty("id", String.valueOf(parentId));
        WebPeerUtils.wrapException(()->{
            command.add("data", child.buildElement(null, context));
        });
        getParameter(RESPONSE_COMMANDS).add(command);
    }

}
