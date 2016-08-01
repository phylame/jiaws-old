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
import pw.phylame.jiaws.io.ResponseWriteEvent;
import pw.phylame.jiaws.servlet.AbstractServletResponse;
import pw.phylame.jiaws.util.DateUtils;
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
        setStatus(sc);
        reason = msg;
        // if error page declared, go to it
        // else do default error processing
        ImplementUtils.raiseForImpl();
    }

    @Override
    public void sendError(int sc) throws IOException {
        ensureNotCommitted("Response has been committed");
        setStatus(sc);
        // clear buffer and commit
        ImplementUtils.raiseForImpl();
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
        if (!isCommitted()) {
            headers.putOne(name, value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if (!isCommitted()) {
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

    @Override
    public void setStatus(int sc) {
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
    }

    @Override
    public void beforeWrite(ResponseWriteEvent e) throws IOException {
        val p = new PrintStream((ResponseOutputStream) e.getSource(), false);
        writeStatusLine(p);
        writeHeaderFields(p);
    }

    public static final String CRLF = "\r\n";

    private void writeStatusLine(PrintStream p) throws IOException {
        p.append(protocol).append(' ').append(Integer.toString(getStatus())).append(' ')
                .append(HttpUtils.getStatusReason(getStatus())).append(CRLF);
    }

    private void writeHeaderFields(PrintStream p) throws IOException {
        // general header
        writeHeaderField(p, "Date", DateUtils.toGMT(new Date()));
        // response header
        writeHeaderField(p, "Server", serverRef.get().getAssembly().getVersionInfo());
        // entity header
        if (contentEncoding != null) {
            writeHeaderField(p, "Content-Encoding", contentEncoding);
        }
        writeHeaderField(p, "Content-Length", Long.toString(getContentLength()));
        if (getContentLength() > 0) {
            writeHeaderField(p, "Content-Type", getContentType());
        }
        for (Cookie cookie : cookies) {
            writeHeaderField(p, "Set-Cookie", renderCookie(cookie));
        }
        for (val e : headers.entrySet()) {
            String name = e.getKey();
            for (String value : e.getValue()) {
                writeHeaderField(p, name, value);
            }
        }
    }

    private void writeHeaderField(PrintStream p, String name, String value) throws IOException {
        p.append(name).append(": ").append(value).append(CRLF);
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
