package pw.phylame.jiaws.servlet.impl;

import static pw.phylame.jiaws.util.ImplementUtils.raiseForDeprecated;
import static pw.phylame.jiaws.util.ImplementUtils.raiseForTodo;

import java.lang.ref.WeakReference;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

class HttpSessionImpl extends AttributeSupport implements HttpSession, ServletContextAware {
    private final String id;

    private boolean invalidated = false;

    private int maxInactiveInterval = -1;

    private long creationTime = new Date().getTime();

    private long lastAccessed = creationTime;

    private WeakReference<ServletContext> contextRef;

    HttpSessionImpl(String id) {
        this.id = id;
    }

    private void ensureNotInvalidated() throws IllegalStateException {
        if (invalidated) {
            throw new IllegalStateException("Session is invalidated");
        }
    }

    @Override
    public long getCreationTime() {
        ensureNotInvalidated();
        return creationTime;
    }

    @Override
    public String getId() {
        ensureNotInvalidated();
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        ensureNotInvalidated();
        return lastAccessed;
    }

    @Override
    public ServletContext getServletContext() {
        return contextRef.get();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
        raiseForTodo("Container check for timeout");
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return raiseForDeprecated();
    }

    @Override
    protected void checkAccessible() throws IllegalStateException {
        ensureNotInvalidated();
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public String[] getValueNames() {
        return getAttributeKeys().toArray(new String[getAttributeSize()]);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        ensureNotInvalidated();
        invalidated = true;
    }

    @Override
    public boolean isNew() {
        ensureNotInvalidated();
        return false;
    }

    @Override
    public void setServletContext(ServletContext context) {
        contextRef = new WeakReference<>(context);
    }
}
