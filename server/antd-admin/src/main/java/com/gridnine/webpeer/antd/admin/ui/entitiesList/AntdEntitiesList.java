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

public class AntdEntitiesList extends AntdDiv {

    private AntdTextField searchField;

    private Map<String, EntitiesListFilter> filtersValues = new HashMap<>();

    @Override
    protected AntdDivConfiguration createConfiguration(JsonObject uiData, Object config, OperationUiContext ctx) {
        AntEntitiesListConfiguration conf = (AntEntitiesListConfiguration) config;
        return AntdDivConfigurationBuilder.createConfiguration(uiData,  el ->{
            el.style("display=flex;flexDirection=column;width=100%;height=100%");
            el.div(uiData, ctx, h ->{
                h.style("width=100%;flexGrow=0");
                h.div(null, ctx, hd ->{
                    hd.style("flexGrow=0;display=flex;flexDirection=row;width=100%;height=100%;alignItems=center");
                    hd.div(null, ctx, t -> {
                                t.content(conf.getTitle());
                                t.style("flexGrow=0;padding=token:padding;fontSize=token:fontSizeHeading2;fontWeight=token:fontWeightStrong;marginRight=token:padding");
                            }
                    );
                    hd.glue(ctx);
                    hd.div(null, ctx, tf ->{
                        tf.style("flexGrow=0;padding=token:padding");
                        searchField = tf.textField((JsonObject) AntdUtils.findUiDataByTag(uiData,"search-field"), ctx, f ->{
                            f.tag("search-field");
                            f.debounceTime(300);
                            f.valueChangedHandler((value, ctx1) ->{
                                System.out.println(value);
                            });
                        } );
                    });
                    hd.div(null, ctx, id ->{
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
                cd.table(ctx, table->{
                    table.style("width=100%;height=100%");
                    table.columns(conf.getColumns());
                    table.initSort(conf.getInitSort());
                    table.dataProvider((fields, limit, sort) -> {
                                var filters = new HashMap<String, JsonElement>();
                                filtersValues.forEach((k,v) -> filters.put(k, v.get()));
                                return conf.getDataProvider().getData(fields, limit, sort, searchField.getValue(), filters);
                            }
                    );
                });
            });
        });
    }

    public AntdEntitiesList(AntEntitiesListConfiguration configuration, JsonObject uidData, OperationUiContext context) {
        super(uidData, configuration, context);
    }

}
