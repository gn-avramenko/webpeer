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

package com.gridnine.webpeer.demo.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.*;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public class DemoEntitiesList extends AntdEntitiesList {
    public DemoEntitiesList(String language) {
        super(b ->{
            b.column("stringProperty", "ru".equals(language)? "Строка": "String property", AntdEntitiesListColumnType.TEXT, true, null);
            b.column("numberProperty", "ru".equals(language)? "Число": "Number property", AntdEntitiesListColumnType.NUMBER, true, null);
            b.initSort("stringProperty", false);
            b.limitStep(50);
            b.title("ru".equals(language)? "Тестовый список": "Test entities list");
            b.dataProvider(((columns, limit, sort, searchText) -> {
                List<JsonObject> data = new ArrayList<JsonObject>();
                for(int n =0; n < 1000; n++){
                    var idx = sort.getDirection() == AntdSortDirection.ASC? n: 1000-n;
                    var stringProperty = String.format("%s - %s", "ru".equals(language)? "Строка": "String", idx);
                    if(WebPeerUtils.isNotBlank(searchText) && !stringProperty.toLowerCase().contains(searchText)){
                        continue;
                    }
                    var item = new JsonObject();
                    item.addProperty("id", String.valueOf(idx));
                    item.addProperty("stringProperty", stringProperty);
                    item.addProperty("numberProperty", idx);
                    data.add(item);
                }
                var hasMore = false;
                if(data.size() > limit){
                    data = data.subList(0, limit);
                    hasMore = true;
                }
                var result = new AntdListData();
                var arr = new JsonArray();
                data.forEach(arr::add);
                result.setData(arr);
                result.setHasMore(hasMore);
                return result;
            }));
        });
    }
}
