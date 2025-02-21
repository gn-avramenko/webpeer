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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.gridnine.webpeer.core.ui.UiContext;
import com.gridnine.webpeer.core.ui.UiHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BaseWebAppServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public BaseWebAppServlet(){
        var timerTask = new TimerTask() {

            @Override
            public void run() {
                var now = Instant.now();
                UiContext.context.keySet().forEach(it -> {
                   var clients = new HashMap<>(UiContext.context.get(it));
                   clients.forEach((k, v) -> {
                       var upd = (Instant)v.get(UiContext.LAST_UPDATED);
                       if(upd == null || Duration.between(upd, now).getSeconds() > TimeUnit.HOURS.toSeconds(1)){
                           UiContext.context.get(it).remove(k);
                           Session s = (Session) v.get(UiContext.WS_SESSION_KEY);
                           if(s != null){
                               try {
                                   s.close();
                               } catch (Throwable e) {
                                   logger.error("Error closing session", e);
                               }
                           }
                       }
                   });
                });
            }
        };
        Timer cleanupSessionTimer = new Timer(true);
        cleanupSessionTimer.schedule(timerTask, 60_000, 60_000);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
                doGetIndexHtml(resp);
                return;
            }
            if (pathInfo.equals("/fav.ico")) {
                if (getFaviconUrl() != null) {
                    writeResource(getFaviconUrl(), resp);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (pathInfo.startsWith("/_resources/")) {
                String resourceName = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                HtmlLinkWrapper linkWrapper = getModules().stream().flatMap(it -> it.links.stream())
                        .filter(it -> it.name.equals(resourceName)).findFirst().orElse(null);
                if (linkWrapper != null) {
                    writeResource(linkWrapper.url, resp);
                    return;
                }
                HtmlScriptWrapper scriptWrapper = getModules().stream().flatMap(it -> it.scripts.stream())
                        .filter(it -> it.name.equals(resourceName) || resourceName.equals(it.jsMapName)).findFirst().orElse(null);
                if (scriptWrapper != null) {
                    writeResource(resourceName.equals(scriptWrapper.name)? scriptWrapper.url: scriptWrapper.jsMapUrl, resp);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (pathInfo.startsWith("/_ui")) {
                JsonElement request;
                try(var is = req.getInputStream()){
                    request = new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonElement.class);
                }
                String clientId = req.getHeader("X-Client-Id");
                Map<String, Object> context = new HashMap<>();
                context.put(UiContext.REQUEST_KEY, req);
                context.put(UiContext.RESPONSE_KEY, resp);
                context.put(UiContext.CLIENT_ID_KEY, clientId);
                var pi = URLEncoder.encode(pathInfo, StandardCharsets.UTF_8);
                context.put(UiContext.PATH_KEY, pi);
                UiContext.setParameter(pi, clientId, UiContext.LAST_UPDATED, Instant.now());
                var result = getUiHandler().processCommand(request, context);
                try(var os= resp.getOutputStream()){
                    var jw = new JsonWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    new Gson().toJson(result, jw);
                    jw.flush();
                }
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Throwable e){
            throw  new ServletException(e);
        }
    }

    protected abstract List<WebAppModule> getModules() throws Exception;

    protected abstract URL getFaviconUrl();

    protected void writeResource(URL url, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        try(var is = url.openStream()) {
            try (var os = resp.getOutputStream()) {
                is.transferTo(os);
                os.flush();
            }
        }
    }


    protected void doGetIndexHtml(HttpServletResponse resp) throws Exception {
        String content = getContent(getIndexHtmlUrl());
        content = content.replace("${title}", getTitle());
        String scripts = getModules().stream().flatMap(it -> it.scripts.stream())
                .map(it -> String.format("<script type=\"text/javascript\" src=\"_resources/%s\"></script>\n", it.name)).reduce("", (a,b) -> a+b);
        String links = getModules().stream().flatMap(it -> it.links.stream())
                .map(it -> String.format("<link rel=\"%s\" type=\"%s\" src=\"_resources/%s\"></link>\n", it.rel, it.type, it.name)).reduce("", (a,b) -> a+b);
        String parameters = getWebAppParameters().entrySet().stream()
                .map(it -> String.format("%s: \"%s\",\n", it.getKey(), it.getValue())).reduce("", (a,b) -> a+b);
        content = content.replace("${favicon}", getFaviconUrl() == null? "": "<link rel=\"icon\" type=\"image/x-icon\" href=\"/fav.ico\">");
        content = content.replace("${links}", links);
        content = content.replace("${scripts}", scripts);
        content = content.replace("${parameters}", String.format("window.webPeer = {parameters: {\n%s\n}\n}", parameters));
        writeStringContent(content, resp);
    }

    protected void writeStringContent(String content, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding("UTF-8");
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try (var os = resp.getOutputStream()) {
            new ByteArrayInputStream(bytes).transferTo(os);
            os.flush();
        }
    }

    protected String getContent(URL indexHtmlUrl) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(var is = indexHtmlUrl.openStream()){
            is.transferTo(baos);
            baos.flush();
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    protected abstract Map<String,String> getWebAppParameters();

    protected abstract String getTitle();

    protected abstract UiHandler getUiHandler();

    protected URL getIndexHtmlUrl(){
        return getClass().getClassLoader().getResource("webpeerCore/index.html");
    }
}
