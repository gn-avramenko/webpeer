package com.gridnine.webpeer.antd.admin.ui.components.router;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

public class AntdRouterConfiguration extends BaseAntdConfiguration {

    private String initPath;

    private AntdViewProvider viewProvider;

    public String getInitPath() {
        return initPath;
    }

    public void setInitPath(String initPath) {
        this.initPath = initPath;
    }

    public AntdViewProvider getViewProvider() {
        return viewProvider;
    }

    public void setViewProvider(AntdViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }
}
