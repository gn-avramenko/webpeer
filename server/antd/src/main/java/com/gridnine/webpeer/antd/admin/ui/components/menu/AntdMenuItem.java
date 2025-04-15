package com.gridnine.webpeer.antd.admin.ui.components.menu;

import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;

import java.util.ArrayList;
import java.util.List;

public class AntdMenuItem {
    private String name;
    private String icon;
    private RunnableWithExceptionAndArgument<OperationUiContext> handler;

    private final List<AntdMenuItem> children = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AntdMenuItem> getChildren() {
        return children;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setHandler(RunnableWithExceptionAndArgument<OperationUiContext> handler) {
        this.handler = handler;
    }

    public RunnableWithExceptionAndArgument<OperationUiContext> getHandler() {
        return handler;
    }
}
