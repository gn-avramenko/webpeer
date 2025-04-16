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

package com.gridnine.webpeer.antd.admin.ui.mainFrame;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.builder.AntdAdminStyles;
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.breakpoint.AntdBreakpoint;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.icon.AntdIcon;
import com.gridnine.webpeer.antd.admin.ui.components.layout.*;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenu;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdRouter;
import com.gridnine.webpeer.antd.admin.ui.components.theme.AntdTheme;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.RootUiElement;
import com.gridnine.webpeer.core.ui.UiModel;

import java.util.HashMap;
import java.util.Map;

public class AntdMainFrame extends AntdBreakpoint implements RootUiElement {

    public static String MOBILE_BREAKPOINT = "mobile";

    public static String DESKTOP_BREAKPOINT = "desktop";

    private final UiModel model;

    private AntdTheme theme;

    private AntdRouter router;

    private AntdDrawer drawer;

    public static AntdMainFrame lookup(OperationUiContext context) {
        return (AntdMainFrame) context.getParameter(GlobalUiContext.UI_MODEL).getRootElement();
    }

    public void navigate(String path, OperationUiContext ctx){
        if(drawer != null){
            drawer.setOpen(false, ctx);
        }
        router.setPath(path, ctx);
    }
    public AntdMainFrame(UiModel model, JsonObject data, OperationUiContext ctx, AntdMainFrameConfiguration configuration) {
        super(data, ctx);
        this.model = model;
        model.setRootElement(this);
        final Map<String, Object> breakPoints = new HashMap<>();
        breakPoints.put(MOBILE_BREAKPOINT, 0);
        if (configuration.getDesktopWidth() > 0) {
            breakPoints.put(DESKTOP_BREAKPOINT, configuration.getDesktopWidth());
        }
        setBreakpoints(breakPoints);
        var breakpoint = data == null || !data.has("breakpoint")? null : data.get("breakpoint").getAsString();
        if (breakpoint == null) {
            return;
        }
        String initPath = "/";
        var params = ctx.getParameter(OperationUiContext.PARAMS);
        if(params != null) {
            initPath = params.get("initPath").getAsString();
        }
        theme = new AntdTheme(ctx, configuration.getTheme());
        UiModel.addElement(theme, this);
        var layout = new AntdLayout(ctx);
        var layoutStyle = new HashMap<String, Object>();
        layoutStyle.put("height", "100%");
        layoutStyle.put("borderRadius", "token:borderRadiusLG");
        layout.setStyle(layoutStyle);
        layout.setTag("layout");
        UiModel.addElement(layout, theme);
        if (DESKTOP_BREAKPOINT.equals(breakpoint)) {
            {
                var header = new AntdHeader(ctx);
                header.setTag("header");
                header.setStyle(configuration.getHeaderStyle());
                if (configuration.getHeader() != null) {
                    UiModel.addElement(configuration.getHeader(), header);
                }
                UiModel.addElement(header, layout);
            }
            {
                var content = new AntdContent(ctx);
                var contentStyle = new HashMap<String, Object>();
                contentStyle.put("height", "100%");
                content.setStyle(contentStyle);
                content.setTag("content");
                UiModel.addElement(content, layout);
                {
                    var innerLayout = new AntdLayout(ctx);
                    innerLayout.setTag("inner-layout");
                    var innerLayoutStyle = new HashMap<String, Object>();
                    innerLayoutStyle.put("height", "100%");
                    innerLayout.setStyle(innerLayoutStyle);
                    UiModel.addElement(innerLayout, content);
                    {
                        var sider = new AntdSider(ctx);
                        sider.setTag("sider");
                        UiModel.addElement(sider, innerLayout);
                        {
                            var menu = new AntdMenu(ctx, configuration.getMenuItems());
                            UiModel.addElement(menu, sider);
                        }
                    }
                    {
                        var innerContent = new AntdContent(ctx);
                        innerContent.setTag("inner-content");
                        UiModel.addElement(innerContent, innerLayout);
                        {
                            router = new AntdRouter(AntdUtils.getFirstChildData(AntdUtils.getFirstChildData(AntdUtils.findUiDataByTag(data,"inner-content"))), initPath, configuration.getViewProvider(), ctx);
                            UiModel.addElement(router, innerContent);
                        }
                    }
                }
            }
            return;
        }
        {
            var header = new AntdHeader(ctx);
            header.setTag("header");
            header.setStyle(configuration.getHeaderStyle());
            header.getStyle().put("display","flex");
            var iconDiv = new AntdDiv(null, ctx);
            iconDiv.setStyle(AntdAdminStyles.parseStyle("lineHeight=35px;padding=token:padding"));
            UiModel.addElement(iconDiv, header);
            iconDiv.setClickHandler((context)->{
                drawer.setOpen(true, context);
            });
            var icon = new AntdIcon(AntdIcons.MENU_FOLD_OUTLINED.name(), ctx);
            UiModel.addElement(icon, iconDiv);
            if (configuration.getHeader() != null) {
                UiModel.addElement(configuration.getHeader(), header);
            }
            UiModel.addElement(header, layout);
            {
                {
                    var content = new AntdContent(ctx);
                    var contentStyle = new HashMap<String, Object>();
                    contentStyle.put("height", "100%");
                    content.setStyle(contentStyle);
                    content.setTag("content");
                    UiModel.addElement(content, layout);
                    {
                        drawer  = new AntdDrawer(false, ctx);
                        drawer.setTag("drawer");
                        UiModel.addElement(drawer, content);
                        {
                            var menu = new AntdMenu(ctx, configuration.getMenuItems());
                            menu.getStyle().put("height", "100%");
                            menu.getStyle().put("overflowY", "auto");
                            UiModel.addElement(menu, drawer);
                        }
                    }
                    {
                        var innerLayout = new AntdLayout(ctx);
                        innerLayout.setTag("inner-layout");
                        var innerLayoutStyle = new HashMap<String, Object>();
                        innerLayoutStyle.put("height", "100%");
                        innerLayout.setStyle(innerLayoutStyle);
                        UiModel.addElement(innerLayout, content);
                        {
                            var innerContent = new AntdContent(ctx);
                            innerContent.setTag("inner-content");
                            UiModel.addElement(innerContent, innerLayout);
                            {
                                router = new AntdRouter(AntdUtils.getFirstChildData(AntdUtils.getFirstChildData(AntdUtils.findUiDataByTag(data,"inner-content"))), initPath, configuration.getViewProvider(), ctx);
                                UiModel.addElement(router, innerContent);
                            }
                        }
                    }

                }
            }

        }
    }

    public void setTheme(JsonObject theme, OperationUiContext ctx) {
        this.theme.setTheme(theme, ctx);
    }

    @Override
    public UiModel getModel() {
        return model;
    }
}
