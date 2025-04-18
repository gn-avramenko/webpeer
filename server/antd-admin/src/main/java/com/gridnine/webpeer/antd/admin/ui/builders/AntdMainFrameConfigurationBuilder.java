package com.gridnine.webpeer.antd.admin.ui.builders;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdStyle;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenuItem;
import com.gridnine.webpeer.antd.admin.ui.mainFrame.AntdMainFrameConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdViewProvider;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.List;

public class AntdMainFrameConfigurationBuilder {

    private final AntdMainFrameConfiguration configuration = new AntdMainFrameConfiguration();

    public void desktopWidth(int desktopWidth){
        configuration.setDesktopWidth(desktopWidth);
    }

    public void header(BaseAntdUiElement<?> header, AntdStyle... headerStyles){
        configuration.setHeader(header);
        configuration.setHeaderStyle(headerStyles);
    }

    public void theme(JsonObject obj){
        configuration.setTheme(obj);
    }

    public void viewProvider(AntdViewProvider provider){
        configuration.setViewProvider(provider);
    }

    public void menu(List<AntdMenuItem> menuItems){
        configuration.setMenuItems(menuItems);
    }


    public static AntdMainFrameConfiguration build(RunnableWithExceptionAndArgument<AntdMainFrameConfigurationBuilder> configurator) {
        final AntdMainFrameConfigurationBuilder builder = new AntdMainFrameConfigurationBuilder();
        return WebPeerUtils.wrapException(()-> {
            configurator.run(builder);
            return builder.configuration;
        });
    }

}
