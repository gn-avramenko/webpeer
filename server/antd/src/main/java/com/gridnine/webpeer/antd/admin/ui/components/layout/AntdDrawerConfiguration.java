package com.gridnine.webpeer.antd.admin.ui.components.layout;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;

public class AntdDrawerConfiguration extends BaseAntdConfiguration {

    public AntdDrawerConfiguration() {
        this.open = false;
        this.title = null;
        this.placement = "left";
        this.closeIcon = AntdIcons.MENU_FOLD_OUTLINED.name();
        this.bodyStyle = new JsonObject();
        this.headerStyle = new JsonObject();
    }

    private boolean open;

    private JsonElement getContainer;

    private String title;

    private String placement = "left";

    private String closeIcon = AntdIcons.MENU_FOLD_OUTLINED.name();

    private JsonObject bodyStyle = new JsonObject();

    private JsonObject headerStyle = new JsonObject();

    public void setGetContainer(JsonElement getContainer) {
        this.getContainer = getContainer;
    }

    public JsonElement getGetContainer() {
        return getContainer;
    }

    public void setFooter(BaseAntdUiElement<?> footer) {
        footer.setTag("footer");
        getChildren().add(footer);
    }

    public void setBody(BaseAntdUiElement<?> body) {
        body.setTag("body");
        getChildren().add(body);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getCloseIcon() {
        return closeIcon;
    }

    public void setCloseIcon(String closeIcon) {
        this.closeIcon = closeIcon;
    }

    public JsonObject getBodyStyle() {
        return bodyStyle;
    }

    public void setBodyStyle(JsonObject bodyStyle) {
        this.bodyStyle = bodyStyle;
    }

    public JsonObject getHeaderStyle() {
        return headerStyle;
    }

    public void setHeaderStyle(JsonObject headerStyle) {
        this.headerStyle = headerStyle;
    }
}
