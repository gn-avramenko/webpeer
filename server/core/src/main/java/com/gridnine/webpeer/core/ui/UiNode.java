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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.utils.TypedParameter;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class UiNode {
    public final String type;
    public final long index;
    public final String id;
    public final TrackedProperties properties;
    private UiNode parent;
    public final TrackedChildren children;

    public UiNode(String id, String type, long index) {
        this.id = id;
        this.type = type;
        this.index = index;
        children = new TrackedChildren(this);
        properties = new TrackedProperties(this);
    }

    public void setParent(UiNode parent) {
        if(parent == null){
            if(this.parent != null){
                this.parent.children.remove(this);
            }
        } else {
            if (this.parent != null && this.parent != parent ){
                this.parent.children.remove(this);
            }
            parent.children.add(this);
        }

        this.parent = parent;
    }

    public UiNode getParent() {
        return parent;
    }

    public <T> T getProperty(TypedParameter<T> name) {
        return (T) properties.get(name.name);
    }

    public <T> void setProperty(TypedParameter<T> name, T value) {
        properties.put(name.name, value);
    }

    public JsonElement serialize() throws Exception{
        var result= new JsonObject();
        result.addProperty("type", type);
        result.addProperty("index", index);
        result.addProperty("id", id);
        if(!properties.isEmpty()){
            JsonObject props = new JsonObject();
            properties.forEach((k,v)->{
                if(v != null){
                    WebPeerUtils.wrapException(()->{
                        JsonObject elm = new JsonObject();
                        if(v instanceof String){
                            props.addProperty(k, (String) v);
                        } else if(v instanceof Long){
                            props.addProperty(k, (long) v);
                        } else {
                            var s = (GsonSerializable) v;
                            props.add(k, s.serialize());
                        }
                    });
                }
            });
            result.add("properties", props);
        }
        if(!children.isEmpty()){
            JsonArray cd = new JsonArray();
            children.forEach((c)->{
                WebPeerUtils.wrapException(()->{
                    cd.add(c.serialize());
                });
            });
            result.add("children", cd);
        }
        return result;
    }
}
