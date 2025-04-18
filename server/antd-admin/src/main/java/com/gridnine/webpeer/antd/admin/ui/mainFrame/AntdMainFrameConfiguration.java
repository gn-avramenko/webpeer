package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdStyle;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenuItem;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdViewProvider;

import java.util.*;

public class AntdMainFrameConfiguration {

    private int desktopWidth = 0;

    private JsonObject theme;

    private BaseAntdUiElement<?> header;

    private AntdStyle headerStyle = new AntdStyle();

    private AntdViewProvider viewProvider;

    public void setViewProvider(AntdViewProvider viewProvider) {
        this.viewProvider = viewProvider;
    }

    public AntdViewProvider getViewProvider() {
        return viewProvider;
    }

    public void setHeaderStyle(AntdStyle... headerStyles) {
        Arrays.stream(headerStyles).forEach(headerStyle::putAll);
    }

    public void setTheme(JsonObject theme) {
        this.theme = theme;
    }

    public JsonObject getTheme() {
        return theme;
    }

    public AntdStyle getHeaderStyle() {
        return headerStyle;
    }

    private List<AntdMenuItem> menuItems = new ArrayList<>();

    public int getDesktopWidth() {
        return desktopWidth;
    }

    public List<AntdMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setDesktopWidth(int desktopWidth) {
        this.desktopWidth = desktopWidth;
    }

    public BaseAntdUiElement<?> getHeader() {
        return header;
    }

    public void setHeader(BaseAntdUiElement<?> header) {
        this.header = header;
    }

    public void setHeaderStyle(AntdStyle headerStyle) {
        this.headerStyle = headerStyle;
    }

    public void setMenuItems(List<AntdMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

}
