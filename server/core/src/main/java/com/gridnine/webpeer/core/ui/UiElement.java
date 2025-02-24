package com.gridnine.webpeer.core.ui;


import java.util.Map;

public interface UiElement {
    UiNode createNode(Map<String, Object> context) throws Exception;
}
