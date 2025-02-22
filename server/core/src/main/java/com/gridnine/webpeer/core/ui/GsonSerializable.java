package com.gridnine.webpeer.core.ui;

import com.google.gson.JsonElement;

public interface GsonSerializable {
    JsonElement serialize() throws Exception;
}
