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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DemoEntitiesList extends AntdEntitiesList {
    private final String lang;


    public DemoEntitiesList(String language) {
        super(b ->{
            b.column("stringProperty", "ru".equals(language)? "Строка": "String property", AntdEntitiesListColumnType.TEXT, AntdEntitiesListColumnAlignment.LEFT, true, null);
            b.column("numberProperty", "ru".equals(language)? "Число": "Number property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.RIGHT,true, null);
            b.column("dateProperty", "ru".equals(language)? "Дата": "Date property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.column("enumProperty", "ru".equals(language)? "Перечисление": "Enum property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.column("entityRefProperty", "ru".equals(language)? "Сущность": "Entity property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.linkColumn("linkProperty", "RightOutlined", 10);
            b.initSort("stringProperty", false);
            b.limitStep(50);
            b.title("ru".equals(language)? "Тестовый список": "Test entities list");
            b.dataProvider( new AntdEntitiesListDataProvider(){

                private List<JsonObject> testData = new ArrayList<>();

                private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                {
                    for(int n =0; n < 1000; n++){
                        var stringProperty = String.format("%s - %s", "ru".equals(language)? "Строка": "String", n);
                        var item = new JsonObject();
                        item.addProperty("id", String.valueOf(n));
                        item.addProperty("stringProperty", stringProperty);
                        item.addProperty("numberProperty", String.valueOf(n));
                        item.addProperty("dateProperty", LocalDate.ofInstant(Instant.ofEpochMilli(Math.round(Math.random() *Instant.now().toEpochMilli())), ZoneId.systemDefault()).format(dtf));
                        item.addProperty("enumProperty", Math.random()> 0.5? DemoEnum.ITEM2.toString(): DemoEnum.ITEM1.toString());
                        item.addProperty("entityRefProperty", Math.random()> 0.5? "Entity 2" : "Entity 1");
                        testData.add(item);
                    }
                }
                @Override
                public AntdListData getData(List<AntdEntitiesListColumnDescription> columns, int limit, AntdSorting sort, String searchText) {
                    var data = testData.stream().filter(it ->{
                        if(WebPeerUtils.isBlank(searchText)){
                            return true;
                        }
                        return it.get("stringProperty").getAsString().toLowerCase().contains(searchText.toLowerCase());
                    }).sorted((a,b) -> {
                        var str1 = a.get(sort.getColumn()).getAsString();
                        var str2 = b.get(sort.getColumn()).getAsString();
                        return sort.getDirection() == AntdSortDirection.DESC? str2.compareTo(str1) : str1.compareTo(str2);
                    }).toList();
                    var result = new AntdListData();
                    var arr = new JsonArray();
                    if(data.size() > limit) {
                        data = data.subList(0, limit);
                        result.setHasMore(true);
                    }
                    data.forEach(arr::add);
                    result.setData(arr);
                    return result;
                }
            });
        });
        lang = language;

    }
}
