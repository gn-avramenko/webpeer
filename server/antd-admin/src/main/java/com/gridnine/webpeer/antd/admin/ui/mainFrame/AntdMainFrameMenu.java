package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.GsonSerializable;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public class AntdMainFrameMenu implements GsonSerializable {
    private List<AntdMainFrameMenuItem> menuItems= new ArrayList<>();

    public List<AntdMainFrameMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<AntdMainFrameMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        if(!menuItems.isEmpty()){
            var ch = new JsonArray();
            menuItems.forEach(it -> ch.add(WebPeerUtils.wrapException(it::serialize)));
            result.add("items", ch);
        }
        return result;
    }
}
