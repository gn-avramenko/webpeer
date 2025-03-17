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

package com.gridnine.webpeer.antd.admin.ui.components.image;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntdImage implements UiElement {

    private Map<String,Object> style = new HashMap<>();

    private final long id;

    private String width;

    private String height;

    private String src;

    private UiElement parent;

    public AntdImage() {
        this.id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public void setStyleProperty(String property, Object value){
        style.put(property, value);
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public void setParent(UiElement parent) {
        this.parent = parent;
    }

    @Override
    public UiElement getParent() {
        return parent;
    }

    @Override
    public List<UiElement> getChildren() {
        return List.of();
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("type", "img");
        result.addProperty("id", String.valueOf(id));
        result.add("style", WebPeerUtils.serialize(style));
        result.addProperty("width", this.width);
        result.addProperty("height", this.height);
        result.addProperty("src", String.format("/_resources/classpath/%s", src));
        return result;
    }

    @Override
    public void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception {

    }

    @Override
    public long getId() {
        return id;
    }
}
