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
import com.gridnine.webpeer.core.ui.*;
import com.gridnine.webpeer.core.utils.CallableWithExceptionAnd2Arguments;
import com.gridnine.webpeer.core.utils.CallableWithExceptionAnd3Arguments;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class BaseWebAppServlet<T extends BaseUiElement> extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private volatile List<UiServletInterceptor<T>> interceptors;

    private volatile List<WebAppModule> allModules;

    public BaseWebAppServlet() {
        var timerTask = new TimerTask() {

            @Override
            public void run() {
                var now = Instant.now();
                GlobalUiContext.context.keySet().forEach(it -> {
                    var clients = new HashMap<>(GlobalUiContext.context.get(it));
                    clients.forEach((k, v) -> {
                        var upd = (Instant) v.get(GlobalUiContext.LAST_UPDATED.name);
                        if (upd == null || Duration.between(upd, now).getSeconds() > TimeUnit.HOURS.toSeconds(1)) {
                            GlobalUiContext.context.get(it).remove(k);
                            Session s = (Session) v.get(GlobalUiContext.WS_SESSION.name);
                            if (s != null) {
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
            initialize();
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
                doGetIndexHtml(resp);
                return;
            }
            if (pathInfo.equals("/fav.ico") || pathInfo.equals("/favicon.ico")|| pathInfo.equals("/favicon.svg")) {
                if (getFaviconUrl() != null) {
                    writeResource(getFaviconUrl(), resp);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (pathInfo.startsWith("/_resources/")) {
                if (pathInfo.startsWith("/_resources/classpath/")) {
                    var path = pathInfo.substring("/_resources/classpath/".length());
                    writeResource(Objects.requireNonNull(getClass().getClassLoader().getResource(path)), resp);
                    return;
                }
                String resourceName = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
                HtmlLinkWrapper linkWrapper = allModules.stream().flatMap(it -> it.css.stream())
                        .filter(it -> it.name.equals(resourceName)).findFirst().orElse(null);
                if (linkWrapper != null) {
                    writeResource(linkWrapper.url, resp);
                    return;
                }
                HtmlScriptWrapper scriptWrapper = allModules.stream().flatMap(it -> it.scripts.stream())
                        .filter(it -> it.name.equals(resourceName) || resourceName.equals(it.jsMapName)).findFirst().orElse(null);
                if (scriptWrapper != null) {
                    writeResource(resourceName.equals(scriptWrapper.name) ? scriptWrapper.url : scriptWrapper.jsMapUrl, resp);
                    return;
                }
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            doGetIndexHtml(resp);
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

    protected abstract T createRootElement(OperationUiContext operationUiContext) throws Exception;

    private JsonObject readData(HttpServletRequest req) throws IOException {
        try (var is = req.getInputStream()) {
            return new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OperationUiContext operationUiContext = new OperationUiContext();
        operationUiContext.setParameter(OperationUiContext.RESPONSE_COMMANDS, new JsonArray());
        operationUiContext.setParameter(OperationUiContext.POST_PROCESS_COMMANDS, new JsonArray());
        String pathInfo = req.getPathInfo();
        var statusCode = HttpServletResponse.SC_OK;
        var pi = URLEncoder.encode(pathInfo, StandardCharsets.UTF_8);
        var clientId = req.getHeader("x-client-id");
        Throwable error = null;
        RequestType requestType = null;
        JsonElement response = null;
        try {
            initialize();
            if (pathInfo.startsWith("/_ui")) {
                var queryString = req.getQueryString();
                if (queryString != null && queryString.contains("action=destroy")) {
                    requestType = RequestType.DESTROY;
                    JsonObject destroyData;
                    try (var is = req.getInputStream()) {
                        destroyData = new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
                    }
                    var piv = GlobalUiContext.context.get(pi);
                    if (piv != null) {
                        clientId = WebPeerUtils.getString(destroyData, "clientId");
                        var uiElements = GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.UI_ELEMENTS);
                        if (uiElements != null) {
                            uiElements.values().forEach(it -> {
                                try {
                                    it.destroy();
                                } catch (Throwable e) {
                                    logger.error("unable to destroy element", e);
                                }
                            });
                        }
                        piv.remove(clientId);
                    }
                    return;
                }
                if (queryString != null && queryString.contains("action=get-module-for-type")) {
                    requestType = RequestType.REQUEST;
                    JsonObject request = readData(req);
                    var elementType = WebPeerUtils.getString(request, "type");
                    var moduleId = findAdditionalModuleByElementType(elementType);
                    var module = allModules.stream().filter(it -> moduleId.equals(it.moduleId)).findFirst().get();
                    var command = new JsonObject();
                    command.addProperty("cmd", "load-module");
                    var scripts = new JsonArray();
                    module.scripts.forEach(it -> scripts.add(it.name));
                    command.add("scripts", scripts);
                    var css = new JsonArray();
                    module.css.forEach(it -> css.add(it.name));
                    command.add("css", css);
                    response = command;
                    return;
                }
                if (queryString != null && queryString.contains("action=ping")) {
                    requestType = RequestType.REQUEST;
                    response = new JsonObject();
                    if(pi != null && clientId != null) {
                        GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.LAST_UPDATED, Instant.now());
                    }
                    return;
                }
                if (queryString != null && queryString.contains("action=request")) {
                    requestType = RequestType.REQUEST;
                    var data = readData(req);
                    var  nodeId = WebPeerUtils.getLong(data, "id", 0);
                    var cmd = WebPeerUtils.getString(data, "cmd");
                    var cmdData = WebPeerUtils.getDynamic(data, "data");
                    var uiElements = GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.UI_ELEMENTS);
                    if (uiElements == null) {
                        uiElements = new ConcurrentHashMap<>();
                        GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.UI_ELEMENTS, uiElements);
                        GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.ELEMENT_INDEX_PROVIDER, new AtomicLong(-1));
                        GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.VERSION_PROVIDER, new AtomicInteger(-1));
                    }
                    operationUiContext.setParameter(OperationUiContext.PATH, pi);
                    operationUiContext.setParameter(OperationUiContext.CLIENT_ID, clientId);
                    operationUiContext.setParameter(OperationUiContext.REQUEST, req);
                    operationUiContext.setParameter(OperationUiContext.RESPONSE, resp);
                    var fuiElements = uiElements;
                    response = requestWithInterceptors(cmd, cmdData, operationUiContext, 0, (cmd2, cmdData2, ctx2)-> fuiElements.get(nodeId).doService(cmd2, cmdData2, ctx2));
                    return;
                }
                requestType = RequestType.COMMAND;
                List<JsonObject> requestCommands;
                try (var is = req.getInputStream()) {
                    //noinspection unchecked,rawtypes
                    requestCommands = (List) new Gson().fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonArray.class).asList();
                }
                var uiElements = GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.UI_ELEMENTS);
                if (uiElements == null) {
                    uiElements = new ConcurrentHashMap<>();
                    GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.UI_ELEMENTS, uiElements);
                    GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.ELEMENT_INDEX_PROVIDER, new AtomicLong(-1));
                    GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.VERSION_PROVIDER, new AtomicInteger(-1));
                }
                operationUiContext.setParameter(OperationUiContext.PATH, pi);
                operationUiContext.setParameter(OperationUiContext.CLIENT_ID, clientId);
                operationUiContext.setParameter(OperationUiContext.REQUEST, req);
                operationUiContext.setParameter(OperationUiContext.RESPONSE, resp);
                operationUiContext.setParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER, GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.ELEMENT_INDEX_PROVIDER));
                var firstCommand = requestCommands.get(0);
                if ("init".equals(WebPeerUtils.getString(firstCommand, "cmd")) && WebPeerUtils.isBlank(WebPeerUtils.getString(firstCommand, "id"))) {
                    var data = requestCommands.get(0).get("data").getAsJsonObject();
                    operationUiContext.setParameter(OperationUiContext.LOCAL_STORAGE_DATA, WebPeerUtils.getObject(data, "ls"));
                    operationUiContext.setParameter(OperationUiContext.PARAMS, WebPeerUtils.getObject(data, "params"));
                    var state = WebPeerUtils.getObject(data, "state");
                    if(state!=null){
                        operationUiContext.setParameter(OperationUiContext.INIT_STATE, state);
                    }
                    var rootElement =  initWithInterceptors(operationUiContext, state, 0, (ctx2, state2)->{
                        var result = createRootElement(ctx2);
                        if (state2 != null) {
                            result.restoreFromState(state2, ctx2);
                        }
                        return result;
                    });
                    GlobalUiContext.setParameter(pi, clientId, GlobalUiContext.LAST_UPDATED, Instant.now());
                    var command = new JsonObject();
                    command.addProperty("cmd", "init");
                    command.add("data", rootElement.buildState(operationUiContext));
                    var commands = operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS);
                    var resultCommands = new JsonArray();
                    resultCommands.add(command);
                    resultCommands.addAll(commands);
                    operationUiContext.setParameter(OperationUiContext.RESPONSE_COMMANDS, resultCommands);
                    return;
                } else {
                    long xVersion = Long.parseLong(req.getHeader("x-version"));
                    if (Objects.requireNonNull(GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.VERSION_PROVIDER)).get() != xVersion) {
                        var command = new JsonObject();
                        command.addProperty("cmd", "resync");
                        operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS).add(command);
                    } else {
                        var fUiElements = uiElements;
                        processCommandsWithInterceptors(requestCommands, operationUiContext, 0, (cmds2, ctx2) -> {
                            cmds2.forEach(cmdData -> WebPeerUtils.wrapException(() -> {
                                var elementId = Long.parseLong(Objects.requireNonNull(WebPeerUtils.getString(cmdData, "id")));
                                var commandId = Objects.requireNonNull(WebPeerUtils.getString(cmdData, "cmd"));
                                fUiElements.get(elementId).processCommand(ctx2, commandId, WebPeerUtils.getElement(cmdData, "data"));
                            }));
                        });
                    }
                }
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Throwable e) {
            logger.error("unable to handle request", e);
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            error = e;
        } finally {
            if(requestType == RequestType.DESTROY){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getOutputStream().close();
            } else {
                resp.setStatus(statusCode);
                resp.setHeader("Content-Type", "application/json");
                var result = new JsonObject();
                if(requestType == RequestType.COMMAND){
                    resp.setHeader("x-version", String.valueOf(Objects.requireNonNull(GlobalUiContext.getParameter(pi, clientId, GlobalUiContext.VERSION_PROVIDER)).incrementAndGet()));
                    var commands = operationUiContext.getParameter(OperationUiContext.RESPONSE_COMMANDS);
                    var postProcessCommands = operationUiContext.getParameter(OperationUiContext.POST_PROCESS_COMMANDS);
                    commands.addAll(postProcessCommands);
                    result.add("commands", commands);
                } else {
                    result.add("result", response);
                }
              if (error != null) {
                    var exc = new JsonObject();
                    WebPeerUtils.addProperty(exc, "message", error.getMessage());
                    WebPeerUtils.addProperty(exc, "stacktrace", WebPeerUtils.getExceptionStackTrace(error));
                    result.add("error", exc);
                }
                try (var os = resp.getOutputStream()) {
                    var jw = new JsonWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    new Gson().toJson(result, jw);
                    jw.flush();
                }
            }
        }
    }

    protected abstract List<WebAppModule> getAllModules() throws Exception;

    protected List<String> getBootstrapModulesIds() throws Exception {
        initialize();
        return allModules.stream().map(it -> it.moduleId).collect(Collectors.toList());
    }

    protected String findAdditionalModuleByElementType(String elementType) {
        throw new IllegalStateException("there are no additional modules");
    }

    protected abstract URL getFaviconUrl();

    protected void writeResource(URL url, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        if (url.toString().endsWith(".svg")) {
            resp.setHeader("Content-Type", "image/svg+xml");
        } else if (url.toString().endsWith(".js")) {
            resp.setHeader("Content-Type", "text/javascript");
        }
        try (var is = url.openStream()) {
            try (var os = resp.getOutputStream()) {
                is.transferTo(os);
                os.flush();
            }
        }
    }


    protected void doGetIndexHtml(HttpServletResponse resp) throws Exception {
        String content = getContent(getIndexHtmlUrl());
        content = content.replace("${title}", getTitle());
        String scripts = allModules.stream().flatMap(it -> it.scripts.stream())
                .map(it -> String.format("<script type=\"module\" src=\"_resources/%s\"></script>\n", it.name)).reduce("", (a, b) -> a + b);
        String links = allModules.stream().flatMap(it -> it.css.stream())
                .map(it -> String.format("<link rel=\"%s\" type=\"%s\" href=\"_resources/%s\"></link>\n", it.rel, it.type, it.name)).reduce("", (a, b) -> a + b);
        String parameters = getWebAppParameters().entrySet().stream()
                .map(it -> String.format("%s: \"%s\",\n", it.getKey(), it.getValue())).reduce("", (a, b) -> a + b);
        content = content.replace("${favicon}", getFaviconUrl() == null ? "" : "<link rel=\"icon\" type=\"image/x-icon\" href=\"%s\">".formatted(getFaviconUrl()));
        content = content.replace("${links}", links);
        content = content.replace("${scripts}", scripts);
        content = content.replace("${parameters}", String.format("<script>\nwindow.webPeer = {\nparameters: {\n%sinitPath:  window.location.pathname+(window.location.search? window.location.search: '')\n}\n}\n</script>", parameters));
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
        try (var is = indexHtmlUrl.openStream()) {
            is.transferTo(baos);
            baos.flush();
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    protected abstract Map<String, String> getWebAppParameters();

    protected abstract String getTitle();

    protected URL getIndexHtmlUrl() {
        return getClass().getClassLoader().getResource("webpeerCore/index.html");
    }

    enum RequestType {
        DESTROY,
        REQUEST,
        COMMAND
    }

    private JsonElement requestWithInterceptors(String commandId, JsonElement request, OperationUiContext context, int idx, CallableWithExceptionAnd3Arguments<JsonElement, String, JsonElement, OperationUiContext> callback) throws Exception {
        initialize();
        if (idx == interceptors.size()) {
            return callback.call(commandId, request, context);
        }
        return interceptors.get(idx).onRequest(commandId, request, context, (commandId2, request2, context2)->requestWithInterceptors(commandId2, request2, context2, idx+1, callback));
    }

    private T initWithInterceptors(OperationUiContext context, JsonObject state, int idx, CallableWithExceptionAnd2Arguments<T, OperationUiContext, JsonObject> callback) throws Exception {
        initialize();
        if (idx == interceptors.size()) {
            return callback.call(context, state);
        }
        return interceptors.get(idx).onInit(context, state, (ctx2, state2)->initWithInterceptors(ctx2, state2, idx+1, callback));
    }

    private void processCommandsWithInterceptors(List<JsonObject> commands, OperationUiContext context,  int idx, RunnableWithExceptionAndTwoArguments<List<JsonObject>, OperationUiContext> callback) throws Exception {
        initialize();
        if (idx == interceptors.size()) {
            callback.run(commands, context);;
            return;
        }
        interceptors.get(idx).onCommand(commands,context, (cmds2, ctx2)->processCommandsWithInterceptors(cmds2, ctx2, idx+1, callback));
    }

    private void initialize() throws Exception {
        if(interceptors == null){
            synchronized (this) {
                if(interceptors == null){
                    interceptors = getInterceptors();
                    allModules = getAllModules();
                }
            }
        }
    }


    protected List<UiServletInterceptor<T>> getInterceptors() {
        return Collections.emptyList();
    }
}

