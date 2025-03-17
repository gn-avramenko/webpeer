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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Constants {
    public static final JsonObject LIGHT_THEME = new JsonObject();
    public static final JsonObject DARK_THEME = new JsonObject();
    static {
        {
            var desktopAlgorithm = new JsonArray();
            desktopAlgorithm.add("darkAlgorithm");
            DARK_THEME.add("desktopAlgorithm", desktopAlgorithm);
            var mobileAlgorithm = new JsonArray();
            mobileAlgorithm.add("darkAlgorithm");
            mobileAlgorithm.add("compactAlgorithm");
            DARK_THEME.add("mobileAlgorithm", mobileAlgorithm);
        }
        {

            var components = new JsonObject();
            var layout = new JsonObject();
            components.add("Layout", layout);
            layout.addProperty( "headerColor", "rgb(255,255,255)");
            layout.addProperty( "siderBg", "rgb(255,255,255)");
            layout.addProperty( "lightTriggerColor", "rgb(255,255,255)");
            layout.addProperty( "triggerBg", "rgb(255,255,255)");
            LIGHT_THEME.add("components", components);
            {
                var desktopAlgorithm = new JsonArray();
                desktopAlgorithm.add("defaultAlgorithm");
                LIGHT_THEME.add("desktopAlgorithm", desktopAlgorithm);
                var mobileAlgorithm = new JsonArray();
                mobileAlgorithm.add("defaultAlgorithm");
                mobileAlgorithm.add("compactAlgorithm");
                LIGHT_THEME.add("mobileAlgorithm", mobileAlgorithm);
            }
        }
    }
}
