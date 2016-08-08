package pw.phylame.jiaws.servlet.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.val;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.jiaws.servlet.AbstractServletResponse;
import pw.phylame.jiaws.util.DateUtils;
import pw.phylame.jiaws.util.Exceptions;
import pw.phylame.jiaws.util.HttpUtils;
import pw.phylame.jiaws.util.ImplementUtils;
import pw.phylame.jiaws.util.MultiValueMap;

public class HttpServletResponseImpl extends AbstractServletResponse implements HttpServletResponse {
    private String protocol = "HTTP/1.1";

    @Getter
    private int status = SC_OK;

    private String reason = null;

    private List<Cookie> cookies = new LinkedList<>();

    private String contentEncoding = null;

    private MultiValueMap<String, String> headers = new MultiValueMap<>();

    public HttpServletResponseImpl(ResponseOutputStream out) {
        super(out);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (cookie != null && !isCommitted()) {
            cookies.add(cookie);
        }
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public String encodeRedirectURL(String url) {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public String encodeUrl(String url) {
        return encodeUrl(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        ensureNotCommitted("Response has been committed");
        status = sc;
        reason = msg;
        // clear headers
        headers.clear();
        // clear content in buffer
        resetBuffer();
    }

    @Override
    public void sendError(int sc) throws IOException {
        sendError(sc, null);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        ensureNotCommitted("Response has been committed");
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
        if (name != null && value != null && !isCommitted()) {
            headers.putOne(name, value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if (name != null && value != null && !isCommitted()) {
            headers.addOne(name, value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    // valid status in [200, 600)
    private boolean isValidStatus(int status) {
        return status >= 200 && status < 600;
    }

    @Override
    public void setStatus(int sc) {
        if (isCommitted()) {
            return;
        }
        if (!isValidStatus(sc)) {
            logger.info("Ignore invalid status: %d", sc);
            return;
        }
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        ImplementUtils.raiseForDeprecated();
    }

    @Override
    public String getHeader(String name) {
        return headers.getFirst(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public void reset() {
        super.reset();
        status = SC_OK;
        reason = null;
        headers.clear();
        cookies.clear();
    }

    @Override
    public void beforCommit(ResponseOutputStream out) throws IOException {
        if (out != this.out) {
            Exceptions.forRuntime("BUG: 'out' in parameter of %s.onCommit is not the 'out' of %s", getClass().getName(),
                    getClass().getName());
        }
        String ls = System.getProperty("line.separator");
        System.setProperty("line.separator", CRLF);
        val ps = new PrintStream(socket.getOutputStream(), false);
        writeStatusLine(ps);
        writeHeaderFields(ps);
        ps.flush();
        System.setProperty("line.separator", ls);
    }

    public static final String CRLF = "\r\n";

    private void writeStatusLine(PrintStream ps) throws IOException {
        ps.append(protocol).append(' ').append(Integer.toString(getStatus())).append(' ');
        ps.append(reason != null ? reason : HttpUtils.getStatusReason(getStatus())).append(CRLF);
    }

    private void writeHeaderFields(PrintStream ps) throws IOException {
        // general header
        writeHeaderField(ps, "Date", DateUtils.toGMT(new Date()));
        // response header
        writeHeaderField(ps, "Server", serverRef.get().getAssembly().getVersionInfo());
        // entity header
        writeHeaderField(ps, "Content-Encoding", contentEncoding);
        writeHeaderField(ps, "Content-Length", getContentLength());
        if (getContentLength() == null) {
            if (isFlushed()) { // buffer has been flushed, so content length is unknown
                writeHeaderField(ps, "Transfer-Encoding", "chunked");
            } else {
                System.out.println(out.getTotal());
                writeHeaderField(ps, "Content-Length", out.getTotal());
            }
        }
        writeHeaderField(ps, "Content-Type", getContentType());
        // cookies
        for (Cookie cookie : cookies) {
            writeHeaderField(ps, "Set-Cookie", renderCookie(cookie));
        }
        // user headers
        for (val e : headers.entrySet()) {
            String name = e.getKey();
            for (String value : e.getValue()) {
                writeHeaderField(ps, name, value);
            }
        }
    }

    private void writeHeaderField(PrintStream p, String name, Object value) throws IOException {
        if (value != null) {
            p.append(name).append(": ").append(value.toString()).append(CRLF);
        }
    }

    private String renderCookie(Cookie cookie) {
        StringBuilder b = new StringBuilder();
        b.append(cookie.getName()).append('=').append(cookie.getValue());
        if (cookie.getMaxAge() != -1) {
            b.append("; Max-Age=").append(cookie.getMaxAge());
        }
        if (cookie.getDomain() != null) {
            b.append("; Domain=").append(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append("; Path=").append(cookie.getPath());
        }
        if (cookie.getComment() != null) {
            b.append("; Comment=").append(cookie.getComment());
        }
        if (cookie.getSecure()) {
            b.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            b.append("; HttpOnly");
        }
        return b.toString();
    }

}
