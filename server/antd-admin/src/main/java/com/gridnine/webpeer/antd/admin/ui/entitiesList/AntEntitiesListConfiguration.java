package com.gridnine.webpeer.antd.admin.ui.entitiesList;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.table.AntdTableColumnDescription;
import com.gridnine.webpeer.antd.admin.ui.components.table.AntdTableSort;

import java.util.ArrayList;
import java.util.List;

public class AntEntitiesListConfiguration extends BaseAntdConfiguration {
    private String title;

    private AntdTableSort initSort;

    private AntdEntitiesListDataProvider dataProvider;

    private List<AntdTableColumnDescription> columns = new ArrayList<>();

    public void setInitSort(AntdTableSort initSort) {
        this.initSort = initSort;
    }

    public AntdTableSort getInitSort() {
        return initSort;
    }

    public List<AntdTableColumnDescription> getColumns() {
        return columns;
    }

    public void setColumns(List<AntdTableColumnDescription> columns) {
        this.columns = columns;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDataProvider(AntdEntitiesListDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public AntdEntitiesListDataProvider getDataProvider() {
        return dataProvider;
    }
}
