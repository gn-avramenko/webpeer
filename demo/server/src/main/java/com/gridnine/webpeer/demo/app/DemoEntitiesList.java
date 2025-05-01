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

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.builders.AntdEntitiesListConfigurationBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.builders.AntdDivConfigurationBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.table.AntdTableColumnAlignment;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesList;
import com.gridnine.webpeer.core.ui.OperationUiContext;

public class DemoEntitiesList extends AntdEntitiesList {
    public DemoEntitiesList(JsonObject uidData, String language, OperationUiContext context, final DemoDataSource dataSource) {
        super(AntdEntitiesListConfigurationBuilder.build(b ->{
            b.title("en".equals(language)? "Entities list": "Список сущностей");
            b.standardColumn("stringProperty", "ru".equals(language)? "Строка": "String property", true, AntdTableColumnAlignment.LEFT, null);
            b.standardColumn("numberProperty", "ru".equals(language)? "Число": "Number property", true, AntdTableColumnAlignment.RIGHT, null);
            b.standardColumn("dateProperty", "ru".equals(language)? "Дата": "Date property", true, AntdTableColumnAlignment.LEFT, null);
            b.standardColumn("enumProperty", "ru".equals(language)? "Перечисление": "Enum property", true, AntdTableColumnAlignment.LEFT, null);
            b.standardColumn("entityRefProperty","ru".equals(language)? "Сущность": "Entity property", true, AntdTableColumnAlignment.LEFT, null);
            b.customColumn("link", "ru".equals(language)? "Детали": "Details", AntdTableColumnAlignment.LEFT, 80, (data, ctx) ->{
                return AntdDivConfigurationBuilder.createElement(null, ctx, db->{
                    db.icon(ctx, i->i.icon(AntdIcons.RIGHT_CIRCLE_OUTLINED.name()));
                    db.style("cursor=pointer");
                    db.clickHandler((c)->{
                        System.out.println(data.get("id"));
                    });
                });
            });
            b.initSort("stringProperty", false);
            b.dataProvider(((fields, limit, sort, searchText, filters) -> {
                Thread.sleep(1000L);
                return dataSource.getData(language, limit, sort, searchText, filters);
            }));
        }), uidData, context);
    }
}
