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

package com.gridnine.webpeer.core.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebAppModule {
    public final String moduleId;
    public final List<HtmlScriptWrapper> scripts;
    public final List<HtmlLinkWrapper> css;

    private static synchronized List<String> getResourcesFromJar(URL jarUrl, String path) throws IOException, URISyntaxException {
        var resourceNames = new ArrayList<String>();

        try (var fileSystem = FileSystems.newFileSystem(jarUrl.toURI(), Collections.emptyMap())) {
            var jarPath = fileSystem.getPath(path);

            try (var walk = Files.walk(jarPath, 1)) {
                walk.filter(Files::isRegularFile)
                        .forEach(file -> {
                            String fileName = file.getFileName().toString();
                            resourceNames.add(path + "/" + fileName);
                        });
            }
        }

        return resourceNames;
    }

    private static List<String> getResourcesFromFile(URL fileUrl, String baseUrl) throws URISyntaxException {
        List<String> resourceNames = new ArrayList<>();
        File dir = new File(fileUrl.toURI());

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        resourceNames.add(String.format("%s/%s", baseUrl, file.getName()));
                    }
                }
            }
        }

        return resourceNames;
    }


    public WebAppModule(String moduleId, List<HtmlScriptWrapper> scripts, List<HtmlLinkWrapper> css) {
        this.moduleId = moduleId;
        this.scripts = scripts;
        this.css = css;
    }

    public WebAppModule(String moduleId, String baseUrl, ClassLoader classLoader) throws IOException, URISyntaxException {
        this.moduleId = moduleId;
        var scripts = new ArrayList<HtmlScriptWrapper>();
        var css = new ArrayList<HtmlLinkWrapper>();
        var url = classLoader.getResource(baseUrl);
        List<String> urls;
        if (url.getProtocol().equals("jar")) {
            urls = getResourcesFromJar(url, baseUrl);
        } else {
            urls = getResourcesFromFile(url, baseUrl);
        }
        for (var resource: urls) {
            if(resource.lastIndexOf(".") < resource.lastIndexOf("/")) {
                continue;
            }
            String baseName = resource.substring(resource.lastIndexOf('/')+1, resource.lastIndexOf("."));
            if(resource.endsWith(".js")){
                String jsMapPath = resource+".map";
                var jsMap  = classLoader.getResource(jsMapPath);
                scripts.add(new HtmlScriptWrapper(baseName+".js", classLoader.getResource(resource), jsMap == null? null: baseName+".js.map", jsMap));
            }
            if(resource.endsWith(".css")){
                css.add(new HtmlLinkWrapper(baseName+".css", classLoader.getResource(resource), "stylesheet", "text/css"));
            }
        }
        this.scripts = scripts;
        this.css = css;
    }
}
