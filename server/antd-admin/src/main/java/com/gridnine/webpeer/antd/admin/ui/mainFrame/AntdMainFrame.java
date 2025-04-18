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
import com.gridnine.webpeer.antd.admin.ui.components.AntdIcons;
import com.gridnine.webpeer.antd.admin.ui.components.breakpoint.AntdBreakpoint;
import com.gridnine.webpeer.antd.admin.ui.components.breakpoint.AntdBreakpointConfiguration;
import com.gridnine.webpeer.antd.admin.ui.components.builders.AntdBreakpointConfigurationBuilder;
import com.gridnine.webpeer.antd.admin.ui.components.common.AntdUtils;
import com.gridnine.webpeer.antd.admin.ui.components.layout.*;
import com.gridnine.webpeer.antd.admin.ui.components.router.AntdRouter;
import com.gridnine.webpeer.antd.admin.ui.components.theme.AntdTheme;
import com.gridnine.webpeer.core.ui.GlobalUiContext;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.ui.RootUiElement;
import com.gridnine.webpeer.core.ui.UiModel;


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

    public void navigate(String path, OperationUiContext ctx) {
        if (drawer != null) {
            drawer.setOpen(false, ctx);
        }
        router.setPath(path, ctx);
    }


    @Override
    protected AntdBreakpointConfiguration createConfiguration(JsonObject uiData, Object config, OperationUiContext ctx) {
        AntdMainFrameConfiguration conf = (AntdMainFrameConfiguration) config;
        return AntdBreakpointConfigurationBuilder.createConfiguration(uiData, (bp) -> {
            bp.breakPoint(MOBILE_BREAKPOINT, 0);
            if (conf.getDesktopWidth() > 0) {
                bp.breakPoint(DESKTOP_BREAKPOINT, conf.getDesktopWidth());
            }
            String initPath = ctx.getParameter(OperationUiContext.PARAMS) != null ? ctx.getParameter(OperationUiContext.PARAMS).get("initPath").getAsString() : "/";
            theme = bp.theme(ctx, th -> {
                th.layout(ctx, ml -> {
                    ml.tag("main-layout");
                    ml.style("height=100%;borderRadius=token:borderRadiusLG");
                    if (DESKTOP_BREAKPOINT.equals(bp.currentBreakPoint())) {
                        ml.header(ctx, mh -> {
                            mh.tag("main-header");
                            if (conf.getHeader() != null) {
                                mh.appendChild(conf.getHeader());
                            }
                            if (!conf.getHeaderStyle().isEmpty()) {
                                mh.style(conf.getHeaderStyle());
                            }
                        });
                        ml.content(ctx, mc -> {
                            mc.tag("main-content");
                            mc.style("height=100%");
                            mc.layout(ctx, il -> {
                                il.tag("inner-layout");
                                il.style("height=100%");
                                il.sider(ctx, ins -> {
                                    ins.tag("sider");
                                    ins.menu(ctx, m -> m.menuItems(conf.getMenuItems()));
                                });
                                il.content(ctx, ic -> {
                                    ic.tag("inner-content");
                                    router = ic.router(AntdUtils.getFirstChildData(AntdUtils.getFirstChildData(AntdUtils.findUiDataByTag(uiData, "inner-content"))),
                                            ctx, router -> {
                                                router.initPath(initPath);
                                                router.viewProvider(conf.getViewProvider());
                                            });
                                });
                            });
                        });
                        return;
                    }
                    ml.header(ctx, mh -> {
                        mh.tag("main-header");
                        mh.style(conf.getHeaderStyle());
                        mh.style("display","flex");
                        mh.div(null, ctx, d ->{
                            d.style("lineHeight=35px;padding=token:padding");
                            d.clickHandler(c ->{
                                drawer.setOpen(false, c);
                            });
                            d.icon(ctx, i -> i.icon(AntdIcons.MENU_FOLD_OUTLINED.name()));
                        });
                        if(conf.getHeader() != null){
                            mh.appendChild(conf.getHeader());
                        }
                    });
                    ml.content(ctx, mc -> {
                        mc.tag("main-content");
                        mc.style("height=100%");
                        drawer = mc.drawer(ctx, drawer->{
                            setTag("drawer");
                            drawer.menu(ctx, m -> m.menuItems(conf.getMenuItems()));
                        });
                        mc.layout(ctx, il -> {
                            il.tag("inner-layout");
                            il.style("height=100%");
                            il.content(ctx, ic -> {
                                ic.tag("inner-content");
                                router = ic.router(AntdUtils.getFirstChildData(AntdUtils.getFirstChildData(AntdUtils.findUiDataByTag(uiData, "inner-content"))),
                                        ctx, router -> {
                                            router.initPath(initPath);
                                            router.viewProvider(conf.getViewProvider());
                                        });
                            });
                        });
                    });
                });
            });

        });
    }

    public AntdMainFrame(JsonObject uiData, OperationUiContext ctx, AntdMainFrameConfiguration conf) {
        super(uiData, conf, ctx);
        this.model = ctx.getParameter(GlobalUiContext.UI_MODEL);
        model.setRootElement(this);

    }

    public void setTheme(JsonObject theme, OperationUiContext ctx) {
        this.theme.setTheme(theme, ctx);
    }

    @Override
    public UiModel getModel() {
        return model;
    }
}
