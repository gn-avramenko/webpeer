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

package com.gridnine.webpeer.antd.admin.ui.components.common;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.Collections;
import java.util.List;

public abstract class BaseAntdUiElement<T extends BaseAntdConfiguration> extends BaseUiElement {

    protected T configuration;

    private boolean initialized = false;

    public BaseAntdUiElement(T config, OperationUiContext ctx) {
        super(ctx);
        configuration = config;
        updateFromConfig();
    }

    public BaseAntdUiElement(JsonObject uiData, Object config, OperationUiContext ctx) {
        super(ctx);
        configuration = createConfiguration(uiData, config, ctx);
        updateFromConfig();
    }

    protected void updateFromConfig(){
        setTag(configuration.getTag());
        if(configuration.getChildren() != null) {
            configuration.getChildren().forEach(ch ->{
                if(ch.getParent() == null){
                    UiModel.addElement(ch, this);
                }
            });
        }
    }

    protected T createConfiguration(JsonObject uiData, Object config, OperationUiContext ctx) {
        return null;
    }

    public abstract String getType();

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        initialized = true;
        var result = super.buildElement(context);
        if(getType() != null) {
            result.addProperty("type", getType());
        }
        if(configuration.getStyle() != null && !configuration.getStyle().isEmpty()) {
            result.add("style", WebPeerUtils.serialize(configuration.getStyle()));
        }
        return result;
    }

    //прямая модификация возможна только в процессе инициализации дерева UI элементов
    @Override
    public List<BaseUiElement> getChildren() {
        return initialized? Collections.unmodifiableList(super.getChildren()): super.getChildren();
    }


}

