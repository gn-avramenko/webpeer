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

package com.gridnine.webpeer.antd.admin.ui.components.table;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.builders.AntdDivConfigurationBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiModel;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.stream.Collectors;

public class AntdTable extends BaseAntdUiElement<AntdTableConfiguration> {

    private int limit = configuration.getLimitStep();

    private AntdTableSort sort = configuration.getInitSort();

    public AntdTable(AntdTableConfiguration config, OperationUiContext ctx) {
        super(config,ctx);
    }

    @Override
    public JsonObject buildElement(OperationUiContext context) {
        var result =  super.buildElement(context);
        {
            var columns = new JsonArray();
            result.add("columns", columns);
            configuration.getColumns().forEach(column -> {
                var item = new JsonObject();
                columns.add(item);
                item.addProperty("id", column.getId());
                item.addProperty("name", column.getName());
                item.addProperty("sortable", column.isSortable());
                item.addProperty("type", column.getType().name());
                item.addProperty("alignment", column.getAlignment().name());
                if(column.getWidth() != null){
                    item.addProperty("width", column.getWidth());
                }
            });
            if(configuration.getInitSort() != null){
                var sort = new JsonObject();
                sort.addProperty("propertyName", configuration.getInitSort().getPropertyName());
                sort.addProperty("desc", configuration.getInitSort().isDescending());
                result.add("sort", sort);
            }
        }
        return result;
    }


    @Override
    protected void executeAction(String actionId, JsonElement actionData, OperationUiContext operationUiContext) throws Exception {
        if (actionId.equals("init")) {
            loadData(operationUiContext, actionData.getAsString());
            return;
        }
        if (actionId.equals("increaseLimit")) {
            limit += configuration.getLimitStep();
            loadData(operationUiContext, actionData.getAsString());
            return;
        }
        if (actionId.equals("changeSort")) {
            var as = actionData.getAsJsonObject();
            limit = configuration.getLimitStep();
            sort = new AntdTableSort();
            sort.setDescending(as.get("desc").getAsBoolean());
            sort.setPropertyName(as.get("propertyName").getAsString());
            loadData(operationUiContext, as.get("requestId").getAsString());
            return;
        }
        super.executeAction(actionId, actionData, operationUiContext);
    }

    private void loadData(OperationUiContext operationUiContext, String requestId) throws Exception {
        var data = configuration.getDataProvider().getData(configuration.getColumns().stream()
                .map(AntdTableColumnDescription::getId).collect(Collectors.toList()), limit, sort);
        var response = new JsonObject();
        var items = data.getData();
        response.add("data", items);
        response.addProperty("hasMore", data.isHasMore());
        response.addProperty("requestId", requestId);
        if(configuration.getColumns().stream().anyMatch(it -> it.getType() == AntdTableColumnType.CUSTOM)){
            var div = AntdDivConfigurationBuilder.createElement(null, operationUiContext, c-> c.tag("custom-entities-list-elements-wrapper"));
            configuration.getColumns().stream().filter(c -> c.getType() == AntdTableColumnType.CUSTOM).forEach(c ->{
                items.forEach( elm ->{
                    var obj = (JsonObject) elm;
                    WebPeerUtils.wrapException(()->{
                        var ce = c.getCustomRenderer().call(obj, operationUiContext);
                        UiModel.addElement(ce, div);
                        obj.addProperty(c.getId(), String.valueOf(ce.getId()));
                    });
                });
            });
            OperationUiContext.upsertChild(getChildren().isEmpty()? null: getChildren().get(0), div, this, operationUiContext);
        }
        var jsort = new JsonObject();
        jsort.addProperty("propertyName", sort.getPropertyName());
        jsort.addProperty("desc", sort.isDescending());
        response.add("sort", jsort);
        operationUiContext.sendElementPropertyChange(getId(), "data", response);
    }

    @Override
    public String getType() {
        return "table";
    }
}
