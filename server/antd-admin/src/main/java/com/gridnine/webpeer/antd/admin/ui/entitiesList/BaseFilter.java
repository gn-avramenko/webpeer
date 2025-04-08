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
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseFilter<T extends BaseUiElement> extends AntdDiv implements EntitiesListFilter {
    private final T element;

    public BaseFilter(T element, String labelText) {
        this.element = element;
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
        element.setParent(this);
        var elementStyle = new HashMap<String, Object>();
        elementStyle.put("width", "100%");
        setStyle(element, elementStyle);
        getChildren().add(element);
    }

    @Override
    public JsonElement get() {
        return getValue(this.element);
    }

    @Override
    public void clear(OperationUiContext context) {
        clear(this.element, context);
    }

    public abstract JsonElement getValue(T element);

    public abstract void clear(T element, OperationUiContext context);

    public abstract void setStyle(T element, Map<String, Object> style);

}
