package pw.phylame.jiaws.servlet;

import static pw.phylame.jiaws.util.ImplementUtils.raiseForDeprecated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.Setter;
import lombok.val;
import pw.phylame.jiaws.util.AddressTuple;
import pw.phylame.jiaws.util.Enumerations;
import pw.phylame.jiaws.util.MultiValueMap;
import pw.phylame.jiaws.util.Provider;
import pw.phylame.jiaws.util.values.LazyValue;

public abstract class AbstractServletRequest extends ServletObject implements ServletRequest {
    @Setter
    private AddressTuple localAddress;

    @Setter
    private AddressTuple remoteAddress;

    private final LazyValue<Enumeration<String>> parameterNames = new LazyValue<>(new Provider<Enumeration<String>>() {
        @Override
        public Enumeration<String> provide() throws Exception {
            return Enumerations.enumeration(getInternalParameters().keySet());
        }
    });

    private final LazyValue<Map<String, String[]>> parameterMap = new LazyValue<>(
            new Provider<Map<String, String[]>>() {
                @Override
                public Map<String, String[]> provide() throws Exception {
                    Map<String, String[]> result = new HashMap<>();
                    for (Map.Entry<String, Collection<String>> e : getInternalParameters().entrySet()) {
                        result.put(e.getKey(), e.getValue().toArray(new String[0]));
                    }
                    return result;
                }
            });

    private final LazyValue<Enumeration<Locale>> locales = new LazyValue<>(new Provider<Enumeration<Locale>>() {
        @Override
        public Enumeration<Locale> provide() throws Exception {
            val locales = getInternalLocales();
            return !locales.isEmpty() ? Enumerations.enumeration(locales) : Enumerations.singleton(Locale.getDefault());
        }
    });

    @Override
    public final int getContentLength() {
        long length = getContentLengthLong();
        return length > Integer.MAX_VALUE ? -1 : (int) length;
    }

    protected abstract MultiValueMap<String, String> getInternalParameters();

    @Override
    public final String getParameter(String name) {
        return getInternalParameters().getFirst(name);
    }

    @Override
    public final Enumeration<String> getParameterNames() {
        return parameterNames.get();
    }

    @Override
    public final String[] getParameterValues(String name) {
        val values = getInternalParameters().get(name);
        return values != null ? values.toArray(new String[values.size()]) : null;
    }

    @Override
    public final Map<String, String[]> getParameterMap() {
        return parameterMap.get();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public final String getRemoteAddr() {
        return remoteAddress.getIp();
    }

    @Override
    public final String getRemoteHost() {
        return remoteAddress.getHost();
    }

    protected abstract List<Locale> getInternalLocales();

    @Override
    public final Locale getLocale() {
        val locales = getInternalLocales();
        return !locales.isEmpty() ? locales.get(0) : Locale.getDefault();
    }

    @Override
    public final Enumeration<Locale> getLocales() {
        return locales.get();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public final String getRealPath(String path) {
        return raiseForDeprecated();
    }

    @Override
    public final int getRemotePort() {
        return remoteAddress.getPort();
    }

    @Override
    public final String getLocalName() {
        return localAddress.getHost();
    }

    @Override
    public final String getLocalAddr() {
        return localAddress.getIp();
    }

    @Override
    public final int getLocalPort() {
        return localAddress.getPort();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return null;
    }

}
