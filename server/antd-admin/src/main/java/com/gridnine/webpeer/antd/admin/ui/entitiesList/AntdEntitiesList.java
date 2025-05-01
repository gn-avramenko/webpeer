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
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdDrawer;
import com.gridnine.webpeer.antd.admin.ui.components.textField.AntdTextField;
import com.gridnine.webpeer.core.ui.OperationUiContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AntdEntitiesList extends AntdDiv {

    private AntdTextField searchField;

    private Map<String, EntitiesListFilter> filtersValues = new HashMap<>();


    private AntdDrawer drawer;

    @Override
    protected AntdDivConfiguration createConfiguration(JsonObject uiData, Object config, OperationUiContext ctx) {
        AntEntitiesListConfiguration conf = (AntEntitiesListConfiguration) config;
        return AntdDivConfigurationBuilder.createConfiguration(uiData,  el ->{
            el.style("position=relative;display=flex;flexDirection=column;width=100%;height=100%");
            el.tag("entities-list");
            el.className("entities-list");
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
                            f.deferred(true);
                            f.clientChangeHandlerId("entities-list-search-field-change-handler");
                        } );
                    });
                    hd.div(null, ctx, id ->{
                        id.style("flexGrow=0;paddingRight=token:padding");
                        id.clientClickHandlerId("entities-list-filters-open-handler");
                        id.icon(ctx, i ->{
                            i.icon(AntdIcons.FILTER_OUTLINED.name());
                        });
                    });
                });
            });
            el.div(uiData, ctx, cd ->{
                cd.style("flexGrow=1");
                drawer = cd.drawer(ctx, db ->{
                    db.tag("entities-list-drawer");
                    db.placement("right");
                    db.title("Filters");
                    db.noContainer();
                    db.footer(AntdDivConfigurationBuilder.createElement(null, ctx, (fb)->{
                        fb.style("width=100%;display=flex;flexDirection=row");
                        fb.glue(ctx);
                        fb.div(null, ctx, ok->{
                            ok.style("padding=5px;flexGrow=0");
                            ok.button(ctx, button->{
                                button.title("Apply");
                                button.clickHandler(c->{
                                    drawer.setOpen(false, c);
                                });
                            });
                        });
                        fb.div(null, ctx, clear->{
                            clear.style("padding=5px;flexGrow=0");
                            clear.button(ctx, button->{
                                button.title("Clear");
                                button.clickHandler(c->{
                                    drawer.setOpen(false, c);
                                });
                            });
                        });
                    }));
                });
                cd.table(ctx, table->{
                    table.tag("entities-list-table");
                    table.style("width=100%;height=100%");
                    var columns = new ArrayList<>(conf.getColumns());
                    table.columns(columns);
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
