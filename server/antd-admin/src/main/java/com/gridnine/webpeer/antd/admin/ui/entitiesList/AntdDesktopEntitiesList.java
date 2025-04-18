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
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.builders.AntdDivConfigurationBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDivConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.textField.AntdTextField;
import com.gridnine.webpeer.core.ui.OperationUiContext;

import java.util.HashMap;
import java.util.Map;

public class AntdDesktopEntitiesList extends AntdDiv {

    private JsonArray data = new JsonArray();


    private String title;

    private int limitStep = 50;

    private int limit = limitStep;

//    private AntdSorting sort;

    private AntdEntitiesListDataProvider dataProvider;

    private AntdTextField searchField;

    private Map<String, EntitiesListFilter> filtersValues = new HashMap<>();

    private String language;

    @Override
    protected AntdDivConfiguration createConfiguration(JsonObject uiData, Object config, OperationUiContext ctx) {
        AntEntitiesListConfiguration conf = (AntEntitiesListConfiguration) config;
        return AntdDivConfigurationBuilder.createConfiguration(uiData,  el ->{
            el.style("display=flex;flexDirection=column;width=100%;height=100%");
            el.div(uiData, ctx, h ->{
                h.style("display=flex;flexDirection=row;width=100%;alignItems=center;flexGrow=0");
                h.div(null, ctx, t ->{
                    t.style("flexGrow=0;padding=token:padding;fontSize=token:fontSizeHeading2;fontWeight=token:fontWeightStrong;marginRight=token:padding");
                    t.content(conf.getTitle());
                    t.glue(ctx);
                    searchField = t.textField((JsonObject) AntdUtils.findUiDataByTag(uiData,"search-field"), ctx, tf ->{
                        tf.tag("search-field");
                        tf.style("flexGrow=0;padding=token:padding");
                        tf.debounceTime(300);
                    } );
                    t.div(null, ctx, id ->{
                        id.style("flexGrow=0;paddingRight=token:padding");
                        id.clickHandler((c) ->{
                            System.out.println("test");
                        });
                        id.icon(ctx, i ->{
                            i.icon(AntdIcons.FILTER_OUTLINED.name());
                        });
                    });
                });
            });
            el.div(uiData, ctx, cd ->{
                cd.style("flexGrow=1");
                cd.drawer(ctx, d ->{

                });
            });
        });
    }

    public AntdDesktopEntitiesList(AntEntitiesListConfiguration configuration, JsonObject uidData, OperationUiContext context) {
        super(uidData, configuration, context);
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
//        if (actionId.equals("changeSort")) {
//            var as = actionData.getAsJsonObject();
//            sort = new AntdSorting();
//            sort.setDirection(as.get("desc").getAsBoolean() ? AntdSortDirection.DESC : AntdSortDirection.ASC);
//            sort.setColumn(as.get("propertyName").getAsString());
//            loadData(operationUiContext);
//            return;
//        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    private void loadData(OperationUiContext operationUiContext) {
        var fv = new HashMap<String, JsonElement>();
        filtersValues.forEach((k,v) ->{
            fv.put(k, v.get());
        });
//        var data = dataProvider.getData(columns, limit, sort, searchField.getValue(), fv);
//        this.data = data.getData();
//        var response = new JsonObject();
//        response.add("data", data.getData());
//        response.addProperty("hasMore", data.isHasMore());
//        var jsort = new JsonObject();
//        jsort.addProperty("propertyName", sort.getColumn());
//        jsort.addProperty("desc", sort.getDirection() == AntdSortDirection.DESC);
//        response.add("sort", jsort);
//        operationUiContext.sendElementPropertyChange(getId(), "data", response);
    }

    public void setLimitStep(int limitStep) {
        this.limitStep = limitStep;
    }

//    public List<AntdEntitiesListColumnDescription> getColumns() {
//        return columns;
//    }

//    public void setSort(AntdSorting sort) {
//        this.sort = sort;
//    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDataProvider(AntdEntitiesListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

//    public List<AntdEntitiesListFilterDescription> getFilters() {
//        return filters;
//    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
