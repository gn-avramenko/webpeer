package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.GsonSerializable;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.ArrayList;
import java.util.List;

public class AntdMainFrameMenuItem implements GsonSerializable {
    private AntdMainFrameMenuItemType type;
    private String name;
    private String link;
    private String icon;

    private final List<AntdMainFrameMenuItem> children = new ArrayList<>();

    public AntdMainFrameMenuItemType getType() {
        return type;
    }

    public void setType(AntdMainFrameMenuItemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<AntdMainFrameMenuItem> getChildren() {
        return children;
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("type", type.name());
        result.addProperty("name", name);
        if(link != null) {
            result.addProperty("link", link);
        }
        if(icon != null) {
            result.addProperty("icon", icon);
        }
        if(type == AntdMainFrameMenuItemType.GROUP && !children.isEmpty()) {
            var ch = new JsonArray();
            children.forEach(it -> ch.add(WebPeerUtils.wrapException(it::serialize)));
            result.add("children", ch);
        }
        return result;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
