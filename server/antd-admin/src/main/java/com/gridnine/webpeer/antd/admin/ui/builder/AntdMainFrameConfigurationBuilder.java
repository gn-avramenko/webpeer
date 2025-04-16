package com.gridnine.webpeer.antd.admin.ui.builder;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.mainFrame.AntdMainFrameConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdViewProvider;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

public class AntdMainFrameConfigurationBuilder {

    private final AntdMainFrameConfiguration configuration = new AntdMainFrameConfiguration();

    public void desktopWidth(int desktopWidth){
        configuration.setDesktopWidth(desktopWidth);
    }

    public void header(BaseAntdUiElement header, Style... headerStyles){
        configuration.setHeader(header);
        configuration.setHeaderStyle(headerStyles);
    }

    public void theme(JsonObject obj){
        configuration.setTheme(obj);
    }

    public void viewProvider(AntdViewProvider provider){
        configuration.setViewProvider(provider);
    }
    public void menu(RunnableWithExceptionAndArgument<AntdMenuBuilder> configurator){
        var builder = new AntdMenuBuilder(configuration.getMenuItems());
        WebPeerUtils.wrapException(()->{
            configurator.run(builder);
        });
    }


    public static AntdMainFrameConfiguration build(RunnableWithExceptionAndArgument<AntdMainFrameConfigurationBuilder> configurator) {
        final AntdMainFrameConfigurationBuilder builder = new AntdMainFrameConfigurationBuilder();
        return WebPeerUtils.wrapException(()-> {
            configurator.run(builder);
            return builder.configuration;
        });
    }

}
