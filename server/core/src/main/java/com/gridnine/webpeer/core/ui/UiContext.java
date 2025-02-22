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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UiContext {
    public final static String REQUEST_KEY = "request";
    public final static String RESPONSE_KEY = "response";
    public final static String PATH_KEY = "path";
    public final static String CLIENT_ID_KEY = "clientId";
    public final static String WS_SESSION_KEY = "ws-session";
    public final static String LAST_UPDATED = "last-updated";
    public final static String VERSION = "version";
    public final static String UI_MODEL = "ui-model";

    public final static Map<String, Map<String, Map<String, Object>>> context = new ConcurrentHashMap<>();

    public static void setParameter(String path, String clientId, String key, Object value) {
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
            clientData= pathData.computeIfAbsent(path, k -> new ConcurrentHashMap<>());
        }
        clientData.put(key, value);
    }

    public static<T> T getParameter(String path, String clientId, String key, Class<T> cls) {
        var data = context.get(path);
        if(data == null){
            return null;
        }
        var clientData = data.get(clientId);
        //noinspection unchecked
        return clientData == null? null: (T) clientData.get(key);
    }

    public static Map<String, Object> getClientData(String path, String client) {
        var data = context.get(path);
        if(data == null){
            return null;
        }
        return data.get(client);
    }

}
