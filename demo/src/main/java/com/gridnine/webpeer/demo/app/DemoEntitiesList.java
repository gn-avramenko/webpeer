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

import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesList;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesListColumnAlignment;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesListColumnType;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesListFilterType;

public class DemoEntitiesList extends AntdEntitiesList {

    public DemoEntitiesList(String language, DemoDataSource dataSource) {
        super(b ->{
            b.language(language);
            b.column("stringProperty", "ru".equals(language)? "Строка": "String property", AntdEntitiesListColumnType.TEXT, AntdEntitiesListColumnAlignment.LEFT, true, null);
            b.column("numberProperty", "ru".equals(language)? "Число": "Number property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.RIGHT,true, null);
            b.column("dateProperty", "ru".equals(language)? "Дата": "Date property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.column("enumProperty", "ru".equals(language)? "Перечисление": "Enum property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.column("entityRefProperty", "ru".equals(language)? "Сущность": "Entity property", AntdEntitiesListColumnType.TEXT,  AntdEntitiesListColumnAlignment.LEFT,true, null);
            b.filter("stringProperty", "ru".equals(language)? "Поиск по строке": "String search", AntdEntitiesListFilterType.STRING);
            b.filter("numberProperty", "ru".equals(language)? "Поиск по числу": "Number search", AntdEntitiesListFilterType.NUMBER_INTERVAL);
            b.linkColumn("linkProperty", "RightOutlined", 10);
            b.initSort("stringProperty", false);
            b.limitStep(50);
            b.title("ru".equals(language)? "Тестовый список": "Test entities list");
            b.dataProvider(dataSource);
        });
    }
}
