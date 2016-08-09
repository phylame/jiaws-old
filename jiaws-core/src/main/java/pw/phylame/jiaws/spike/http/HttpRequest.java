package pw.phylame.jiaws.spike.http;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import pw.phylame.jiaws.util.HttpException;
import pw.phylame.jiaws.util.MultiValueMap;
import pw.phylame.jiaws.util.ProtocolException;

@ToString
public class HttpRequest {
    private String method;

    private String path;

    private String query;

    private String protocol;

    private MultiValueMap<String, String> parameters = new MultiValueMap<>();

    private MultiValueMap<String, String> headers = new MultiValueMap<>();

    private InputStream input;

    private StringBuilder buf = new StringBuilder();

    private HttpRequest() {
    }

    public static HttpRequest from(@NonNull InputStream input) throws IOException, ProtocolException {
        HttpRequest request = new HttpRequest();
        request.parse(input);
        return request;
    }

    private String bufferString() {
        val str = buf.toString();
        buf.setLength(0);
        return str;
    }

    private void parse(InputStream input) throws IOException, ProtocolException {
        this.input = input;
        parseRequestLine();
        parseHeaderFields();
    }

    private void parseRequestLine() throws IOException, ProtocolException {
        buf.setLength(0);

        val exc = new HttpException("Bad HTTP protocol in request line");

        int ch, order = 1;
        // query string
        StringBuilder query = new StringBuilder();
        // parameter name, value
        String name = null, value = null;
        loop: while ((ch = input.read()) != -1) {
            switch (ch) {
            case '&': {
                value = bufferString();
                query.append('&');

                parameters.addOne(name, value);
                name = value = null;
            }
            break;
            case '=': {
                if (order != 2) {
                    throw exc;
                }
                name = bufferString();
                query.append('=');
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
                    buf.append('?');
                    query.append('?');
                }
            }
            break;
            case '\r': { // may be CRLF
                ch = input.read();
                if (ch == '\n') {
                    if (order != 3) {
                        throw exc;
                    }
                    protocol = bufferString();
                    break loop;
                } else if (ch != -1) {
                    buf.append(ch);
                }
            }
            break;
            default: {
                buf.append((char) ch);
                if (order == 2 && path != null) {
                    query.append((char) ch);
                }
            }
            break;
            }
        }
        this.query = query.toString();
    }

    private void parseHeaderFields() throws IOException {
        buf.setLength(0);

        int ch;

        loop: while ((ch = input.read()) != -1) {
            switch (ch) {
            case ':': {
            }
            break;

            default: {
            }
            break;
            }
        }
    }
}
