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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.gridnine.webpeer.core.ui.OperationUiContext;
import com.gridnine.webpeer.core.utils.CallableWithExceptionAnd2Arguments;
import com.gridnine.webpeer.core.utils.CallableWithExceptionAnd3Arguments;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndTwoArguments;

import java.util.List;

public interface UiServletInterceptor<T> {

    default JsonElement onRequest(String commandId, JsonElement request, OperationUiContext context, CallableWithExceptionAnd3Arguments<JsonElement, String, JsonElement, OperationUiContext> callback) throws Exception {
        return callback.call(commandId, request, context);
    }

    default T onInit(OperationUiContext context, JsonObject state, CallableWithExceptionAnd2Arguments<T,OperationUiContext, JsonObject> callback) throws Exception {
        return callback.call(context, state);
    }

    default void onCommand(List<JsonObject> commands, OperationUiContext context, RunnableWithExceptionAndTwoArguments<List<JsonObject>, OperationUiContext> callback) throws Exception{
        callback.run(commands, context);
    }
}
