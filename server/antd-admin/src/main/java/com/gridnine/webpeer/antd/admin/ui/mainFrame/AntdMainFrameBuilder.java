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

package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.div.AntdDivBuilder;
import com.gridnine.webpeer.core.ui.GlobalUiContext;

import java.util.function.Consumer;

public class AntdMainFrameBuilder {

    private final AntdMainFrame frame;

    public AntdMainFrameBuilder(AntdMainFrame frame) {
        this.frame = frame;
    }

    public void menu(Consumer<AntdMenuBuilder> configurator){
        var menu = new AntdMainFrameMenu();
        var builder  = new AntdMenuBuilder(menu);
        configurator.accept(builder);
        frame.setMenu(menu, null);
    }

    public void theme(JsonObject theme){
        frame.setTheme(theme, null);
    }
    public void header(String style, Consumer<AntdDivBuilder> configurator){
        var header = new AntdDiv();
        header.setStyle(AntdUtils.parseStyle(style));
        configurator.accept(new AntdDivBuilder(header));
        frame.setHeader(header, null);
    }

    public void viewProvider(String path, AntdViewProvider viewProvider ){
        frame.setViewProvider(viewProvider);
        frame.setPath(path, null);
    }

}
