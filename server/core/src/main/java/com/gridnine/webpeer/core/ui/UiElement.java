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


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;


public interface UiElement {
    void setParent(UiElement parent);
    UiElement getParent();
    List<UiElement> getChildren();
    JsonElement serialize() throws Exception;
    default void destroy() throws Exception{
        //noops
    }
    void executeCommand(JsonObject command, OperationUiContext operationUiContext) throws Exception;
    long getId();
    default String getTag(){
        return null;
    }

    default UiElement findChildByTag(String tag){
        return  getChildren().stream().filter(it -> {
            return tag.equals(it.getTag());
        }).findFirst().orElse(null);
    }
}
