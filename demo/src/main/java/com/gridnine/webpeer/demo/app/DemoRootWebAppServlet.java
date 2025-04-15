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

package com.gridnine.webpeer.demo.app;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.builder.AntdAdminStyles;
import com.gridnine.webpeer.antd.admin.ui.builder.AntdUiBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.breakpoint.AntdBreakpoint;
import com.gridnine.webpeer.antd.admin.ui.mainFrame.AntdMainFrame;
import com.gridnine.webpeer.antd.admin.ui.builder.AntdMainFrameConfigurationBuilder;
import com.gridnine.webpeer.core.servlet.BaseWebAppServlet;
import com.gridnine.webpeer.core.servlet.CoreWebAppModule;
import com.gridnine.webpeer.core.servlet.WebAppModule;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiModel;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class DemoRootWebAppServlet extends BaseWebAppServlet<AntdMainFrame> {
    @Override
    protected List<WebAppModule> getModules() throws Exception {
        return List.of(new CoreWebAppModule());
    }

    @Override
    protected URL getFaviconUrl() {
        return getClass().getClassLoader().getResource("demo/favicon.ico");
    }

    @Override
    protected Map<String, String> getWebAppParameters() {
        return Map.of();
    }

    @Override
    protected String getTitle() {
        return "Demo App";
    }

    @Override
    protected AntdMainFrame createRootElement(UiModel model, JsonObject uiData, OperationUiContext operationUiContext) throws Exception {
        return new AntdMainFrame(model, uiData, operationUiContext, AntdMainFrameConfigurationBuilder.build(c->{
            var lang = "en".equals(operationUiContext.getStringLocalStorageParam("lang"))? "en": "ru";
            c.desktopWidth(1024);
            var mobile = "mobile".equals(operationUiContext.getParameter(AntdBreakpoint.BREAKPOINT));
            if("dark".equals(operationUiContext.getStringLocalStorageParam("theme"))){
                c.theme(mobile? Constants.DARK_MOBILE_THEME: Constants.DARK_DESKTOP_THEME);
            } else {
                c.theme(mobile? Constants.LIGHT_MOBILE_THEME: Constants.LIGHT_DESKTOP_THEME);
            }
            c.header(AntdUiBuilder.div(operationUiContext, d ->{
                d.style(AntdAdminStyles.parseStyle("width=100%;display=flex;flexDirection=row;alignItems=center;lineHeight=20px;height=50px"));
                d.img("demo/logo.svg", null, "45px",AntdAdminStyles.parseStyle("display=inline-block"));
                d.div("ru".equals(lang)? "Веб аватар": "Web peer", AntdAdminStyles.parseStyle("fontSize=token:fontSizeHeading2;fontWeight=token:fontWeightStrong"), AntdAdminStyles.STANDARD_PADDING);
                d.hGlue();
                d.dropdownImage(dd ->{
                    dd.menuItem("en", "classpath/demo/en-flag.png", "english", "20px", null, (ctx)->{
                        //TODO implement
                    });
                    dd.menuItem("ru", "classpath/demo/ru-flag.png", "русский", "20px", null, (ctx)->{
                        //TODO implement
                    });
                    if("en".equals(lang)){
                        dd.selectItem("en");
                    } else {
                        dd.selectItem("ru");
                    }
                }, AntdAdminStyles.STANDARD_PADDING);
                d.dropdownIcon( dd ->{
                    dd.menuItem("light", AntdIcons.SUN_OUTLINED.name(), "Light", (ctx)->{
                        ctx.setLocalStorageParam("theme", "light");
                        AntdMainFrame.lookup(ctx).setTheme(mobile? Constants.LIGHT_MOBILE_THEME: Constants.LIGHT_DESKTOP_THEME, ctx);
                    });
                    dd.menuItem("dark", AntdIcons.MOON_FILLED.name(), "Dark", (ctx)->{
                        ctx.setLocalStorageParam("theme", "dark");
                        AntdMainFrame.lookup(ctx).setTheme(mobile? Constants.DARK_MOBILE_THEME: Constants.DARK_DESKTOP_THEME, ctx);
                    });
                    if("dark".equals(operationUiContext.getStringLocalStorageParam("theme"))){
                        dd.selectItem("dark");
                    } else {
                        dd.selectItem("light");
                    }
                }, AntdAdminStyles.STANDARD_PADDING);

            }), AntdAdminStyles.SMALL_PADDING);
            c.menu(menu ->{
                for(var n =1; n<3;n++){
                    var fn = n;
                    menu.group(String.format("%s %s","ru".equals(lang)? "Группа": "Group", n), AntdIcons.SUN_OUTLINED.name(), g->{
                        for(var m =1; m<10;m++){
                            g.item(String.format("%s %s - %s","ru".equals(lang)? "Элемент": "Item", fn, m), String.format("/view-%s", fn));
                        }
                    });
                }
            });

        }));
    }
}
