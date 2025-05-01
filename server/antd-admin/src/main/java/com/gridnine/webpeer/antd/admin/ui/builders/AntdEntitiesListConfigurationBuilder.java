package com.gridnine.webpeer.antd.admin.ui.builders;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.table.*;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntEntitiesListConfiguration;
import com.gridnine.webpeer.antd.admin.ui.entitiesList.AntdEntitiesListDataProvider;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.CallableWithExceptionAndTwoArguments;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdEntitiesListConfigurationBuilder {

    private final AntEntitiesListConfiguration configuration = new AntEntitiesListConfiguration();

    public void title(String title) {
        configuration.setTitle(title);
    }

    public void standardColumn(String id, String name, boolean sortable, AntdTableColumnAlignment alignment, Integer width){
        var column = new AntdTableColumnDescription();
        column.setId(id);
        column.setName(name);
        column.setSortable(sortable);
        column.setAlignment(alignment);
        column.setWidth(width);
        column.setType(AntdTableColumnType.STANDARD);
        configuration.getColumns().add(column);
    }

    public void customColumn(String id, String name,  AntdTableColumnAlignment alignment, Integer width, CallableWithExceptionAndTwoArguments<JsonObject, OperationUiContext, BaseAntdUiElement<?>> renderer){
        var column = new AntdTableColumnDescription();
        column.setId(id);
        column.setName(name);
        column.setSortable(false);
        column.setAlignment(alignment);
        column.setWidth(width);
        column.setType(AntdTableColumnType.CUSTOM);
        column.setCustomRenderer(renderer);
        configuration.getColumns().add(column);
    }

    public void dataProvider(AntdEntitiesListDataProvider dataProvider) {
        configuration.setDataProvider(dataProvider);
    }
    public void initSort(String propertyName, boolean desc){
        var sort = new AntdTableSort();
        sort.setDescending(desc);
        sort.setPropertyName(propertyName);
        configuration.setInitSort(sort);
    }

    public static AntEntitiesListConfiguration build(RunnableWithExceptionAndArgument<AntdEntitiesListConfigurationBuilder> configurator) {
        final AntdEntitiesListConfigurationBuilder builder = new AntdEntitiesListConfigurationBuilder();
        return WebPeerUtils.wrapException(()-> {
            configurator.run(builder);
            return builder.configuration;
        });
    }

}
