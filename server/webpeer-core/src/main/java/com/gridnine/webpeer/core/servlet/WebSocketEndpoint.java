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

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketEndpoint {

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    private final Timer timer;

    public WebSocketEndpoint(){
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            private AtomicInteger counter = new AtomicInteger(0);
            @Override
            public void run() {
                counter.incrementAndGet();
                sessions.forEach((key,value) ->{
                    try {
                        value.getBasicRemote().sendText("Hello-"+counter.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 1000L, 1000L);
    }
    @OnMessage
    public String sayHello(String name) {
        System.out.println("Say hello to '" + name + "'");
        return ("Hello" + name);
    }

    @OnOpen
    public void helloOnOpen(Session session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket opened: " + session.getId());
    }

    @OnClose
    public void helloOnClose(Session session, CloseReason reason) {
        sessions.remove(session.getId());
        System.out.println("WebSocket connection closed with CloseCode: " + reason.getCloseCode()+" with id " + session.getId());
    }
}