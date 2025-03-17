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
import com.gridnine.webpeer.core.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AntdEntitiesList extends BaseUiElement implements UiElement {

    private long id;

    private JsonArray data = new JsonArray();

    private List<AntdEntitiesListColumnDescription> columns = new ArrayList<>();

    private String title;

    private boolean hasMore;

    private int limitStep = 50;

    private int limit = limitStep;

    private AntdSorting sort;

    private String searchText;

    private UiElement parent;

    private AntdEntitiesListDataProvider dataProvider;

    public AntdEntitiesList(Consumer<AntdEntitiesListBuilder> configurator) {
        id = GlobalUiContext.getParameter(GlobalUiContext.ELEMENT_INDEX_PROVIDER).incrementAndGet();
        var builder = new AntdEntitiesListBuilder(this);
        configurator.accept(builder);
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();

        result.addProperty("type", "entities-list");
        result.addProperty("id", String.valueOf(id));
        result.addProperty("title", title);
        result.addProperty("hasMore", hasMore);
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
        return List.of();
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
        if (actionId.equals("updateSearchText")) {
            searchText = actionData.getAsString();
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
        var data = dataProvider.getData(columns, limit, sort, searchText);
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
}
