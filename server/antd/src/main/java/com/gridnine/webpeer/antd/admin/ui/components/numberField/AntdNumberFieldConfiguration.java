package com.gridnine.webpeer.antd.admin.ui.components.numberField;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenuItem;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AntdNumberFieldConfiguration extends BaseAntdConfiguration {

    private boolean deferred;

    private RunnableWithExceptionAndTwoArguments<BigDecimal, OperationUiContext> valueChangedHandler;

    private BigDecimal initValue;

    public AntdNumberFieldConfiguration(JsonObject uiData) {
        this.initValue = uiData != null && uiData.has("value")? uiData.get("value").getAsBigDecimal(): null;
    }

    public void setInitValue(BigDecimal initValue) {
        this.initValue = initValue;
    }

    public BigDecimal getInitValue() {
        return initValue;
    }

    public boolean isDeferred() {
        return deferred;
    }

    public void setDeferred(boolean deferred) {
        this.deferred = deferred;
    }

    public RunnableWithExceptionAndTwoArguments<BigDecimal, OperationUiContext> getValueChangedHandler() {
        return valueChangedHandler;
    }

    public void setValueChangedHandler(RunnableWithExceptionAndTwoArguments<BigDecimal, OperationUiContext> valueChangedHandler) {
        this.valueChangedHandler = valueChangedHandler;
    }
}
