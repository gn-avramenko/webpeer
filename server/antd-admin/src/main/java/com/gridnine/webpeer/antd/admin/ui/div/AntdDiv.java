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

import com.gridnine.webpeer.antd.admin.ui.common.AntdStyle;
import com.gridnine.webpeer.core.ui.UiContext;
import com.gridnine.webpeer.core.ui.UiElement;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.ui.UiNode;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AntdDiv implements UiElement {

    private final AntdStyle style = new AntdStyle();

    private String content;

    private final String id;

    private List<UiElement> children = new ArrayList<UiElement>();

    public AntdDiv(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStyle(String property, Object value){
        style.put(property, value);
    }

    public List<UiElement> getChildren() {
        return children;
    }

    public void setChildren(List<UiElement> children) {
        this.children = children;
    }


    @Override
    public UiNode createNode(Map<String, Object> context) throws Exception {
        UiModel root = (UiModel) context.get(UiContext.UI_MODEL);
        var result = new UiNode(id, "div", root.elementIndex.incrementAndGet());
        style.forEach((k,v) ->{
            result.properties.put(String.format("style:%s",k), v);
        });
        if(WebPeerUtils.isNotBlank(content)){
            result.properties.put("content", content);
            result.children.clear();
        } else {
            children.forEach( ch ->{
                WebPeerUtils.wrapException(() ->{
                    result.children.add(ch.createNode(context));
                });
            });
        }
        return result;
    }
}
