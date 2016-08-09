package pw.phylame.jiaws.spike.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.jiaws.servlet.JiawsHttpRequest;
import pw.phylame.jiaws.servlet.http.JiawsHttpResponse;
import pw.phylame.jiaws.spike.ProtocolParser;
import pw.phylame.jiaws.spike.SocketInput;
import pw.phylame.jiaws.util.IPTuple;
import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.jiaws.util.values.Pair;

public class Http11SocketParser
        implements ProtocolParser<JiawsHttpRequest, JiawsHttpResponse, SocketInput>, ServerAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WeakReference<Server> serverRef;

    private int maxHeaderCount;

    private int maxHeaderSize;

    @Override
    public void setServer(Server server) {
        serverRef = new WeakReference<>(server);
        maxHeaderCount = server.getConfig().getMaxRequestHeaderCount();
        maxHeaderSize = server.getConfig().getMaxRequestHeaderSize();
    }

    @Override
    public Pair<JiawsHttpRequest, JiawsHttpResponse> parse(SocketInput input)
            throws IOException, ProtocolException {
        val socket = input.getSocket();
        InputStream in = new BufferedInputStream(socket.getInputStream());
        StringBuilder b = new StringBuilder();
        val request = new JiawsHttpRequest();
        // request line
        parseRequestLine(in, b, request);
        parseHeaderFields(in, b, request);
        parseRequestBody(in, b, request);
        setAddressInfo(socket, request);
        val response = new JiawsHttpResponse(new ResponseOutputStream(socket.getOutputStream()));
        response.setSocket(socket);
        return new Pair<JiawsHttpRequest, JiawsHttpResponse>(request, response);
    }

    private void sendError(String message) {

    }

    private void parseRequestLine(InputStream in, StringBuilder b, JiawsHttpRequest request)
            throws IOException, ProtocolException {
        b.setLength(0);
        val parameters = request.getParameters();
        int ch, order = 0, recvNum = 0;
        String method = null, path = null, protocol = null;
        StringBuilder query = new StringBuilder();
        String paramName = null, paramValue = null;
        line: while ((ch = in.read()) != -1) {
            ++recvNum;
            switch (ch) {
            case ' ':
                switch (order) {
                case 0: { // method
                    method = b.toString();
                }
                break;
                case 1: { // path or query
                    if (path != null) {
                        if (paramName != null) {
                            parameters.addOne(paramName, b.toString());
                            b.setLength(0);
                        }
                    } else {
                        path = b.toString();
                    }
                }
                break;
                default: {
                    throw new ProtocolException("Bad HTTP protocol in request line", "http");
                }
                }
                b.setLength(0);
                ++order;
            break;
            case '?': { // path and query
                if (order != 1) {
                    throw new ProtocolException("Bad HTTP protocol in request line", "http");
                } else if (path == null) {
                    path = b.toString();
                    b.setLength(0);
                } else { // '?' in query data
                    b.append((char) ch);
                    query.append((char) ch);
                }
            }
            break;
            case '=': {
                paramName = b.toString();
                b.setLength(0);
                query.append((char) ch);
            }
            break;
            case '&': {
                paramValue = b.toString();
                b.setLength(0);
                query.append((char) ch);

                parameters.addOne(paramName, paramValue);
                paramName = paramValue = null;
            }
            break;
            case '\r': { // may be CRLF
                ch = in.read();
                if (ch == '\n') {
                    protocol = b.toString();
                    break line;
                } else if (ch != -1) {
                    b.append(ch);
                }
            }
            break;
            default: {
                b.append((char) ch);
                if (order == 1 && path != null) {
                    query.append((char) ch);
                }
            }
            break;
            }
        }
        if (recvNum == 0) {
            throw new ProtocolException("Bad HTTP protocol in request line", "http");
        }
        // received data but invalid
        if (recvNum >= 0 && method == null || path == null || protocol == null) {
            throw new ProtocolException("Bad HTTP protocol in request line", "http");
        }
        request.setMethod(method);
        request.setPath(path);
        request.setQuery(query.toString());
        request.setProtocol(protocol);
    }

    private void parseHeaderFields(InputStream in, StringBuilder b, JiawsHttpRequest request)
            throws IOException, ProtocolException {
        b.setLength(0);
        val headers = request.getHeaders();
        int ch, headerSize = 0;
        String name = null, value = null;
        line: while ((ch = in.read()) != -1) {
            switch (ch) {
            case ':': { // may be name
                if (name == null) {
                    name = b.toString();
                    b.setLength(0);
                } else {
                    b.append((char) ch);
                }
            }
            break;
            case '\r': { // may be CRLF
                ch = in.read();
                if (ch == '\n') {
                    if (name == null) { // finished header
                        break line;
                    }
                    value = b.toString();
                    b.setLength(0);
                    headerSize = 0;

                    if (headers.size() == maxHeaderCount) {
                        throw new ProtocolException(String.format("Number of header field > limit(%d)", maxHeaderCount),
                                "http");
                    }
                    headers.addOne(name, value);
                    name = value = null;
                    continue;
                } else if (ch != -1) {
                    b.append(ch);
                }
            }
            break;
            default: {
                if (name != null) { // for value
                    if (headerSize++ > maxHeaderSize) {
                        throw new ProtocolException(
                                String.format("Value of header field > limit(%d): %s", maxHeaderSize, b), "http");
                    } else if (headerSize != 1) {
                        b.append((char) ch);
                    }
                } else {
                    b.append((char) ch);
                }
            }
            break;
            }
        }
    }

    private void parseRequestBody(InputStream in, StringBuilder b, JiawsHttpRequest request)
            throws IOException, ProtocolException {
        b.setLength(0);
        long length = request.getContentLengthLong();
        String mime = request.getContentType();
        if (length > 0 && "application/x-www-form-urlencoded".equals(mime)) {
            if (!request.getMethod().equals("POST")) {
                throw new ProtocolException(String.format("Request body with mime(%s) in only supported for POST"),
                        "http");
            }
            parsePostParameters(in, length, b, request);
        }
    }

    private void parsePostParameters(InputStream in, long length, StringBuilder b, JiawsHttpRequest request)
            throws IOException, ProtocolException {
        val parameters = request.getParameters();
        long recvNum = 0;
        int ch;
        String name = null, value = null;
        while (recvNum++ < length && (ch = in.read()) != -1) {
            switch (ch) {
            case '&': {
                value = b.toString();
                b.setLength(0);

                parameters.addOne(name, value);
                name = value = null;
            }
            break;
            case '=': {
                name = b.toString();
                b.setLength(0);
            }
            break;
            default: {
                b.append((char) ch);
            }
            break;
            }
        }
        if (recvNum - 1 != length) {
            throw new ProtocolException(
                    String.format("Bad HTTP body for POST parameters: content-length(%d), actual(%d)", length, recvNum),
                    "http");
        } else if (name != null) {
            parameters.addOne(name, b.toString());
        }
    }

    private void setAddressInfo(Socket socket, JiawsHttpRequest request) {
        request.setLocalIP(new IPTuple(socket.getLocalAddress(), socket.getLocalPort()));
        request.setRemoteIP(new IPTuple(socket.getInetAddress(), socket.getPort()));
    }

}
