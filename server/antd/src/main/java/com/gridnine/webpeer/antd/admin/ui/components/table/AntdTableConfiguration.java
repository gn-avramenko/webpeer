package com.gridnine.webpeer.antd.admin.ui.components.table;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AntdTableConfiguration extends BaseAntdConfiguration {

    private List<AntdTableColumnDescription> columns = new ArrayList<>();

    private AntdTableSort initSort;

    private int limitStep = 50;

    private AntdTableDataProvider dataProvider;

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

    public AntdTableDataProvider getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(AntdTableDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void setLimitStep(int limitStep) {
        this.limitStep = limitStep;
    }

    public int getLimitStep() {
        return limitStep;
    }
}
