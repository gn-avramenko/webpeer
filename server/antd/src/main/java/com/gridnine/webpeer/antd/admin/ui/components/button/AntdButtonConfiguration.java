package com.gridnine.webpeer.antd.admin.ui.components.button;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;

import java.util.HashMap;
import java.util.Map;

public class AntdButtonConfiguration extends BaseAntdConfiguration {

    private String title;

    private RunnableWithExceptionAndArgument<OperationUiContext> clickHandler;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setClickHandler(RunnableWithExceptionAndArgument<OperationUiContext> clickHandler) {
        this.clickHandler = clickHandler;
    }

    public RunnableWithExceptionAndArgument<OperationUiContext> getClickHandler() {
        return clickHandler;
    }

    public AntdButtonConfiguration() {

    }

}
