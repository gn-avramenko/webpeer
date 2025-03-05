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

package com.gridnine.webpeer.antd.admin.ui.div;

import com.gridnine.webpeer.antd.admin.ui.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.dropdown.AntdDropDownIcon;
import com.gridnine.webpeer.antd.admin.ui.dropdown.AntdDropDownIconBuilder;
import com.gridnine.webpeer.antd.admin.ui.dropdown.AntdDropDownImage;
import com.gridnine.webpeer.antd.admin.ui.dropdown.AntdDropDownImageBuilder;
import com.gridnine.webpeer.antd.admin.ui.image.AntdImage;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.UiModel;

import java.util.function.Consumer;

public class AntdDivBuilder {

    private final AntdDiv div;

    public AntdDivBuilder(AntdDiv div) {
        this.div = div;
    }

    public void div(String style, String content){
        var ch = new AntdDiv();
        ch.setStyle(AntdUtils.parseStyle(style));
        ch.setContent(content);
        UiModel.addElement(ch, div);
    }
    public void div(String style, Consumer<AntdDivBuilder> configurator){
        var ch = new AntdDiv();
        ch.setStyle(AntdUtils.parseStyle(style));
        configurator.accept(new AntdDivBuilder(ch));
        UiModel.addElement(ch, div);
    }

    public void img(String src, String width, String height, String style){
        var ch = new AntdImage();
        ch.setStyle(AntdUtils.parseStyle(style));
        ch.setHeight(height);
        ch.setWidth(width);
        ch.setSrc(src);
        UiModel.addElement(ch, div);
    }

    public void dropdownIcon(String style, Consumer<AntdDropDownIconBuilder> configurator){
        var ch = new AntdDropDownIcon();
        ch.setStyle(AntdUtils.parseStyle(style));
        configurator.accept(new AntdDropDownIconBuilder(ch));
        UiModel.addElement(ch, div);
    }

    public void dropdownImage(String style, Consumer<AntdDropDownImageBuilder> configurator){
        var ch = new AntdDropDownImage();
        ch.setStyle(AntdUtils.parseStyle(style));
        configurator.accept(new AntdDropDownImageBuilder(ch));
        UiModel.addElement(ch, div);
    }

    public void hGlue(){
        div("flexGrow=1", c->{});
    }
}
