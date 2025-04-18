package com.gridnine.webpeer.antd.admin.ui.components.breakpoint;

import com.google.gson.JsonObject;
import com.gridnine.webpeer.antd.admin.ui.components.common.BaseAntdConfiguration;

import java.util.HashMap;
import java.util.Map;

public class AntdBreakpointConfiguration extends BaseAntdConfiguration {
    private final String breakPoint;

    private final Map<String, Object> breakpoints = new HashMap<String, Object>();

    public AntdBreakpointConfiguration(JsonObject uiData) {
        breakPoint = uiData == null || !uiData.has("breakpoint")? null : uiData.get("breakpoint").getAsString();
    }

    public String getBreakPoint() {
        return breakPoint;
    }

    public Map<String, Object> getBreakPoints() {
        return breakpoints;
    }
}
