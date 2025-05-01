package com.gridnine.webpeer.antd.admin.ui.components.textField;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;

public class AntdTextFieldConfiguration extends BaseAntdConfiguration {

    private int debounceTime;

    private boolean deferred;

    private String clientChangeHandlerId;

    public String getClientChangeHandlerId() {
        return clientChangeHandlerId;
    }

    public void setClientChangeHandlerId(String clientChangeHandlerId) {
        this.clientChangeHandlerId = clientChangeHandlerId;
    }

    private RunnableWithExceptionAndTwoArguments<String, OperationUiContext> valueChangedHandler;

    private String initValue;

    public AntdTextFieldConfiguration(JsonObject uiData) {
        this.initValue = uiData != null && uiData.has("value")? uiData.get("value").getAsString(): null;
    }

    public int getDebounceTime() {
        return debounceTime;
    }

    public void setDebounceTime(int debounceTime) {
        this.debounceTime = debounceTime;
    }

    public String getInitValue() {
        return initValue;
    }

    public boolean isDeferred() {
        return deferred;
    }

    public void setDeferred(boolean deferred) {
        this.deferred = deferred;
    }

    public RunnableWithExceptionAndTwoArguments<String, OperationUiContext> getValueChangedHandler() {
        return valueChangedHandler;
    }

    public void setValueChangedHandler(RunnableWithExceptionAndTwoArguments<String, OperationUiContext> valueChangedHandler) {
        this.valueChangedHandler = valueChangedHandler;
    }
}
