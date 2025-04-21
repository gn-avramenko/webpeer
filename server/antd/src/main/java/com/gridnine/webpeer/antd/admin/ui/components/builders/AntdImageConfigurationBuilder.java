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

import com.gridnine.webpeer.antd.admin.ui.components.image.AntdImageConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdContentConfiguration;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdImageConfigurationBuilder extends BaseAntdConfigurationBuilder<AntdImageConfiguration> {

    private AntdImageConfigurationBuilder(){
        super(new AntdImageConfiguration());
    }

    public void src(final String src) {
        config.setSrc(src);
    }

    public void height(final String height) {
        config.setHeight(height);
    }

    public void width(final String width) {
        config.setWidth(width);
    }

    public static AntdImageConfiguration createConfiguration(RunnableWithExceptionAndArgument<AntdImageConfigurationBuilder> configurator){
        var builder = new AntdImageConfigurationBuilder();
        return WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
            return builder.config;
        });
    }

}
