package pw.phylame.jiaws.spike.http;

import static pw.phylame.ycl.util.StringUtils.isNotEmpty;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import pw.phylame.jiaws.core.Assembly;
import pw.phylame.jiaws.io.ContentSource;
import pw.phylame.jiaws.io.IOConstanst;
import pw.phylame.jiaws.util.HttpUtils;
import pw.phylame.ycl.util.DateUtils;

@ToString
public class HttpResponse extends HttpObject {

    private int status = HttpServletResponse.SC_OK;

    private String message;

    private List<Cookie> cookies = new LinkedList<>();

    @Setter
    private ContentSource content;

    @Setter
    private Assembly serverAssembly;

    public void addCookie(Cookie cookie) {
        if (cookie != null && !isCommitted()) {
            cookies.add(cookie);
        }
    }

    public void setDateHeader(String name, long date) {
        setHeader(name, DateUtils.toRFC1123(new Date(date)));
    }

    public void addDateHeader(String name, long date) {
        addHeader(name, DateUtils.toRFC1123(new Date(date)));
    }

    public void setHeader(String name, String value) {
        if (name != null && value != null && !isCommitted()) {
            getHeaders().putOne(name, value);
        }
    }

    public void addHeader(String name, String value) {
        if (name != null && value != null && !isCommitted()) {
            getHeaders().addOne(name, value);
        }
    }

    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    public boolean isCommitted() {
        return false;
    }

    public void renderTo(@NonNull OutputStream out) throws IOException {
        val writer = new OutputStreamWriter(out);
        writeRequestLine(writer);
        writeHeaderFields(writer);
        writeContent(out);
    }

    private void writeRequestLine(Writer w) throws IOException {
        w.append(isNotEmpty(getProtocol()) ? getProtocol() : "HTTP/1.1").append(' ');
        w.append(Integer.toString(status)).append(' ');
        w.append(isNotEmpty(message) ? message : HttpUtils.getStatusReason(status))
                .append(IOConstanst.CRLF);
        w.flush();
    }

    private void writeHeaderFields(Writer w) throws IOException {
        // general header
        writeHeaderField(w, "Date", DateUtils.toRFC1123(new Date()));
        // response header
        writeHeaderField(w, "Server", serverAssembly.toString());
        // entity header
        if (content != null) {
            writeHeaderField(w, "Content-Encoding", content.getEncoding());
            val length = content.getLength();
            if (length == -1) { // chunked
                writeHeaderField(w, "Transfer-Encoding", "chunked");
            } else {
                writeHeaderField(w, "Content-Length", length);
            }
            writeHeaderField(w, "Content-Type", content.getType());
        }
        // cookies
        for (Cookie cookie : cookies) {
            writeHeaderField(w, "Set-Cookie", renderCookie(cookie));
        }
        // user headers
        for (val e : getHeaders().entrySet()) {
            String name = e.getKey();
            for (String value : e.getValue()) {
                writeHeaderField(w, name, value);
            }
        }
        w.append(IOConstanst.CRLF).flush();
    }

    private void writeHeaderField(Writer w, String name, Object value) throws IOException {
        if (value != null) {
            w.append(name).append(": ").append(value.toString()).append(IOConstanst.CRLF);
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

    private void writeContent(OutputStream out) throws IOException {
        if (content != null) {
            content.writeTo(out);
            out.flush();
        }
    }
}
