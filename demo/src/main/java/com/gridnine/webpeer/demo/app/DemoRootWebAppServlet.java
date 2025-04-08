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

import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.mainFrame.AntdMainFrame;
import com.gridnine.webpeer.core.servlet.BaseWebAppServlet;
import com.gridnine.webpeer.core.servlet.CoreWebAppModule;
import com.gridnine.webpeer.core.servlet.WebAppModule;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.UiElement;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class DemoRootWebAppServlet extends BaseWebAppServlet {
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
    protected UiElement createRootElement(OperationUiContext operationUiContext) throws Exception {
        return new AntdMainFrame(GlobalUiContext.getParameter(GlobalUiContext.UI_MODEL), frame ->{
            var lang = operationUiContext.getStringLocalStorageParam("lang");
            var dataSource = new DemoDataSource(lang);
            String initPath = "/";
            var params = operationUiContext.getParameter(OperationUiContext.PARAMS);
            if(params != null) {
                initPath = params.get("initPath").getAsString();
            }
            frame.menu(menu ->{
                for(var n =1; n<3;n++){
                    var fn = n;
                    menu.group(String.format("%s %s","ru".equals(lang)? "Группа": "Group", n), AntdIcons.SUN_OUTLINED.name(), g->{
                       for(var m =1; m<10;m++){
                           g.item(String.format("%s %s - %s","ru".equals(lang)? "Элемент": "Item", fn, m), String.format("/view-%s", fn));
                       }
                   });
                }
            });
            if("dark".equals(operationUiContext.getStringLocalStorageParam("theme"))){
                frame.theme(Constants.DARK_THEME);
            } else {
                frame.theme(Constants.LIGHT_THEME);
            }
            frame.header("padding=0;height=60px;display=flex;flexDirection=row;alignItems=center", d ->{
                d.img("demo/logo.svg", null, "60px", null);
                d.div("fontSize=token:fontSizeHeading2;fontWeight=token:fontWeightStrong;padding=0px;marginBottom=5px", "ru".equals(lang)? "Веб аватар": "Web peer");
                d.hGlue();
                d.dropdownImage(null, dd ->{
                    dd.menuItem("en", "classpath/demo/en-flag.png", "english", "20px", null, (ctx)->{
                       AntdMainFrame.lookup().setLang("en", ctx);
                    });
                    dd.menuItem("ru", "classpath/demo/ru-flag.png", "русский", "20px", null, (ctx)->{
                        AntdMainFrame.lookup().setLang("ru", ctx);
                    });
                    if("ru".equals(operationUiContext.getStringLocalStorageParam("lang"))){
                        dd.selectItem("ru");
                    } else {
                        dd.selectItem("en");
                    }
                });
                d.dropdownIcon(null,  dd ->{
                    dd.menuItem("light", AntdIcons.SUN_OUTLINED.name(), "Light", (ctx)->{
                        AntdMainFrame.lookup().setTheme(Constants.LIGHT_THEME, ctx);
                        ctx.setLocalStorageParam("theme", "light");
                    });
                    dd.menuItem("dark", AntdIcons.MOON_FILLED.name(), "Dark", (ctx)->{
                        AntdMainFrame.lookup().setTheme(Constants.DARK_THEME, ctx);
                        ctx.setLocalStorageParam("theme", "dark");
                    });
                    if("dark".equals(operationUiContext.getStringLocalStorageParam("theme"))){
                        dd.selectItem("dark");
                    } else {
                        dd.selectItem("light");
                    }
                });
            });
            frame.viewProvider(initPath, path ->{
                return new DemoEntitiesList(lang, dataSource);
            });
        });
    }
}
