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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.BaseUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;

public abstract class BaseDemoUiElement<T extends BaseDemoElementConfiguration> extends BaseUiElement {

    protected T configuration;

    public BaseDemoUiElement(JsonObject uiData, OperationUiContext ctx) {
        super(ctx);
        configuration = createConfiguration(uiData, ctx);
        updateFromConfig();
    }

    public BaseDemoUiElement(T configuration, OperationUiContext ctx) {
        super(ctx);
        this.configuration = configuration;
        updateFromConfig();
    }

    protected void updateFromConfig(){
        setTag(configuration == null? null: configuration.getTag());
    }

    protected T createConfiguration(JsonElement uiData, OperationUiContext ctx) {
        return null;
    }

    public abstract String getType();

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result  = super.buildElement(context);
        result.addProperty("type", getType());
        return result;
    }

    public JsonElement findUiChildData(JsonElement uiData, String tag){
        if(uiData == null || !uiData.isJsonObject()){
            return null;
        }
        return uiData.getAsJsonObject().getAsJsonArray("children").asList().stream().filter(it ->
            it.isJsonObject() && it.getAsJsonObject().has("tag") && it.getAsJsonObject().get("tag").getAsString().equals(tag)
        ).findFirst().orElse(null);
    }
}

