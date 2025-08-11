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

import com.gridnine.webpeer.core.utils.TypedParameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalUiContext {
    public final static TypedParameter<HttpServletRequest> REQUEST = new TypedParameter<>("request") ;
    public final static TypedParameter<HttpServletResponse> RESPONSE = new TypedParameter<>("response");
    public final static TypedParameter<Instant> LAST_UPDATED = new TypedParameter<>("last-updated");
    public final static TypedParameter<AtomicLong> ELEMENT_INDEX_PROVIDER = new TypedParameter<>("element-index-provider");
    public final static TypedParameter<AtomicInteger> VERSION_PROVIDER = new TypedParameter<>("version-provider");
    public final static TypedParameter<Session> WS_SESSION = new TypedParameter<>("ws-session");
    public final static TypedParameter<Map<Long, BaseUiElement>> UI_ELEMENTS = new TypedParameter<>("ui-elements") ;
    public final static Map<String, Map<String, Map<String, Object>>> context = new ConcurrentHashMap<>();

    public static<T> void setParameter(String path, String clientId, TypedParameter<T> param, T value) {
        var pathData = context.get(path);
        if(pathData == null) {
            if(value == null){
                return;
            }
            pathData= context.computeIfAbsent(path, k -> new ConcurrentHashMap<>());
        }
        var clientData = pathData.get(clientId);
        if(clientData == null) {
            if(value == null){
                return;
            }
            clientData= pathData.computeIfAbsent(clientId, k -> new ConcurrentHashMap<>());
        }
        clientData.put(param.name, value);
    }

    public static<T> T getParameter(String path, String clientId, TypedParameter<T> param) {
        var data = context.get(path);
        if(data == null){
            return null;
        }
        var clientData =  data.get(clientId);
        //noinspection unchecked
        return clientData == null? null: (T) clientData.get(param.name);
    }


}
