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

package com.gridnine.webpeer.antd.admin.ui.entitiesList;

public class AntdEntitiesListBuilder {
    private final AntdEntitiesList element;


    private AntdEntitiesListDataProvider dataProvider;

    public AntdEntitiesListBuilder(AntdEntitiesList element) {
        this.element = element;
    }

    public void column(String id, String name, AntdEntitiesListColumnType type, AntdEntitiesListColumnAlignment alignment, boolean sortable, Integer width) {
        var column = new AntdEntitiesListColumnDescription();
        column.setId(id);
        column.setName(name);
        column.setType(type);
        column.setSortable(sortable);
        column.setWidth(width);
        column.setAlignment(alignment);
        this.element.getColumns().add(column);
    }

    public void filter(String id, String name, AntdEntitiesListFilterType type) {
        var filter = new AntdEntitiesListFilterDescription();
        filter.setId(id);
        filter.setTitle(name);
        filter.setType(type);
        this.element.getFilters().add(filter);
    }

    public void linkColumn(String id,  String icon, Integer width) {
        var column = new AntdEntitiesListColumnDescription();
        column.setId(id);
        column.setType(AntdEntitiesListColumnType.LINK);
        column.setWidth(width);
        column.setIcon(icon);
        column.setAlignment(AntdEntitiesListColumnAlignment.RIGHT);
        this.element.getColumns().add(column);
    }

    public void title(String title) {
        this.element.setTitle(title);
    }

    public void limitStep(int step) {
        this.element.setLimitStep(step);
    }

    public void initSort(String propertyName, boolean desc) {
        var sort = new AntdSorting();
        sort.setColumn(propertyName);
        sort.setDirection(desc? AntdSortDirection.DESC: AntdSortDirection.ASC);
        this.element.setSort(sort);
    }

    public void dataProvider(AntdEntitiesListDataProvider provider) {
        this.element.setDataProvider(provider);
    }

}
