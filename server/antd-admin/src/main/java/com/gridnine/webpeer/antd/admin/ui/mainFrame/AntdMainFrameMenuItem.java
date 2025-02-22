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
    private String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AntdMainFrameMenuItem> getChildren() {
        return children;
    }

    @Override
    public JsonElement serialize() throws Exception {
        var result = new JsonObject();
        result.addProperty("type", type.name());
        result.addProperty("name", name);
        if(id != null) {
            result.addProperty("id", id);
        }
        if(type == AntdMainFrameMenuItemType.GROUP && !children.isEmpty()) {
            var ch = new JsonArray();
            children.forEach(it -> ch.add(WebPeerUtils.wrapException(it::serialize)));
            result.add("children", ch);
        }
        return result;
    }
}
