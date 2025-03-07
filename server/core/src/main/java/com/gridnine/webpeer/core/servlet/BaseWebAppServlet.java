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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.utils.WebPeerUtils;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseWebAppServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public BaseWebAppServlet(){
        var timerTask = new TimerTask() {

            @Override
            public void run() {
                var now = Instant.now();
                GlobalUiContext.context.keySet().forEach(it -> {
                   var clients = new HashMap<>(GlobalUiContext.context.get(it));
                   clients.forEach((k, v) -> {
                       var upd = (Instant)v.get(GlobalUiContext.LAST_UPDATED.name);
                       if(upd == null || Duration.between(upd, now).getSeconds() > TimeUnit.HOURS.toSeconds(1)){
                           GlobalUiContext.context.get(it).remove(k);
                           Session s = (Session) v.get(GlobalUiContext.WS_SESSION.name);
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
            if (pathInfo.equals("/fav.ico") || pathInfo.equals("/favicon.ico")) {
                if (getFaviconUrl() != null) {
                    writeResource(getFaviconUrl(), resp);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (pathInfo.startsWith("/_resources/")) {
                if(pathInfo.startsWith("/_resources/classpath/")){
                    var path = pathInfo.substring("/_resources/classpath/".length());
                    writeResource(getClass().getClassLoader().getResource(path), resp);
                    return;
                }
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
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Throwable e){
            throw  new ServletException(e);
        }
    }

    protected abstract UiElement createRootElement(OperationUiContext operationUiContext) throws Exception;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo.startsWith("/_ui")) {
                List<JsonObject> requestCommands;
                try(var is = req.getInputStream()){
                    requestCommands = (List) new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonArray.class).asList();
                }
                String clientId = req.getHeader("x-client-id");
                var pi = URLEncoder.encode(pathInfo, StandardCharsets.UTF_8);
                GlobalUiContext.setOperationContext(pi, clientId);
                try {
                    GlobalUiContext.setParameter(GlobalUiContext.LAST_UPDATED, Instant.now());
                    var model = GlobalUiContext.getParameter(GlobalUiContext.UI_MODEL);
                    if (model == null) {
                        model = new UiModel();
                        GlobalUiContext.setParameter(GlobalUiContext.UI_MODEL, model);
                        GlobalUiContext.setParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER, new AtomicLong(-1));
                        GlobalUiContext.setParameter(GlobalUiContext.VERSION_PROVIDER, new AtomicInteger(-1));
                    }
                    OperationUiContext operationUiContext = new OperationUiContext();
                    operationUiContext.setParameter(OperationUiContext.RESPONSE_COMMANDS, new JsonArray());
                    operationUiContext.setParameter(OperationUiContext.REQUEST, req);
                    if(requestCommands.get(0).get("cmd").getAsString().equals("init")){
                        var data = requestCommands.get(0).get("data").getAsJsonObject();
                        operationUiContext.setParameter(OperationUiContext.LOCAL_STORAGE_DATA, data.get("ls").getAsJsonObject());
                        model.setRootElement(createRootElement(operationUiContext));
                        var command = new JsonObject();
                        command.addProperty("cmd", "init");
                        command.add("data", model.getRootElement().serialize());
                        operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
                    } else {
                        long xVersion = Long.parseLong(req.getHeader("x-version"));
                        if (GlobalUiContext.getParameter(GlobalUiContext.VERSION_PROVIDER).get() != xVersion) {
                            var command = new JsonObject();
                            command.addProperty("cmd", "resync");
                            operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
                        } else {
                            var fModel = model;
                            requestCommands.forEach(cmdData -> WebPeerUtils.wrapException(() ->{
                                var rc = WebPeerUtils.getString(cmdData, "cmd");
                                if("ec".equals(rc)){
                                    var elementId = Long.parseLong(WebPeerUtils.getString(cmdData, "id"));
                                    fModel.findElement(elementId).executeCommand(cmdData.get("data").getAsJsonObject(), operationUiContext);
                                }
                            }));
                        }
                    }
                    resp.setHeader("x-version", String.valueOf(GlobalUiContext.getParameter(GlobalUiContext.VERSION_PROVIDER).incrementAndGet()));
                    resp.setHeader("Content-Type", "application/json");
                    try (var os = resp.getOutputStream()) {
                        var jw = new JsonWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                        new Gson().toJson(operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS), jw);
                        jw.flush();
                    }
                    resp.setStatus(HttpServletResponse.SC_OK);
                } finally {
                    GlobalUiContext.clearOperationContext();
                }
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Throwable e){
            throw  new ServletException(e);
        }
    }

    protected abstract List<WebAppModule> getModules() throws Exception;

    protected abstract URL getFaviconUrl();

    protected void writeResource(URL url, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        if(url.toString().endsWith(".svg")) {
            resp.setHeader("Content-Type", "image/svg+xml");
        }
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
        content = content.replace("${parameters}", String.format("<script>\nwindow.webPeer = {parameters: {\n%s\n}\n}\n</script>", parameters));
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

    protected URL getIndexHtmlUrl(){
        return getClass().getClassLoader().getResource("webpeerCore/index.html");
    }
}
