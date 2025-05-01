package com.gridnine.webpeer.antd.admin.ui.components.div;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;

public class AntdDivConfiguration extends BaseAntdConfiguration {

    private boolean hidden;

    private String content;

    private RunnableWithExceptionAndArgument<OperationUiContext> clickHandler;

    private String clientClickHandlerId;

    public void setClientClickHandlerId(String clientClickHandlerId) {
        this.clientClickHandlerId = clientClickHandlerId;
    }

    public String getClientClickHandlerId() {
        return clientClickHandlerId;
    }

    public void setClickHandler(RunnableWithExceptionAndArgument<OperationUiContext> clickHandler) {
        this.clickHandler = clickHandler;
    }

    public RunnableWithExceptionAndArgument<OperationUiContext> getClickHandler() {
        return clickHandler;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public AntdDivConfiguration(JsonObject uiData) {
        hidden = uiData != null && uiData.has("hidden") && uiData.get("hidden").getAsBoolean();
    }

}
