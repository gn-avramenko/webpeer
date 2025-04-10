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

package com.gridnine.webpeer.core.ui;

import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UiModel{
    private BaseUiElement rootElement;

    private final Map<Long, BaseUiElement> elements = new ConcurrentHashMap<>();

    public static void removeElement(BaseUiElement element) {
        if(element.getParent() != null){
            element.getParent().getChildren().remove(element);
        }
        UiModel model = findModel(element);
        if(model == null){
            return;
        }
        removeElement(model, element);
    }

    public static void upsertElement(BaseUiElement element, BaseUiElement parent) {
        if(parent.getChildren().stream().anyMatch(it ->it.getId() == element.getId())){
            return;
        }
        var existing = element.getTag() != null? parent.getChildren().stream().filter(it -> element.getTag().equals(it.getTag())).findFirst().orElse(null): null;
        if(existing != null){
            removeElement(existing);
        }
        element.setParent(parent);
        parent.getChildren().add(element);
        UiModel model = findModel(element);
        if(model == null){
            return;
        }
        addElement(model, element);
    }

    public static void addElement(BaseUiElement element, BaseUiElement parent) {
        if(parent == null){
            return;
        }
        element.setParent(parent);
        parent.getChildren().add(element);
        UiModel model = findModel(element);
        if(model == null){
            return;
        }
        addElement(model, element);
    }

    private static void addElement(UiModel model, BaseUiElement element) {
        WebPeerUtils.wrapException(() ->{
            model.elements.put(element.getId(), element);
            element.getChildren().forEach((ch) ->{
                addElement(model, ch);
            });
        });
    }


    private static void removeElement(UiModel model, BaseUiElement element) {
        WebPeerUtils.wrapException(() ->{
            element.destroy();
            model.elements.remove(element.getId());
            element.getChildren().forEach((ch) ->{
                removeElement(model, ch);
            });
        });
    }

    private static UiModel findModel(BaseUiElement element) {
        if(element instanceof RootUiElement){
            return ((RootUiElement) element).getModel();
        }
        if(element.getParent() == null){
            return null;
        }
        return findModel(element.getParent());
    }

    public BaseUiElement getRootElement() {
        return rootElement;
    }

    public void setRootElement(BaseUiElement rootElement) {
        if(this.rootElement != null){
            removeElement(this, this.rootElement);
        }
        this.rootElement = rootElement;
        addElement(this, rootElement);
    }

    public BaseUiElement findElement(long id) {
        return elements.get(id);
    }

}
