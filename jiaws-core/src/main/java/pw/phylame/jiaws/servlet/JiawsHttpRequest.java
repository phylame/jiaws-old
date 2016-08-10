/*
 * Copyright 2014-2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package pw.phylame.jiaws.servlet;

import static pw.phylame.jiaws.util.ImplementUtils.raiseForImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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

import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.spike.http.HttpRequest;
import pw.phylame.jiaws.util.Enumerations;
import pw.phylame.jiaws.util.MultiValueMap;
import pw.phylame.jiaws.util.Provider;
import pw.phylame.jiaws.util.StringUtils;
import pw.phylame.jiaws.util.values.LazyValue;

public class JiawsHttpRequest extends AbstractServletRequest implements HttpServletRequest {
    private HttpRequest httpRequest;

    /**
     * Indicates that the input is processed.
     */
    private boolean doneInput = false;

    private LazyValue<List<Locale>> locales = new LazyValue<>(new Provider<List<Locale>>() {
        @Override
        public List<Locale> provide() {
            List<Locale> locales = new LinkedList<>();
            for (String code : httpRequest.getHeaders("Accept-Language")) {
                locales.add(Locale.forLanguageTag(code));
            }
            return locales;
        }
    });

    private LazyValue<String> query = new LazyValue<>(new Provider<String>() {
        @Override
        public String provide() throws Exception {
            StringBuilder b = new StringBuilder();
            int i = 1, end = httpRequest.getParameters().size();
            for (val e : httpRequest.getParameters().entrySet()) {
                b.append(e.getKey()).append('=').append(e.getValue());
                if (i++ != end) {
                    b.append('&');
                }
            }
            return b.toString();
        }
    });

    private void ensureInputNotProcessed() {
        if (doneInput) {
            throw new IllegalStateException("Input of the request has been processed");
        }
    }

    @Override
    public String getCharacterEncoding() {
        return httpRequest.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        httpRequest.setCharacterEncoding(env);
    }

    @Override
    public long getContentLengthLong() {
        return httpRequest.getContentLength();
    }

    @Override
    public String getContentType() {
        return httpRequest.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getScheme() {
        return StringUtils.getFirstPartOf(getProtocol(), '/').toLowerCase();
    }

    @Override
    public String getServerName() {
        return StringUtils.getFirstPartOf(httpRequest.getHost(), ":");
    }

    @Override
    public int getServerPort() {
        return Integer.parseInt(StringUtils.getSecondPartOf(httpRequest.getHost(), ":"));
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ensureInputNotProcessed();
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public boolean isSecure() {
        return getProtocol().startsWith("HTTPS");
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return raiseForImpl();
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
        return httpRequest.getCookies().toArray(new Cookie[httpRequest.getCookies().size()]);
    }

    @Override
    public long getDateHeader(@NonNull String name) {
        val date = httpRequest.getDateHeader(name);
        return date != null ? date.getTime() : -1;
    }

    @Override
    public String getHeader(@NonNull String name) {
        return httpRequest.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(@NonNull String name) {
        return Enumerations.enumeration(httpRequest.getHeaders(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Enumerations.enumeration(httpRequest.getHeaderNames());
    }

    @Override
    public int getIntHeader(String name) {
        return httpRequest.getIntHeader(name);
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
        return query.get();
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
        return httpRequest.getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        val b = new StringBuffer();
        b.append(isSecure() ? "https://" : "http://").append(httpRequest.getHost()).append(httpRequest.getPath());
        return b;
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
    @Deprecated
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

    @Override
    public String getProtocol() {
        return httpRequest.getProtocol();
    }

    @Override
    public String getMethod() {
        return httpRequest.getMethod();
    }

    @Override
    protected MultiValueMap<String, String> getInternalParameters() {
        return httpRequest.getParameters();
    }

    @Override
    protected List<Locale> getInternalLocales() {
        return locales.get();
    }

}
