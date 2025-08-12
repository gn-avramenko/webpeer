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

package com.gridnine.webpeer.demo.app.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.WebPeerUtils;
import com.gridnine.webpeer.demo.app.data.DemoDataSource;

public class DemoMessagesArea extends BaseDemoUiElement<DemoMessagesAreaConfiguration> {

    private final DemoDataSource dataSource;

    private final Runnable messageListener = () -> {
        try {
            sendCommandAsync("refresh-messages", null);
        } catch (Throwable ex) {
            //noop
        }
    };

    public DemoMessagesArea(DemoDataSource dataSource, DemoMessagesAreaConfiguration config, OperationUiContext ctx) {
        super(config, ctx);
        this.dataSource = dataSource;
        this.dataSource.addChangeListener(messageListener);
    }

    @Override
    public void destroy() throws Exception {
        dataSource.removeChangeListener(messageListener);
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if("refresh-messages".equals(actionId)){
            sendElementPropertyChange(operationUiContext, "messages", getMessages());
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result = super.buildElement(context);
        result.add("messages", getMessages());
        return result;
    }

    private JsonArray getMessages() {
        var result = new JsonArray();
        dataSource.getMessages().forEach((message) -> {
            var item =new  JsonObject();
            item.addProperty("user", message.user());
            item.addProperty("message", message.message());
            result.add(item);
        });
        return result;
    }

    @Override
    public String getType() {
        return "demo-messages";
    }

}
