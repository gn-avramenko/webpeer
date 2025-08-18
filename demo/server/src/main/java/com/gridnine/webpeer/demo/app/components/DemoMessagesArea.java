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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoMessagesArea extends BaseDemoUiElement {

    private final DemoDataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(DemoMessagesArea.class);

    private final Runnable messageListener = () -> {
        try {
            notify("refresh-messages", null);
        } catch (Throwable ex) {
            logger.error("unable to refresh messages", ex);
        }
    };

    public DemoMessagesArea(String tag, DemoDataSource dataSource,  OperationUiContext ctx) {
        super("demo-messages", tag, new String[0], new String[]{"messages"},ctx);
        this.dataSource = dataSource;
        this.dataSource.addChangeListener(messageListener);
        setProperty("messages", getMessages(), ctx);
    }


    @Override
    public void destroy() {
        dataSource.removeChangeListener(messageListener);
    }

    @Override
    public void processCommand(OperationUiContext ctx, String commandId, JsonElement data) throws Exception {
        if("refresh-messages".equals(commandId)){
            setProperty("messages", getMessages(), ctx);
            return;
        }
        super.processCommand(ctx, commandId, data);
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

}
