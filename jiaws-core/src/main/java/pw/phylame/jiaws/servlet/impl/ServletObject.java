package pw.phylame.jiaws.servlet.impl;

import java.lang.ref.WeakReference;

import javax.servlet.ServletContext;

import lombok.NonNull;

public class ServletObject extends AttributeSupport implements ServletContextAware {
    private WeakReference<ServletContext> contextRef;

    public ServletContext getServletContext() {
        return contextRef.get();
    }

    @Override
    public void setServletContext(@NonNull ServletContext context) {
        contextRef = new WeakReference<ServletContext>(context);
    }

}
