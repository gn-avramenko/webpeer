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

package com.gridnine.webpeer.antd.admin.ui.builder;

import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.dropdown.AntdDropDownIcon;
import com.gridnine.webpeer.antd.admin.ui.components.dropdown.AntdDropDownImage;
import com.gridnine.webpeer.antd.admin.ui.components.image.AntdImage;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.Map;

public class AntdUiBuilder {

    public static AntdDiv div(OperationUiContext context, RunnableWithExceptionAndArgument<AntdDivBuilder> configurator){
        var result = new AntdDiv(context);
        var builder = new AntdDivBuilder(result, context);
        WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
        });
        return result;
    }

}
