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

package com.gridnine.webpeer.antd.admin.ui.entitiesList;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class BaseIntervalFilter<T extends BaseUiElement> extends AntdDiv implements EntitiesListFilter {
    private final T startElement;

    private final T endElement;

    public BaseIntervalFilter(Supplier<T> elementFactory, String labelText,String startText,String endText) {
        var containerStyle = new HashMap<String, Object>();
        containerStyle.put("padding", "token:padding");
        setStyle(containerStyle);
        var label = new AntdDiv();
        label.setParent(this);
        var labelStyle = new HashMap<String, Object>();
        labelStyle.put("padding", "5px");
        label.setStyle(labelStyle);
        label.setContent(labelText);
        this.getChildren().add(label);
        this.startElement = createDiv(elementFactory, startText);
        this.endElement = createDiv(elementFactory, endText);
    }

    private T createDiv(Supplier<T> elementFactory, String labelText) {
        var div = new AntdDiv();
        div.setParent(this);
        this.getChildren().add(div);
        var divStyle = new HashMap<String, Object>();
        divStyle.put("width", "100%");
        divStyle.put("display", "flex");
        divStyle.put("flexDirection", "row");
        divStyle.put("paddingTop", "5px");
        div.setStyle(divStyle);
        var label = new AntdDiv();
        label.setParent(div);
        div.getChildren().add(label);
        var labelStyle = new HashMap<String, Object>();
        labelStyle.put("flexGrow", 0);
        label.setStyle(labelStyle);;
        var result = elementFactory.get();
        result.setParent(div);
        div.getChildren().add(label);
        var element = elementFactory.get();
        element.setParent(div);
        div.getChildren().add(element);
        var elementStyle = new HashMap<String, Object>();
        elementStyle.put("flexGrow", 1);
        elementStyle.put("display", "inline-block");
        setStyle(element, elementStyle);
        return element;
    }

    @Override
    public JsonElement get() {
        var start =  getValue(this.startElement);
        var end =  getValue(this.startElement);
        var res = new JsonObject();
        res.add("start", start);
        res.add("end", end);
        return res;
    }

    @Override
    public void clear(OperationUiContext context) {
        clear(this.startElement, context);
        clear(this.endElement, context);
    }

    public abstract JsonElement getValue(T element);

    public abstract void clear(T element, OperationUiContext context);

    public abstract void setStyle(T element, Map<String, Object> style);

}
