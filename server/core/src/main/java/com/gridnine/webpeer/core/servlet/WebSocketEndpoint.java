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

package com.gridnine.webpeer.core.servlet;

import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.utils.WebPeerException;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketEndpoint {

    private final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);

    @OnOpen
    public void onOpen(Session session) {
        var queryString = session.getQueryString();
        String clientId = null;
        String path = null;
        for(var item: queryString.split("&")){
            var values = item.split("=");
            if(values[0].equals("clientId")){
                clientId = values[1];
            } else if (values[0].equals("path")) {
                path = values[1];
            }
        }
        if(path == null){
            throw new WebPeerException("path is null");
        }
        if(clientId == null){
            throw new WebPeerException("clientId is null");
        }
        session.getUserProperties().put("clientId", clientId);
        session.getUserProperties().put("path", path);
        GlobalUiContext.setParameter(path, clientId, GlobalUiContext.WS_SESSION, session);
        log.debug("created ws session with path={} cientId={} sessionId={}", path, clientId, session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String clientId = (String) session.getUserProperties().get("clientId");
        String path = (String) session.getUserProperties().get("path");
        GlobalUiContext.setParameter(path, clientId, GlobalUiContext.WS_SESSION, null);
        log.debug("closed ws session with path={} cientId={} sessionId={} reason = {}", path, clientId, session.getId(), reason);
    }
}