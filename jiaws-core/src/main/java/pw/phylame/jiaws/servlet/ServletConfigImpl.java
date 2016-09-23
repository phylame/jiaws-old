/*
 * Copyright 2014-2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.jiaws.servlet;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import lombok.NonNull;

class ServletConfigImpl implements ServletConfig, ServletContextAware {
    private WeakReference<ServletContext> contextRef;

    private final String name;

    private final Map<String, String> params;

    ServletConfigImpl(@NonNull String name, @NonNull Map<String, String> params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public String getServletName() {
        return name;
    }

    @Override
    public ServletContext getServletContext() {
        return contextRef.get();
    }

    @Override
    public String getInitParameter(String name) {
        return params.get(name);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public void setServletContext(@NonNull ServletContext context) {
        contextRef = new WeakReference<ServletContext>(context);
    }

}
