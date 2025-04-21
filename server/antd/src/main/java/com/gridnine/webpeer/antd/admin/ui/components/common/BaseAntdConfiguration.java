package com.gridnine.webpeer.antd.admin.ui.components.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseAntdConfiguration {

    private String tag;

    private Map<String, Object> style = new HashMap<>();

    private List<BaseAntdUiElement<?>> children = new ArrayList<>();

    public void setChildren(List<BaseAntdUiElement<?>> children) {
        this.children = children;
    }

    public List<BaseAntdUiElement<?>> getChildren() {
        return children;
    }

    public Map<String, Object> getStyle() {
        return style;
    }

    public void setStyle(Map<String, Object> style) {
        this.style = style;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
