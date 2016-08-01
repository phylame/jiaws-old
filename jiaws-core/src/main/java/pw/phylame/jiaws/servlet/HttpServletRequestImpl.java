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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.util.DateUtils;
import pw.phylame.jiaws.util.Enumerations;
import pw.phylame.jiaws.util.IPTuple;
import pw.phylame.jiaws.util.MultiValueMap;
import pw.phylame.jiaws.util.Provider;
import pw.phylame.jiaws.util.StringUtils;
import pw.phylame.jiaws.util.values.LazyValue;
import pw.phylame.jiaws.util.values.MutableLazyValue;

import static pw.phylame.jiaws.util.ImplementUtils.*;

public class HttpServletRequestImpl extends ServletObject implements HttpServletRequest {
    @Setter
    private IPTuple localIP;

    @Setter
    private IPTuple remoteIP;

    @Setter
    private String serverName;

    @Setter
    private int serverPort;

    @Setter
    @Getter
    private String method;

    @Setter
    private String path;

    @Setter
    private String query;

    @Setter
    @Getter
    private String protocol;

    @Getter
    private MultiValueMap<String, String> parameters = new MultiValueMap<>();

    @Getter
    private MultiValueMap<String, String> headers = new MultiValueMap<>();

    /**
     * Indicates that the input is processed.
     */
    private boolean doneInput = false;

    private LazyValue<List<Locale>> locales = new LazyValue<>(new Provider<List<Locale>>() {
        @Override
        public List<Locale> provide() {
            List<Locale> locales = new LinkedList<>();
            for (String code : Enumerations.asIterable(getHeaders("Accept-Language"))) {
                locales.add(Locale.forLanguageTag(code));
            }
            return locales;
        }
    });

    private MutableLazyValue<String> encoding = new MutableLazyValue<>(new Provider<String>() {
        @Override
        public String provide() {
            String s = getHeader("Character-Encoding");
            return s != null ? StringUtils.getSecondPartOf(s, ';') : null;
        }
    });

    private void ensureInputNotProcessed() {
        if (doneInput) {
            throw new IllegalStateException("Input of the request has been processed");
        }
    }

    @Override
    public String getCharacterEncoding() {
        return encoding.get();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        encoding.set(env);
    }

    @Override
    public int getContentLength() {
        long length = getContentLengthLong();
        return length > Integer.MAX_VALUE ? -1 : (int) length;
    }

    @Override
    public long getContentLengthLong() {
        String str = getHeader("Content-Length");
        if (str == null) {
            return -1;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    @Override
    public String getContentType() {
        String s = getHeader("Content-Type");
        return s != null ? StringUtils.getFirstPartOf(s, ';').trim() : null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return parameters.getFirst(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Enumerations.forCollection(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        Collection<String> c = parameters.get(name);
        return c != null ? c.toArray(new String[c.size()]) : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> result = new HashMap<>();
        for (Map.Entry<String, Collection<String>> e : parameters.entrySet()) {
            result.put(e.getKey(), e.getValue().toArray(new String[0]));
        }
        return result;
    }

    @Override
    public String getScheme() {
        return StringUtils.getFirstPartOf(getProtocol(), '/').toLowerCase();
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ensureInputNotProcessed();
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public String getRemoteAddr() {
        return remoteIP.getIp();
    }

    @Override
    public String getRemoteHost() {
        return remoteIP.getHost();
    }

    @Override
    public Locale getLocale() {
        List<Locale> locales = this.locales.get();
        return locales.isEmpty() ? Locale.getDefault() : locales.get(0);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Enumerations.forCollection(locales.get());
    }

    @Override
    public boolean isSecure() {
        return getProtocol().startsWith("HTTPS");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return raiseForImpl();
    }

    @Override
    public String getRealPath(String path) {
        return raiseForDeprecated();
    }

    @Override
    public int getRemotePort() {
        return remoteIP.getPort();
    }

    @Override
    public String getLocalName() {
        return localIP.getHost();
    }

    @Override
    public String getLocalAddr() {
        return localIP.getIp();
    }

    @Override
    public int getLocalPort() {
        return localIP.getPort();
    }

    private void ensureInAsyncMode() throws IllegalStateException {
        throw new IllegalStateException("Request has not been put into asynchronous mode");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return raiseForImpl();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return raiseForImpl();
    }

    @Override
    public boolean isAsyncStarted() {
        return raiseForImpl();
    }

    @Override
    public boolean isAsyncSupported() {
        return raiseForImpl();
    }

    @Override
    public AsyncContext getAsyncContext() {
        ensureInAsyncMode();
        return raiseForImpl();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return raiseForImpl();
    }

    @Override
    public String getAuthType() {
        return raiseForImpl();
    }

    @Override
    public Cookie[] getCookies() {
        return raiseForImpl();
    }

    @Override
    public long getDateHeader(@NonNull String name) {
        String string = getHeader(name);
        if (string == null) {
            return -1;
        }
        try {
            return DateUtils.forGMT(string).getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getHeader(@NonNull String name) {
        return headers.getFirst(name);
    }

    @Override
    public Enumeration<String> getHeaders(@NonNull String name) {
        return Enumerations.forCollection(headers.get(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Enumerations.forCollection(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        String s = getHeader(name);
        return s != null ? Integer.parseInt(s) : -1;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return raiseForImpl();
    }

    @Override
    public boolean isUserInRole(String role) {
        return raiseForImpl();
    }

    @Override
    public Principal getUserPrincipal() {
        return raiseForImpl();
    }

    @Override
    public String getRequestedSessionId() {
        return raiseForImpl();
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    private void ensureSessionAssociated() throws IllegalStateException {
        throw new IllegalStateException("No session associated with the request");
    }

    @Override
    public HttpSession getSession(boolean create) {
        return raiseForImpl();
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String changeSessionId() {
        ensureSessionAssociated();
        return raiseForImpl();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return raiseForImpl();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return raiseForImpl();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return raiseForImpl();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return raiseForImpl();
    }

    @Override
    public void login(String username, String password) throws ServletException {
        raiseForImpl();
    }

    @Override
    public void logout() throws ServletException {
        raiseForImpl();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return raiseForImpl();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {

        return raiseForImpl();
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return raiseForImpl();
    }

}
