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

package com.gridnine.webpeer.antd.admin.ui.dropdown;

import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithException;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;

public class AntdDropDownImageBuilder {

    private final AntdDropDownImage element;

    public AntdDropDownImageBuilder(AntdDropDownImage image) {
        this.element = image;
    }

    public void menuItem(String id, String image, String name, String width, String height, RunnableWithExceptionAndArgument<OperationUiContext> handler){
        var item = new ImageMenuItem();
        item.setId(id);
        item.setImage(image);
        item.setName(name);
        item.setImageWidth(width);
        item.setImageHeight(height);
        item.setOnClick(handler);
        element.getMenu().add(item);
    }

    public void selectItem(String id){
        element.setSelectedItemId(id);
    }


}
