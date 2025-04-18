package com.gridnine.webpeer.antd.admin.ui.components.menu;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.dropdown.IconMenuItem;
import com.gridnine.webpeer.antd.admin.ui.components.dropdown.ImageMenuItem;

import java.util.ArrayList;
import java.util.List;

public class AntdMenuConfiguration extends BaseAntdConfiguration {
    private List<AntdMenuItem> menu = new ArrayList<>();

    public void setMenu(List<AntdMenuItem> menu) {
        this.menu = menu;
    }

    public List<AntdMenuItem> getMenu() {
        return menu;
    }
}
