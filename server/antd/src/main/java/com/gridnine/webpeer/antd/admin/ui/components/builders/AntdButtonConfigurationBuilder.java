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

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.button.AntdButton;
import com.gridnine.webpeer.antd.admin.ui.components.button.AntdButtonConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdButtonConfigurationBuilder extends BaseAntdConfigurationBuilder<AntdButtonConfiguration> {

    private AntdButtonConfigurationBuilder(){
        super(new AntdButtonConfiguration());
    }

    public void title(String title){
        config.setTitle(title);
    }

    public void clickHandler(RunnableWithExceptionAndArgument<OperationUiContext> clickHandler){
        config.setClickHandler(clickHandler);
    }

    public static AntdButtonConfiguration createConfiguration(RunnableWithExceptionAndArgument<AntdButtonConfigurationBuilder> configurator){
        var builder = new AntdButtonConfigurationBuilder();
        return WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
            return builder.config;
        });
    }

    public static AntdButton createElement(JsonObject uiData, OperationUiContext context, RunnableWithExceptionAndArgument<AntdButtonConfigurationBuilder> configurator){
        return new AntdButton(createConfiguration(configurator), context);
    }

}
