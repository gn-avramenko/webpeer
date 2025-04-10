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

package com.gridnine.webpeer.core.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebPeerUtils {
    public static void wrapException(RunnableWithException body) {
        try {
            body.run();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static <T> T wrapException(Callable<T> body) {
        try {
            return body.call();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private static JsonElement getElement(JsonObject json, String key) {
        if(json.has(key)){
            return json.get(key);
        }
        Map.Entry<String, JsonElement> entry = json.entrySet().stream().filter(it -> it.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
        if(entry == null){
            return null;
        }
        return entry.getValue();
    }

    public static String getString(JsonObject json, String key) {
        JsonElement jsonElement = getElement(json, key);
        return jsonElement == null ? null : jsonElement.getAsString();
    }
    public static long getLong(JsonObject json, String key, long defaultValue) {
        JsonElement jsonElement = getElement(json, key);
        return jsonElement == null ? defaultValue : jsonElement.getAsLong();
    }

    public static int getInt(JsonObject json, String key, int defaultValue) {
        JsonElement jsonElement =getElement(json, key);
        return jsonElement == null ? defaultValue : jsonElement.getAsInt();
    }

    public static boolean getBoolean(JsonObject json, String key, boolean defaultValue) {
        JsonElement jsonElement = getElement(json, key);
        return jsonElement == null ? defaultValue : jsonElement.getAsBoolean();
    }

    private static List<JsonElement> toList(JsonArray json) {
        List<JsonElement> res = new ArrayList<>();
        json.iterator().forEachRemaining(res::add);
        return res;
    }
    public static<C> List<C> getEnumsList(JsonObject json, String key, Function<String, C> converter) {
        JsonElement element = getElement(json, key);
        if(element == null) {
            return Collections.emptyList();
        }
        return toList(element.getAsJsonArray()).stream().map(it -> converter.apply(it.getAsString())).collect(Collectors.toList());
    }
    public static<C> C getEnum(JsonObject json, String key, Function<String, C> converter) {
        JsonElement element = getElement(json, key);
        if(element == null) {
            return null;
        }
        return converter.apply(element.getAsString());
    }




    public static List<String> getStringsList(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if(element == null) {
            return Collections.emptyList();
        }
        return toList(element.getAsJsonArray()).stream().map(JsonElement::getAsString).collect(Collectors.toList());
    }


    public static Instant getInstant(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if(element == null) {
            return null;
        }
        String str = element.getAsString();
        return ZonedDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).toInstant();
    }

    public static BigDecimal getBigDecimal(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if(element == null) {
            return null;
        }
        return element.getAsBigDecimal();
    }

    public static JsonElement getDynamic(JsonObject obj, String key) {
        return getElement(obj, key);
    }

    public static boolean isBlank(String text){
        return text == null || text.trim().isEmpty();
    }
    public static boolean isNotBlank(String text){
        return !isBlank(text);
    }

    public static JsonObject serialize(Map<String,Object> map) {
        JsonObject json = new JsonObject();
        map.forEach((k,v)->{
            addProperty(json, k, v);
        });
        return json;
    }

    private static void addProperty(JsonObject props, String k, Object v) {
       wrapException(() ->{
            if(v instanceof String){
                props.addProperty(k, (String) v);
            } else if(v instanceof Number){
                props.addProperty(k, (Number) v);
            } else if(v instanceof Map){
                JsonObject obj = new JsonObject();
                var map = (Map<String,Object>) v;
                map.forEach((k2, v2)->{
                    WebPeerUtils.wrapException(()->{
                        addProperty(obj, k2, v2);
                    });
                });
                props.add(k, obj);
            } else {
                props.add(k, (JsonObject)v);
            }
        });

    }
}
