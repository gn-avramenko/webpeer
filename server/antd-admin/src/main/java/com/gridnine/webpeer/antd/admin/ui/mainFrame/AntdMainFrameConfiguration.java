package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.builder.Style;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenuItem;

import java.util.*;

public class AntdMainFrameConfiguration {

    private int desktopWidth = 0;

    private JsonObject theme;

    private BaseAntdUiElement header;

    private Map<String, Object> headerStyle = new HashMap<String, Object>();

    public void setHeaderStyle(Style... headerStyles) {
        Arrays.stream(headerStyles).forEach(style -> {
            headerStyle.putAll(style);
        });
    }

    public void setTheme(JsonObject theme) {
        this.theme = theme;
    }

    public JsonObject getTheme() {
        return theme;
    }

    public Map<String, Object> getHeaderStyle() {
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

    public BaseAntdUiElement getHeader() {
        return header;
    }

    public void setHeader(BaseAntdUiElement header) {
        this.header = header;
    }
}
