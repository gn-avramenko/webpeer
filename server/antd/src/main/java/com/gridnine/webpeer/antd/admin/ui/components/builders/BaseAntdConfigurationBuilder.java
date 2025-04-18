/*
 * MIT License
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gridnine.webpeer.antd.admin.ui.components.builders;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdUiElement;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.icon.AntdIcon;
import com.gridnine.webpeer.antd.admin.ui.components.layout.*;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenu;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdRouter;
import com.gridnine.webpeer.antd.admin.ui.components.textField.AntdTextField;
import com.gridnine.webpeer.antd.admin.ui.components.theme.AntdTheme;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;

import java.util.Map;

public class BaseAntdConfigurationBuilder<T extends BaseAntdConfiguration> {
    protected final T config;

    public BaseAntdConfigurationBuilder(T config){
        this.config = config;
    }
    public AntdTheme theme(OperationUiContext context, RunnableWithExceptionAndArgument<AntdThemeConfigurationBuilder> configurator){
        var configuration = AntdThemeConfigurationBuilder.createConfiguration(configurator);
        var theme = new AntdTheme(configuration, context);
        config.getChildren().add(theme);
        return theme;
    }

    protected void header(OperationUiContext context, RunnableWithExceptionAndArgument<AntdHeaderConfigurationBuilder> configurator){
        var configuration = AntdHeaderConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdHeader(configuration, context);
        this.config.getChildren().add(layout);
    }


    protected void sider(OperationUiContext context, RunnableWithExceptionAndArgument<AntdSiderConfigurationBuilder> configurator){
        var configuration = AntdSiderConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdSider(configuration, context);
        this.config.getChildren().add(layout);
    }

    protected void content(OperationUiContext context, RunnableWithExceptionAndArgument<AntdContentConfigurationBuilder> configurator){
        var configuration = AntdContentConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdContent(configuration, context);
        this.config.getChildren().add(layout);
    }

    public void appendChild(BaseAntdUiElement<?> elm){
        config.getChildren().add(elm);
    }

    public void style(Map<String,Object> style){
        config.getStyle().putAll(style);
    }

    public void style(String propertyName, Object propertyValue){
        config.getStyle().put(propertyName, propertyValue);
    }

    public void layout(OperationUiContext context, RunnableWithExceptionAndArgument<AntdLayoutConfigurationBuilder> configurator){
        var configuration = AntdLayoutConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdLayout(configuration, context);
        this.config.getChildren().add(layout);
    }

    public void icon(OperationUiContext context, RunnableWithExceptionAndArgument<AntdIconConfigurationBuilder> configurator){
        var configuration = AntdIconConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdIcon(configuration, context);
        this.config.getChildren().add(layout);
    }

    public void div(JsonObject uiData, OperationUiContext context, RunnableWithExceptionAndArgument<AntdDivConfigurationBuilder> configurator){
        var configuration = AntdDivConfigurationBuilder.createConfiguration(uiData, configurator);
        var layout = new AntdDiv(configuration, context);
        this.config.getChildren().add(layout);
    }

    public void menu(OperationUiContext context, RunnableWithExceptionAndArgument<AntdMenuConfigurationBuilder> configurator){
        var configuration = AntdMenuConfigurationBuilder.createConfiguration(configurator);
        var layout = new AntdMenu(configuration, context);
        this.config.getChildren().add(layout);
    }

    public AntdDrawer drawer(OperationUiContext context, RunnableWithExceptionAndArgument<AntdDrawerConfigurationBuilder> configurator){
        var configuration = AntdDrawerConfigurationBuilder.createConfiguration(configurator);
        var drawer = new AntdDrawer(configuration, context);
        this.config.getChildren().add(drawer);
        return drawer;
    }

    public AntdRouter router(JsonObject uiData, OperationUiContext context, RunnableWithExceptionAndArgument<AntdRouterConfigurationBuilder> configurator){
        var configuration = AntdRouterConfigurationBuilder.createConfiguration(configurator);
        var router = new AntdRouter(configuration, uiData, context);
        this.config.getChildren().add(router);
        return router;
    }



    public void tag(String tag){
        config.setTag(tag);
    }

    public void style(String styleStr){
        config.setStyle(AntdUtils.parseStyle(styleStr));
    }

    public void glue(OperationUiContext context) {
        var configuration = AntdDivConfigurationBuilder.createConfiguration(null, (b)->{
            b.style("flexGrow=1");
        });
        var layout = new AntdDiv(configuration, context);
        this.config.getChildren().add(layout);
    }


    public AntdTextField textField(JsonObject uiData, OperationUiContext context, RunnableWithExceptionAndArgument<AntdTextFieldConfigurationBuilder> configurator) {
        var configuration = AntdTextFieldConfigurationBuilder.createConfiguration(uiData, configurator);
        var textField = new AntdTextField(configuration, context);
        this.config.getChildren().add(textField);
        return textField;
    }

}
