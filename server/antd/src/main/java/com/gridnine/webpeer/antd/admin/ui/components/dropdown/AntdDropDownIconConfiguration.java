package com.gridnine.webpeer.antd.admin.ui.components.dropdown;

import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AntdDropDownIconConfiguration extends BaseAntdConfiguration {
    private List<IconMenuItem> menu = new ArrayList<>();
    private String selectedItemId;

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public void setMenu(List<IconMenuItem> menu) {
        this.menu = menu;
    }

    public List<IconMenuItem> getMenu() {
        return menu;
    }
}
