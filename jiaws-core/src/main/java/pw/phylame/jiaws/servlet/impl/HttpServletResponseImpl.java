package pw.phylame.jiaws.servlet.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.util.DateUtils;
import pw.phylame.jiaws.util.MultiValueMap;
import pw.phylame.jiaws.util.StringUtils;

public class HttpServletResponseImpl implements HttpServletResponse {

    /**
     * Status code.
     */
    @Getter
    private int status = 200;

    /**
     * HTTP status reason.
     */
    @Getter
    private String reason = "OK";

    @Getter
    @Setter
    @NonNull
    private String characterEncoding = "ISO-8859-1";

    @Getter
    private String contentType;

    @Getter
    @Setter
    private String contentEncoding = null;

    @Setter
    @Getter
    private long contentLengthLong = 0L;

    @Getter
    @Setter
    private Locale locale;

    @Getter
    private List<Cookie> cookies = new LinkedList<>();

    @Getter
    private MultiValueMap<String, String> innerHeaders = new MultiValueMap<>();

    /**
     * Indicates that the output is processed.
     */
    private boolean doneOutput = false;

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        doneOutput = true;
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
    }

    @Override
    public void setContentLength(int len) {
        setContentLengthLong(len);
    }

    @Override
    public void setContentType(@NonNull String type) {
        contentType = type;
        if (!doneOutput) {
            setCharacterEncoding(StringUtils.getSecondPartOf(type, '=').trim());
        }
    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void resetBuffer() {
        if (isCommitted()) {
            throw new IllegalStateException("Response has been committed");
        }
    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addCookie(@NonNull Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return innerHeaders.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendError(int sc) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendRedirect(String location) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, DateUtils.toGMT(new Date(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, DateUtils.toGMT(new Date(date)));
    }

    @Override
    public void setHeader(String name, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addHeader(String name, String value) {
        innerHeaders.addOne(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    @Override
    public void setStatus(int sc) {
        checkStatusValid(sc);
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        setStatus(sc);
        reason = sm;
    }

    private void checkStatusValid(int code) {
        if (code <= 100 || code >= 600) {
            throw new IllegalArgumentException("Status code must in [200, 600)");
        }
    }

    @Override
    public String getHeader(String name) {
        return innerHeaders.getFirst(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return innerHeaders.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return innerHeaders.keySet();
    }
}
