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

package com.gridnine.webpeer.antd.admin.ui.components.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdDrawerConfiguration;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdDrawerConfigurationBuilder extends BaseAntdConfigurationBuilder<AntdDrawerConfiguration> {

    public void open(boolean open){
        config.setOpen(open);
    }

    public void title(String title){
        config.setTitle(title);
    }

    public void placement(String placement){
        config.setPlacement(placement);
    }

    public void closeIcon(String icon){
        config.setCloseIcon(icon);
    }

    public void bodyStyle(JsonObject bodyStyle){
        config.setBodyStyle(bodyStyle);
    }

    public void headerStyle(JsonObject headerStyle) {
        config.setHeaderStyle(headerStyle);
    }

    public void footer(BaseAntdUiElement<?> footer) {
        config.setFooter(footer);
    }

    public void noContainer(){
        config.setGetContainer(new JsonPrimitive(false));
    }
    public void body(BaseAntdUiElement<?> body) {
        config.setBody(body);
    }

    private AntdDrawerConfigurationBuilder(){
        super(new AntdDrawerConfiguration());
    }

    public static AntdDrawerConfiguration createConfiguration(RunnableWithExceptionAndArgument<AntdDrawerConfigurationBuilder> configurator){
        var builder = new AntdDrawerConfigurationBuilder();
        return WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
            return builder.config;
        });
    }

}
