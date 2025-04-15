package com.gridnine.webpeer.antd.admin.ui.builder;

import com.gridnine.webpeer.antd.admin.ui.components.common.AntdUtils;

import java.util.HashMap;
import java.util.Map;

public final class AntdAdminStyles {
    public static final Style GLUE = new Style();

    public static final Style SMALL_PADDING = new Style();

    public static final Style STANDARD_PADDING = new Style();

    public static Style parseStyle(final String style) {
        var result = new Style();
        result.putAll(AntdUtils.parseStyle(style));
        return result;
    }

    static {
        GLUE.put("flexGrow", 1);
        SMALL_PADDING.put("padding", 5);
        STANDARD_PADDING.put("padding", "token:padding");
    }
}
