package com.gridnine.webpeer.antd.admin.ui.components.dropdown;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AntdDropDownImageConfiguration extends BaseAntdConfiguration {
    private List<ImageMenuItem> menu = new ArrayList<>();
    private String selectedItemId;

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public List<ImageMenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<ImageMenuItem> menu) {
        this.menu = menu;
    }
}
