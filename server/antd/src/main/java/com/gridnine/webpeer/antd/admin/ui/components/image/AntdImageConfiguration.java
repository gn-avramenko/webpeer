package com.gridnine.webpeer.antd.admin.ui.components.image;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

public class AntdImageConfiguration extends BaseAntdConfiguration {
    private String width;

    private String height;

    private String src;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
