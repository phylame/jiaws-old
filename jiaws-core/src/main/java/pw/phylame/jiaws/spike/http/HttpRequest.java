package pw.phylame.jiaws.spike.http;

import static pw.phylame.ycl.util.StringUtils.isEmpty;
import static pw.phylame.ycl.util.StringUtils.isNotEmpty;
import static pw.phylame.ycl.util.StringUtils.secondPartOf;
import static pw.phylame.ycl.util.StringUtils.valueOfName;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import pw.phylame.jiaws.io.ByteStorage;
import pw.phylame.jiaws.io.LimitedInputStream;
import pw.phylame.jiaws.util.Exceptions;
import pw.phylame.jiaws.util.HttpException;
import pw.phylame.jiaws.util.NumberUtils;
import pw.phylame.ycl.util.DateUtils;
import pw.phylame.ycl.util.MultiMap;
import pw.phylame.ycl.util.Provider;
import pw.phylame.ycl.value.Lazy;

@ToString
public class HttpRequest extends HttpObject {
    @Getter
    private String path;

    @Getter
    private final MultiMap<String, String> parameters = new MultiMap<>(new LinkedHashMap<String, Collection<String>>());

    @Getter
    private final List<Cookie> cookies = new LinkedList<>();

    private InputStream input;

    private ByteStorage buf = new ByteStorage();

    private String urlEncoding = "utf-8";

    @Getter
    private String host;

    @Getter
    private String contentType;

    @Setter
    @Getter
    private String characterEncoding;

    @Getter
    private long contentLength;

    private Lazy<String> encoding = new Lazy<>(new Provider<String>() {
        @Override
        public String provide() {
            val str = getHeader("Character-Encoding");
            return isNotEmpty(str) ? secondPartOf(str, ';') : null;
        }
    });

    private HttpRequest() {
    }

    public Collection<String> getParameterNames() {
        return parameters.keySet();
    }

    public Collection<String> getParameters(String name) {
        return parameters.get(name);
    }

    public String getParameter(String name) {
        return parameters.getOne(name);
    }

    public int getIntHeader(String name) {
        return NumberUtils.parseInt(getHeader(name), -1);
    }

    public long getLongHeader(String name) {
        return NumberUtils.parseLong(getHeader(name), -1L);
    }

    public Date getDateHeader(String name) {
        return DateUtils.parseDate(getHeader(name), null);
    }

    public InputStream getInputStream() {
        if (contentLength >= 0) {
            return new LimitedInputStream(input, contentLength);
        }
        return null;
    }

    public static HttpRequest from(@NonNull InputStream input) throws IOException, HttpException {
        HttpRequest request = new HttpRequest();
        request.parse(input);
        return request;
    }

    private String bufferString() throws UnsupportedEncodingException {
        val str = buf.toString(urlEncoding);
        buf.setLength(0);
        return str;
    }

    private void parse(InputStream input) throws IOException, HttpException {
        this.input = input;
        parseRequestLine();
        parseHeaderFields();
        prepareContent();
    }

    private void parseRequestLine() throws IOException, HttpException {
        buf.ensureCapacity(32);
        buf.setLength(0);

        val exc = new HttpException("Bad HTTP protocol in request line");

        int b, order = 1;
        // parameter name
        String name = null;
        loop: while ((b = input.read()) != -1) {
            switch (b) {
            case '&': {
                if (order != 2 && path == null) { // only in query string
                    throw exc;
                }
                parameters.addOne(name, bufferString());
                name = null;
            }
            break;
            case '=': {
                if (order != 2 && path == null) { // only in query string
                    throw exc;
                }
                name = bufferString();
            }
            break;
            case ' ': {
                if (order == 1) {
                    setMethod(bufferString());
                } else if (order == 2) {
                    if (path != null) {
                        if (name == null) {
                            parameters.addOne(bufferString(), "");
                        } else {
                            parameters.addOne(name, bufferString());
                        }
                    } else {
                        path = bufferString();
                    }
                } else {
                    throw exc;
                }
                ++order;
            }
            break;
            case '?': {
                if (order != 2) {
                    throw exc;
                } else if (path == null) {
                    path = bufferString();
                } else { // '?' in query string
                    buf.append((byte) '?');
                }
            }
            break;
            case '%': {
                decode(exc);
            }
            break;
            case '+': {
                buf.append((byte) ' ');
            }
            break;
            case '\r': { // may be \r or \n
                b = input.read();
                if (b == '\n') {
                    if (order != 3) {
                        throw exc;
                    }
                    setProtocol(bufferString());
                    break loop;
                } else if (b != -1) {
                    buf.append((byte) b);
                }
            }
            break;
            default: {
                buf.append((byte) b);
            }
            break;
            }
        }
        if (isEmpty(getMethod()) || isEmpty(path) || isEmpty(getProtocol())) {
            throw exc;
        }
        if (getProtocol().endsWith("0.9")) {
            throw new HttpException("HTTP/0.9 is absolute");
        }
    }

    private void decode(HttpException exc) throws IOException, HttpException {
        int b = input.read();
        if (b == -1) {
            throw exc;
        }
        int n = NumberUtils.valueOf((char) b) << 4;
        b = input.read();
        if (b == -1) {
            throw exc;
        }
        n += NumberUtils.valueOf((char) b);
        buf.append((byte) n);
    }

    private void parseHeaderFields() throws IOException, HttpException {
        buf.ensureCapacity(64);
        buf.setLength(0);

        val headers = getHeaders();

        val maxHeaderCount = 100;
        val maxHeaderSize = 8192;
        int length = 0;

        int b;
        // header name
        String name = null;
        loop: while ((b = input.read()) != -1) {
            switch (b) {
            case ':': {
                if (name == null) {
                    name = bufferString();
                } else { // ':' in header value
                    buf.append((byte) ':');
                }
            }
            break;
            case '\r': { // may be \r or \n
                b = input.read();
                if (b == '\n') {
                    if (name == null) { // end of header field
                        break loop;
                    }
                    if (headers.size() > maxHeaderCount) {
                        throw Exceptions.forHttp("Number of header field > limit(%d): %s", maxHeaderCount, name);
                    }
                    headers.addOne(name, bufferString());
                    name = null;
                    length = 0;
                } else if (b != -1) {
                    buf.append((byte) b);
                }
            }
            break;
            default: {
                if (name != null) { // for header value
                    if (length++ > maxHeaderSize) {
                        throw Exceptions.forHttp("Value of header field > limit(%d): %s", maxHeaderSize, name);
                    } else if (length == 1 && b == ' ') { // space after ':'
                        continue;
                    }
                }
                buf.append((byte) b);
            }
            break;
            }
        }
        host = getHeader("Host");
        headers.remove("Host");
    }

    private void prepareContent() throws IOException, HttpException {
        val headers = getHeaders();

        contentType = getHeader("Content-Type");
        headers.remove("Content-Type");
        contentLength = getLongHeader("Content-Length");
        headers.remove("Content-Length");
        if (contentType != null) {
            characterEncoding = valueOfName(contentType, "charset", ";", true, null);
        } else {
            return;
        }
        if (contentLength <= 0) {
            return;
        } else if (getMethod().equals("GET")) {
            throw new HttpException("'GET' method must be without content");
        }
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            parseURLEncoded();
        }
    }

    private void parseURLEncoded() throws IOException, HttpException {
        buf.ensureCapacity(64);
        buf.setLength(0);

        val exc = new HttpException("Bad content for 'application/x-www-form-urlencoded'");
        int b, total = 0;
        // parameter name
        String name = null;
        while ((b = input.read()) != -1 && total++ != contentLength) {
            switch (b) {
            case '=': {
                if (name == null) {
                    name = bufferString();
                } else { // '=' in value
                    throw exc;
                }
            }
            break;
            case '&': {
                if (name != null) {
                    parameters.addOne(name, bufferString());
                    name = null;
                } else { // '&' in name
                    throw exc;
                }
            }
            break;
            case '%': {
                decode(exc);
            }
            break;
            case '+': { // space
                buf.append((byte) ' ');
            }
            break;
            default: {
                buf.append((byte) b);
            }
            break;
            }
        }
        if (name != null) {
            parameters.addOne(name, bufferString());
        }
        contentLength = 0L;
    }
}
