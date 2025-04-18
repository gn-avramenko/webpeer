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

package com.gridnine.webpeer.antd.admin.ui.components.menu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.image.AntdImageConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public class AntdMenu extends BaseAntdUiElement<AntdMenuConfiguration> {


    public AntdMenu(AntdMenuConfiguration config, OperationUiContext ctx) {
        super(config, ctx);
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result = super.buildElement(context);
        var menu = new JsonArray();
        result.add("menuItems", menu);
        configuration.getMenu().forEach(g -> {
            var gi = new JsonObject();
            menu.add(gi);
            gi.addProperty("name", g.getName());
            if(g.getIcon() != null){
                gi.addProperty("icon", g.getIcon());
            }
            if(!g.getChildren().isEmpty()){
                var children = new JsonArray();
                gi.add("children", children);
                g.getChildren().forEach(c ->{
                    var item = new JsonObject();
                    item.addProperty("name", c.getName());
                    if(c.getIcon() != null) {
                        item.addProperty("icon", c.getIcon());
                    }
                    children.add(item);
                });
            }
        });
        return result;
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if("click".equals(actionId)) {
            var itemId = actionData.getAsString();
            String[] items = itemId.split("-");
            WebPeerUtils.wrapException(() ->{
                configuration.getMenu().get(Integer.parseInt(items[0])).getChildren().get(Integer.parseInt(items[1])).getHandler().run(operationUiContext);
            });
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    @Override
    public String getType() {
        return "menu";
    }
}
