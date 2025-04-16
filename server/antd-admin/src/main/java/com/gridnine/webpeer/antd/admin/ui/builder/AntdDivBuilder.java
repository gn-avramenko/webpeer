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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AntdDivBuilder {

    private final AntdDiv div;

    private final OperationUiContext context;

    private static Map<String, Object> buildStyle(Style... styles) {
        var style = new HashMap<String, Object>();
        Arrays.stream(styles).forEach(style::putAll);
        return style;
    }

    public void style(Style... styles){
        div.setStyle(buildStyle(styles));
    }
    public AntdDivBuilder(AntdDiv div, OperationUiContext context) {
        this.div = div;this.context = context;
    }

    public void div(String content, Style... styles){
        var ch = new AntdDiv(null, context);
        ch.setStyle(buildStyle(styles));
        ch.setContent(content);
        UiModel.addElement(ch, div);
    }
    public void div(RunnableWithExceptionAndArgument<AntdDivBuilder> configurator, Style... styles){
        var ch = new AntdDiv(null, context);
        ch.setStyle(buildStyle(styles));
        WebPeerUtils.wrapException(() -> configurator.run(new AntdDivBuilder(ch, context)));
        UiModel.addElement(ch, div);
    }

    public void img(String src, String width, String height,  Style... styles){
        var ch = new AntdImage(context);
        ch.setStyle(buildStyle(styles));
        ch.setHeight(height);
        ch.setWidth(width);
        ch.setSrc(src);
        UiModel.addElement(ch, div);
    }

    public void dropdownIcon(RunnableWithExceptionAndArgument<AntdDropDownIconBuilder> configurator,Style... styles){
        var ch = new AntdDropDownIcon(context);
        ch.setStyle(buildStyle(styles));
        WebPeerUtils.wrapException(()->{
            configurator.run(new AntdDropDownIconBuilder(ch));
        });
        UiModel.addElement(ch, div);
    }

    public void dropdownImage(RunnableWithExceptionAndArgument<AntdDropDownImageBuilder> configurator, Style... styles){
        var ch = new AntdDropDownImage(context);
        ch.setStyle(buildStyle(styles));
        WebPeerUtils.wrapException(()->{
            configurator.run(new AntdDropDownImageBuilder(ch));
        });
        UiModel.addElement(ch, div);
    }

    public void hGlue(){
        div(c->{}, AntdAdminStyles.GLUE);
    }
}
