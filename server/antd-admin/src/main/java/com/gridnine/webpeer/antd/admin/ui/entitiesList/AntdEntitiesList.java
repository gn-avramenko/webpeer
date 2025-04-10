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
import com.gridnine.webpeer.antd.admin.ui.components.button.AntdButton;
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

    private JsonArray data = new JsonArray();

    private final List<AntdEntitiesListColumnDescription> columns = new ArrayList<>();

    private final List<AntdEntitiesListFilterDescription> filters = new ArrayList<>();

    private String title;

    private int limitStep = 50;

    private int limit = limitStep;

    private AntdSorting sort;

    private AntdEntitiesListDataProvider dataProvider;

    private final AntdTextField searchField;

    private Map<String, EntitiesListFilter> filtersValues = new HashMap<>();

    private String language;

    public AntdEntitiesList(Consumer<AntdEntitiesListBuilder> configurator) {
        var builder = new AntdEntitiesListBuilder(this);
        configurator.accept(builder);
        searchField = new AntdTextField();
        searchField.setTag("search");
        searchField.setParent(this);
        searchField.setDebounceTime(300);
        searchField.setOnValueChanged((value,context) ->{
            loadData(context);
        });
        getChildren().add(searchField);

        var filtersContent = new AntdDiv();
        filtersContent.setParent(this);
        getChildren().add(filtersContent);
        filtersContent.setTag("filtersContent");
        this.filters.forEach(filter -> {
            EntitiesListFilter f = null;
            switch (filter.getType()){
                case STRING:{
                    f = new TextEntitiesListFilter(filter.getTitle());
                    break;
                }
                case NUMBER_INTERVAL:{
                    f = new NumberEntitiesListFilter(filter.getTitle(), this.language);
                    break;
                }
            }
            f.setParent(filtersContent);
            filtersContent.getChildren().add(f);
            filtersValues.put(filter.getId(), f);
        });
        {
            var filtersFooter = new AntdDiv();
            var footerStyle = new HashMap<String,Object>();
            filtersFooter.setTag("filtersFooter");
            filtersFooter.setParent(this);
            getChildren().add(filtersFooter);
            footerStyle.put("display", "flex");
            footerStyle.put("flexDirection", "row");
            footerStyle.put("padding", "5px");
            filtersFooter.setStyle(footerStyle);
            {
                var div = new AntdDiv();
                div.setContent("");
                var divStyle = new HashMap<String,Object>();
                divStyle.put("flexGrow", 1);
                div.setStyle(divStyle);
                filtersFooter.getChildren().add(div);
                div.setParent(filtersFooter);
            }
            {
                var button = new AntdButton();
                button.setTitle("en".equals(language)? "Apply": "Применить");
                var buttonStyle = new HashMap<String,Object>();
                buttonStyle.put("display", "inline-block");
                buttonStyle.put("flexGrow", 0);
                buttonStyle.put("margin", "5px");
                button.setStyle(buttonStyle);
                filtersFooter.getChildren().add(button);
                button.setParent(filtersFooter);
                button.setOnClicked(ctx -> {
                    loadData(ctx);
                });
            }

            {
                var button = new AntdButton();
                button.setTitle("en".equals(language)? "Clear": "Очистить");
                var buttonStyle = new HashMap<String,Object>();
                buttonStyle.put("display", "inline-block");
                buttonStyle.put("flexGrow", 0);
                button.setStyle(buttonStyle);
                buttonStyle.put("margin", "5px");
                filtersFooter.getChildren().add(button);
                button.setParent(filtersFooter);
                button.setOnClicked(ctx -> {
                    filtersValues.values().forEach(fv ->{
                        fv.clear(ctx);
                    });
                    loadData(ctx);
                });
            }

        }


    }

    @Override
    public JsonElement serialize() throws Exception {
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
        operationUiContext.sendElementPropertyChange(getId(), "data", response);
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

    public void setLanguage(String language) {
        this.language = language;
    }
}
