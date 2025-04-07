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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.textField.AntdTextField;
import com.gridnine.webpeer.core.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AntdEntitiesList extends BaseUiElement implements UiElement {

    private final long id;

    private JsonArray data = new JsonArray();

    private final List<AntdEntitiesListColumnDescription> columns = new ArrayList<>();

    private final List<AntdEntitiesListFilterDescription> filters = new ArrayList<>();

    private String title;

    private int limitStep = 50;

    private int limit = limitStep;

    private AntdSorting sort;

    private UiElement parent;

    private AntdEntitiesListDataProvider dataProvider;

    private final AntdTextField searchField;

    private Map<String, EntitiesListFilter> filtersValues = new HashMap<>();

    private final List<UiElement> children = new ArrayList<>();

    public AntdEntitiesList(Consumer<AntdEntitiesListBuilder> configurator) {
        id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
        var builder = new AntdEntitiesListBuilder(this);
        configurator.accept(builder);
        searchField = new AntdTextField();
        searchField.setTag("search");
        searchField.setParent(this);
        getChildren().add(searchField);
        var filtersContent = new AntdDiv();
        filtersContent.setTag("filtersContent");
        filtersContent.setContent("Hello world");
        filtersContent.setParent(this);
        getChildren().add(filtersContent);

        var filtersFooter = new AntdDiv();
        filtersFooter.setTag("filtersFooter");
        filtersFooter.setContent("Hello footer");
        filtersFooter.setParent(this);
        getChildren().add(filtersFooter);
    }

    @Override
    public JsonElement serialize() throws Exception {
        {


//            this.filters.forEach(filter -> {
//                JsonObject filterJson = new JsonObject();
//                filterJson.addProperty("id", filter.getId());
//                filterJson.addProperty("title", filter.getTitle());
//                filterJson.addProperty("type", filter.getType().name());
//                filters.add(filterJson);
//                EntitiesListFilter f = null;
//                switch (filter.getType()){
//                    case STRING:{
//                        f = new TextEntitiesListFilter();
//                        break;
//                    }
//                }
//                f.setParent(this);
//                getChildren().add(f);
//                filtersValues.put(filter.getId(), f);
//            });
        }
        var result = (JsonObject) super.serialize();
        result.addProperty("type", "entities-list");
        result.addProperty("title", title);
        {
            JsonObject sort = new JsonObject();
            sort.addProperty("propertyName", this.sort == null ? "" : this.sort.getColumn());
            sort.addProperty("desc", this.sort != null && this.sort.getDirection() == AntdSortDirection.DESC);
            result.add("sort", sort);
        }
        {
            JsonArray columns = new JsonArray();
            this.columns.forEach(column -> {
                JsonObject columnJson = new JsonObject();
                columnJson.addProperty("id", column.getId());
                columnJson.addProperty("name", column.getName());
                columnJson.addProperty("type", column.getType().name());
                columnJson.addProperty("alignment", column.getAlignment().name());
                columnJson.addProperty("icon", column.getIcon());
                columnJson.addProperty("sortable", column.isSortable());
                if (column.getWidth() != null) {
                    columnJson.addProperty("width", column.getWidth());
                }
                columns.add(columnJson);
            });
            result.add("columns", columns);
        }

        result.add("data", data);
        return result;
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setParent(UiElement parent) {
        this.parent = parent;
    }

    @Override
    public UiElement getParent() {
        return parent;
    }

    @Override
    public List<UiElement> getChildren() {
        return children;
    }

    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) {
        if (actionId.equals("init")) {
            loadData(operationUiContext);
            return;
        }
        if (actionId.equals("increaseLimit")) {
            limit += limitStep;
            loadData(operationUiContext);
            return;
        }
        if (actionId.equals("changeSort")) {
            var as = actionData.getAsJsonObject();
            sort = new AntdSorting();
            sort.setDirection(as.get("desc").getAsBoolean() ? AntdSortDirection.DESC : AntdSortDirection.ASC);
            sort.setColumn(as.get("propertyName").getAsString());
            loadData(operationUiContext);
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    private void loadData(OperationUiContext operationUiContext) {
        var fv = new HashMap<String, JsonElement>();
        filtersValues.forEach((k,v) ->{
            fv.put(k, v.get());
        });
        var data = dataProvider.getData(columns, limit, sort, searchField.getValue(), fv);
        this.data = data.getData();
        var response = new JsonObject();
        response.add("data", data.getData());
        response.addProperty("hasMore", data.isHasMore());
        var jsort = new JsonObject();
        jsort.addProperty("propertyName", sort.getColumn());
        jsort.addProperty("desc", sort.getDirection() == AntdSortDirection.DESC);
        response.add("sort", jsort);
        operationUiContext.sendElementPropertyChange(id, "data", response);
    }

    public void setLimitStep(int limitStep) {
        this.limitStep = limitStep;
    }

    public List<AntdEntitiesListColumnDescription> getColumns() {
        return columns;
    }

    public void setSort(AntdSorting sort) {
        this.sort = sort;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDataProvider(AntdEntitiesListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public List<AntdEntitiesListFilterDescription> getFilters() {
        return filters;
    }
}
