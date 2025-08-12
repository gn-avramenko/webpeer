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

import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiRootElement;
import com.gridnine.webpeer.demo.app.data.DemoDataSource;
import com.gridnine.webpeer.demo.app.data.Message;

public class DemoRootElement extends BaseDemoUiElement<DemoRootElementConfiguration> implements UiRootElement {

    private final DemoTextField userTextField;

    private final DemoMessagesArea demoMessagesArea;

    private final DemoTextField messageTextField;

    private final DemoButton sendButton;

    public DemoRootElement(JsonObject uiData, DemoDataSource demoDataSource, OperationUiContext ctx) {
        super(uiData, ctx);
        {
            var userNameConfig = new DemoTextFieldConfiguration(uiData == null? null: findUiChildData(uiData, "user"));
            userNameConfig.setDeferred(true);
            userNameConfig.setTag("user");
            userTextField = new DemoTextField(userNameConfig, ctx);
            addChild(ctx, userTextField, 0);
        }
        {
            var demoMessagesAreaConfig = new DemoMessagesAreaConfiguration();
            demoMessagesAreaConfig.setTag("messages");
            demoMessagesArea = new DemoMessagesArea(demoDataSource,  demoMessagesAreaConfig, ctx);
            addChild(ctx, demoMessagesArea, 0);
        }
        {
            var messageConfig = new DemoTextFieldConfiguration(uiData == null? null: findUiChildData(uiData, "message"));
            messageConfig.setDeferred(true);
            messageConfig.setTag("message");
            messageTextField = new DemoTextField(messageConfig, ctx);
            addChild(ctx, messageTextField, 0);
        }
        {
            var sendButtonConfig = new DemoButtonConfiguration();
            sendButtonConfig.setTitle("Send");
            sendButtonConfig.setTag("send-button");
            sendButtonConfig.setClickHandler((context) ->{
                var text = messageTextField.getValue();
                var user = userTextField.getValue();
                if(text != null && user != null) {
                    var message = new Message(user, text);
                    demoDataSource.addMessage(message);
                    messageTextField.setValue(null, context);
                }
            });
            sendButton = new DemoButton(sendButtonConfig, ctx);
            addChild(ctx, sendButton, 0);
        }

    }

    @Override
    public String getType() {
        return "root";
    }
}
