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
import com.gridnine.webpeer.antd.admin.ui.components.theme.AntdThemeConfiguration;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdThemeConfigurationBuilder extends BaseAntdConfigurationBuilder<AntdThemeConfiguration> {

    private AntdThemeConfigurationBuilder(){
        super(new AntdThemeConfiguration());
    }

    public void theme(JsonObject theme){
        config.setTheme(theme);
    }

    public static AntdThemeConfiguration createConfiguration(RunnableWithExceptionAndArgument<AntdThemeConfigurationBuilder> configurator){
        var builder = new AntdThemeConfigurationBuilder();
        return WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
            return builder.config;
        });
    }

}
