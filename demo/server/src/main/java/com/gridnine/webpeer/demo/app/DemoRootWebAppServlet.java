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

import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.servlet.BaseWebAppServlet;
import com.gridnine.webpeer.core.servlet.WebAppModule;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.demo.app.components.DemoRootElement;
import com.gridnine.webpeer.demo.app.data.DemoDataSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class DemoRootWebAppServlet extends BaseWebAppServlet<DemoRootElement> {

    @Autowired
    private DemoDataSource demoDataSource;

    @Override
    protected DemoRootElement createRootElement(JsonObject uiData, OperationUiContext operationUiContext) throws Exception {
        return new DemoRootElement(uiData, demoDataSource, operationUiContext);
    }

    @Override
    protected List<WebAppModule> getModules() throws Exception {
        return List.of();
    }

    @Override
    protected URL getFaviconUrl() {
        return getClass().getClassLoader().getResource("demo/favicon.ico");
    }

    @Override
    protected Map<String, String> getWebAppParameters() {
        return Map.of();
    }

    @Override
    protected String getTitle() {
        return "Demo App";
    }

}
