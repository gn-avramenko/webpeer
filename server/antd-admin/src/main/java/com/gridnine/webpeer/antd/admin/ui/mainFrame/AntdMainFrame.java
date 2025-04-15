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
import com.gridnine.webpeer.antd.admin.ui.components.breakpoint.AntdBreakpoint;
import com.gridnine.webpeer.antd.admin.ui.components.div.AntdDiv;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdContent;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdHeader;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdLayout;
import com.gridnine.webpeer.antd.admin.ui.components.layout.AntdSider;
import com.gridnine.webpeer.antd.admin.ui.components.menu.AntdMenu;
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

    public static AntdMainFrame lookup(OperationUiContext context) {
        return (AntdMainFrame) context.getParameter(GlobalUiContext.UI_MODEL).getRootElement();
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
        var breakpoint = ctx.getParameter(AntdBreakpoint.BREAKPOINT);
        if (breakpoint == null) {
            return;
        }
        theme = new AntdTheme(ctx, configuration.getTheme());
        UiModel.addElement(theme, this);
        var layout = new AntdLayout(ctx);
        var layoutStyle = new HashMap<String,Object>();
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
                var contentStyle = new HashMap<String,Object>();
                contentStyle.put("height", "100%");
                content.setStyle(contentStyle);
                content.setTag("content");
                UiModel.addElement(content, layout);
                {
                   var innerLayout = new AntdLayout(ctx);
                   innerLayout.setTag("inner-layout");
                   UiModel.addElement(innerLayout,  content);
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
                            var realContent = new AntdDiv(ctx);
                            realContent.setContent("Hello content");
                            UiModel.addElement(realContent, innerContent);
                        }
                    }
                }
            }
            return;
        }

    }

    public void setTheme(JsonObject theme, OperationUiContext ctx){
        this.theme.setTheme(theme, ctx);
    }
    @Override
    public UiModel getModel() {
        return model;
    }
}
