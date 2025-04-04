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

package com.gridnine.webpeer.antd.admin.ui.components.div;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AntdDiv implements UiElement {

    private Map<String,Object> style = new HashMap<>();

    private String content;

    private final long id;

    private UiElement parent;

    private List<UiElement> children = new ArrayList<UiElement>();

    public AntdDiv() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStyleProperty(String property, Object value){
        style.put(property, value);
    }


    @Override
    public void setParent(UiElement parent) {
        this.parent = parent;
    }

    @Override
    public UiElement getParent() {
        return parent;
    }

    public List<UiElement> getChildren() {
        return children;
    }

    public void setChildren(List<UiElement> children) {
        this.children = children;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }
    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("id", String.valueOf(id));
        result.addProperty("type", "div");
        result.add("style", WebPeerUtils.serialize(style));
        if(WebPeerUtils.isNotBlank(content)){
            result.addProperty("content", content);
        } else {
            var chs = new JsonArray();
            result.add("children", chs);;
            children.forEach( ch ->{
                WebPeerUtils.wrapException(() -> chs.add(ch.serialize()));
            });
        }
        return result;
    }

    @Override
    public void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception {
        //noops
    }

    @Override
    public long getId() {
        return id;
    }
}
