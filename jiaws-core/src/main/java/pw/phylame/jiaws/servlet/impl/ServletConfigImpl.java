package pw.phylame.jiaws.servlet.impl;

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import lombok.NonNull;
import pw.phylame.jiaws.util.Enumerations;

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
        return Enumerations.forCollection(params.keySet());
    }

    @Override
    public void setServletContext(@NonNull ServletContext context) {
        contextRef = new WeakReference<ServletContext>(context);
    }

}
