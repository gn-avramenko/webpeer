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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public static JsonObject getObject(JsonObject json, String key) {
        if (json == null) {
            return null;
        }
        var elm = getElement(json, key);
        return elm == null ? null : elm.getAsJsonObject();
    }

    public static JsonElement getElement(JsonElement json, String key) {
        if (json == null || !json.isJsonObject()) {
            return null;
        }
        var obj = json.getAsJsonObject();
        if (obj.has(key)) {
            return obj.get(key);
        }
        Map.Entry<String, JsonElement> entry = obj.entrySet().stream().filter(it -> it.getKey().equalsIgnoreCase(key)).findFirst().orElse(null);
        if (entry == null) {
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
        JsonElement jsonElement = getElement(json, key);
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

    public static <C> List<C> getEnumsList(JsonObject json, String key, Function<String, C> converter) {
        JsonElement element = getElement(json, key);
        if (element == null) {
            return Collections.emptyList();
        }
        return toList(element.getAsJsonArray()).stream().map(it -> converter.apply(it.getAsString())).collect(Collectors.toList());
    }

    public static <C> C getEnum(JsonObject json, String key, Function<String, C> converter) {
        JsonElement element = getElement(json, key);
        if (element == null) {
            return null;
        }
        return converter.apply(element.getAsString());
    }


    public static List<String> getStringsList(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if (element == null) {
            return Collections.emptyList();
        }
        return toList(element.getAsJsonArray()).stream().map(JsonElement::getAsString).collect(Collectors.toList());
    }


    public static Instant getInstant(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if (element == null) {
            return null;
        }
        String str = element.getAsString();
        return ZonedDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME).toInstant();
    }

    public static BigDecimal getBigDecimal(JsonObject json, String key) {
        JsonElement element = getElement(json, key);
        if (element == null) {
            return null;
        }
        return element.getAsBigDecimal();
    }

    public static JsonElement getDynamic(JsonElement obj, String key) {
        return getElement(obj, key);
    }


    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    public static JsonObject serialize(Map<String, Object> map) {
        JsonObject json = new JsonObject();
        map.forEach((k, v) -> {
            addProperty(json, k, v);
        });
        return json;
    }

    public static JsonArray serialize(List<?> lst) {
        JsonArray json = new JsonArray();
        lst.forEach((v) -> {
            var obj = new JsonObject();
            addProperty(obj,"key", v);
            json.add(getDynamic(obj, "key"));
        });
        return json;
    }

    public static void addProperty(JsonObject props, String k, Object v) {
        if (v == null) {
            return;
        }
        wrapException(() -> {
            if (v instanceof JsonElement) {
                props.add(k, (JsonElement) v);
            } else if (v instanceof String) {
                props.addProperty(k, (String) v);
            } else if (v instanceof Number) {
                props.addProperty(k, (Number) v);
            } else if (v instanceof Boolean) {
                props.addProperty(k, (Boolean) v);
            } else if (v instanceof Map) {
                JsonObject obj = new JsonObject();
                var map = (Map<String, Object>) v;
                map.forEach((k2, v2) -> {
                    WebPeerUtils.wrapException(() -> {
                        addProperty(obj, k2, v2);
                    });
                });
                props.add(k, obj);
            } else if (v instanceof List) {
                JsonArray obj = new JsonArray();
                var lst = (List<?>) v;
                lst.forEach(value -> {
                            var obj2 = new JsonObject();
                            WebPeerUtils.addProperty(obj2, "prop", value);
                            obj.add(WebPeerUtils.getDynamic(obj2, "prop"));
                        }
                );
                props.add(k, obj);
            } else {
                var s = (GsonSerializable) v;
                props.add(k, s.serialize());
            }
        });

    }

    public static String getExceptionStackTrace(Throwable t) {
        if (t == null) {
            return null;
        }
        var sb = new StringBuilder(t.getStackTrace().length * 100);
        printError(t, t.toString(), sb);
        return sb.toString();
    }

    private static void printError(Throwable t, String header,
                                   StringBuilder sb) {
        if (t == null) {
            return;
        }
        var nl = System.getProperty("line.separator");
        if (!isBlank(header)) {
            sb.append(nl).append(header).append(nl).append(nl);
        }
        for (var element : t.getStackTrace()) {
            printStackTraceElement(element, sb).append(nl);
        }
        var next = t.getCause();
        printError(next, String.format("Caused by %s", next), sb);
        if (t instanceof SQLException) {
            next = ((SQLException) t).getNextException();
            printError(next, String.format("Next exception: %s", next), sb);
        } else if (t instanceof InvocationTargetException) {
            next = ((InvocationTargetException) t).getTargetException();
            printError(next, String.format("Target exception: %s", next), sb);
        }
    }

    private static StringBuilder printStackTraceElement(StackTraceElement ste, StringBuilder sb) {
        sb.append(String.format("%s.%s(", ste.getClassName(), ste.getMethodName()));
        if (ste.isNativeMethod()) {
            sb.append("Native Method");
        } else if (ste.getFileName() != null) {
            sb.append(String.format("%s%s", ste.getFileName(), ste.getLineNumber() > 0 ? ste.getLineNumber() : ""));
        }
        sb.append(")");
        return sb;
    }

    public static Object getValue(JsonElement pv) {
        if (pv == null || pv.isJsonNull()) {
            return null;
        }
        if (pv.isJsonPrimitive()) {
            var pm = pv.getAsJsonPrimitive();
            if (pm.isBoolean()) {
                return pm.getAsBoolean();
            }
            if (pm.isNumber()) {
                return pm.getAsBigDecimal();
            }
            if (pm.isJsonNull()) {
                return null;
            }
            if (pm.isString()) {
                return pm.getAsString();
            }
            return pm;
        }
        return pv;
    }
}
