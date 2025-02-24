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

import com.gridnine.webpeer.core.ui.UiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.ui.UiNode;

import java.util.Map;

public class AntdMainFrame implements UiElement {

    private AntdMainFrameMenu menu = new AntdMainFrameMenu();

    private UiElement header;

    public void setMenu(AntdMainFrameMenu menu) {
        this.menu = menu;
        //TODO update node
    }

    public void setHeader(UiElement header) {
        this.header = header;
        //TODO update node
    }

    @Override
    public UiNode createNode(Map<String, Object> context) throws Exception {
        var result = new UiNode("root", "root", 0);
        result.properties.put("menu", menu, true);
        UiModel root = (UiModel) context.get(UiContext.UI_MODEL);
        if(header != null) {
            result.children.add(header.createNode(context));
        }
        return result;
    }
}
