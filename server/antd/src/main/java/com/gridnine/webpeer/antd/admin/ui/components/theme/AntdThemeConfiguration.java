package com.gridnine.webpeer.antd.admin.ui.components.theme;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;

public class AntdThemeConfiguration extends BaseAntdConfiguration {

    private JsonObject theme;

    public JsonObject getTheme() {
        return theme;
    }

    public void setTheme(JsonObject theme) {
        this.theme = theme;
    }
}
