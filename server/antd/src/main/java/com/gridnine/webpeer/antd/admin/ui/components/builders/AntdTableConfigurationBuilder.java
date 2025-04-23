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

package com.gridnine.webpeer.antd.admin.ui.components.builders;

import com.gridnine.webpeer.antd.admin.ui.components.table.*;
import com.gridnine.webpeer.core.utils.RunnableWithExceptionAndArgument;
import com.gridnine.webpeer.core.utils.WebPeerUtils;

import java.util.List;

public class AntdTableConfigurationBuilder extends BaseAntdConfigurationBuilder<AntdTableConfiguration> {

    private AntdTableConfigurationBuilder(){
        super(new AntdTableConfiguration());
    }

    public void standardColumn(String id, String name, boolean sortable, AntdTableColumnAlignment alignment, Integer width){
        var column = new AntdTableColumnDescription();
        column.setId(id);
        column.setName(name);
        column.setSortable(sortable);
        column.setAlignment(alignment);
        column.setWidth(width);
        column.setType(AntdTableColumnType.STANDARD);
        config.getColumns().add(column);
    }

    public void columns(List<AntdTableColumnDescription> columns){
        config.setColumns(columns);
    }

    public void initSort(AntdTableSort sort){
        config.setInitSort(sort);
    }

    public void dataProvider(AntdTableDataProvider dataProvider){
        config.setDataProvider(dataProvider);
    }

    public static AntdTableConfiguration createConfiguration(RunnableWithExceptionAndArgument<AntdTableConfigurationBuilder> configurator){
        var builder = new AntdTableConfigurationBuilder();
        return WebPeerUtils.wrapException(() ->{
            configurator.run(builder);
            return builder.config;
        });
    }

}
