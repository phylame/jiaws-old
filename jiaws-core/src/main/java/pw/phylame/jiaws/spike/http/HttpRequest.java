package pw.phylame.jiaws.spike.http;

import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import pw.phylame.jiaws.io.ByteStorage;
import pw.phylame.jiaws.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.LinkedHashMap;

@ToString
public class HttpRequest {
    private String method;

    private String path;

    private String protocol;

    private MultiValueMap<String, String> parameters = new MultiValueMap<>(new LinkedHashMap<String, Collection<String>>());

    private MultiValueMap<String, String> headers = new MultiValueMap<>(new LinkedHashMap<String, Collection<String>>());

    private InputStream input;

    private ByteStorage buf = new ByteStorage();

    private String urlEncoding = "utf-8";

    private HttpRequest() {
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
    }

    private void parseRequestLine() throws IOException, HttpException {
        buf.ensureCapacity(32);
        buf.setLength(0);

        val exc = new HttpException("Bad HTTP protocol in request line");

        int b, order = 1;
        // parameter name, value
        String name = null, value;
        loop:
        while ((b = input.read()) != -1) {
            switch (b) {
                case '&': {
                    if (order != 2 && path == null) { // only in query string
                        throw exc;
                    }
                    value = bufferString();

                    parameters.addOne(name, value);
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
                        method = bufferString();
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
                    b = input.read();
                    if (b == -1) {
                        throw exc;
                    }
                    int n = NumberUtils.hexValue((char) b) << 4;
                    b = input.read();
                    if (b == -1) {
                        throw exc;
                    }
                    n += NumberUtils.hexValue((char) b);
                    buf.append((byte) n);
                }
                break;
                case '\r': { // may be \r or \n
                    b = input.read();
                    if (b == '\n') {
                        if (order != 3) {
                            throw exc;
                        }
                        protocol = bufferString();
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
        if (StringUtils.isEmpty(method) || StringUtils.isEmpty(path) || StringUtils.isEmpty(protocol)) {
            throw exc;
        }
        if (protocol.endsWith("0.9")) {
            throw new HttpException("HTTP/0.9 is absolute");
        }
    }

    private void parseHeaderFields() throws IOException, HttpException {
        buf.ensureCapacity(64);
        buf.setLength(0);

        val maxHeaderCount = 100;
        val maxHeaderSize = 8192;
        int length = 0;

        int b;
        // header name, value
        String name = null, value;
        loop:
        while ((b = input.read()) != -1) {
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
                        value = bufferString();
                        headers.addOne(name, value);
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
                        } else if (length == 1 && b == ' ') {   // space after ':'
                            continue;
                        }
                    }
                    buf.append((byte) b);
                }
                break;
            }
        }
    }
}
