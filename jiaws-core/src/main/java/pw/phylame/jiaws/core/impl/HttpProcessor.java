/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package pw.phylame.jiaws.core.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.Date;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import pw.phylame.jiaws.core.ProtocolProcessor;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.jiaws.servlet.HttpServletRequestImpl;
import pw.phylame.jiaws.servlet.http.HttpServletResponseImpl;
import pw.phylame.jiaws.util.DateUtils;
import pw.phylame.jiaws.util.HttpUtils;
import pw.phylame.jiaws.util.IPTuple;
import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.jiaws.util.values.Pair;

public class HttpProcessor implements ProtocolProcessor, ServerAware {
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
    @SuppressWarnings("unchecked")
    public Pair<HttpServletRequestImpl, HttpServletResponseImpl> parse(Socket socket) throws IOException {
        InputStream in = new BufferedInputStream(socket.getInputStream());
        StringBuilder b = new StringBuilder();
        val request = new HttpServletRequestImpl();
        try {
            // request line
            if (!parseRequestLine(in, b, request)) { // received nothing
                logger.info("Received nothing from client");
                return null;
            }
            parseHeaderFields(in, b, request);
            parseRequestBody(in, b, request);
            setAddressInfo(socket, request);
        } catch (ProtocolException e) {
            logger.debug("Get bad HTTP protocol", e);
            sendError(e.getMessage());
            return null;
        }
        val response = new HttpServletResponseImpl(new ResponseOutputStream(socket.getOutputStream()));
        return new Pair<HttpServletRequestImpl, HttpServletResponseImpl>(request, response);
    }

    private boolean parseRequestLine(InputStream in, StringBuilder b, HttpServletRequestImpl request)
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
            return false;
        }
        // received data but invalid
        if (recvNum >= 0 && method == null || path == null || protocol == null) {
            throw new ProtocolException("Bad HTTP protocol in request line", "http");
        }
        request.setMethod(method);
        request.setPath(path);
        request.setQuery(query.toString());
        request.setProtocol(protocol);
        return true;
    }

    private void parseHeaderFields(InputStream in, StringBuilder b, HttpServletRequestImpl request)
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

    private void parseRequestBody(InputStream in, StringBuilder b, HttpServletRequestImpl request)
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

    private void parsePostParameters(InputStream in, long length, StringBuilder b, HttpServletRequestImpl request)
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

    private void setAddressInfo(Socket socket, HttpServletRequestImpl request) {
        request.setLocalIP(new IPTuple(socket.getLocalAddress(), socket.getLocalPort()));
        request.setRemoteIP(new IPTuple(socket.getInetAddress(), socket.getPort()));
    }

    @Override
    public void render(ServletResponse response, Socket socket) throws IOException {
        if (!(response instanceof HttpServletResponseImpl)) {
            throw new IllegalArgumentException(
                    String.format("response to %s must be HttpServletResponseImpl", getClass().getName()));
        }
        HttpServletResponseImpl httpResponse = (HttpServletResponseImpl) response;
        // BufferedWriter writer = new BufferedWriter(
        // new OutputStreamWriter(socket.getOutputStream(),
        // httpResponse.getCharacterEncoding()));
        //
        Writer writer = new StringWriter();
        writeStatusLine(writer, httpResponse);
        writeHeaderFields(writer, httpResponse);
        System.out.println(writer.toString());
    }

    public static final String CRLF = "\r\n";

    private void writeStatusLine(Writer writer, HttpServletResponseImpl response) throws IOException {
        writer.append("HTTP/1.1 ").append(Integer.toString(response.getStatus())).append(' ')
                .append(HttpUtils.getStatusReason(response.getStatus())).append(CRLF);
    }

    private void writeHeaderFields(Writer writer, HttpServletResponseImpl response) throws IOException {
        // general header
        writeHeaderField(writer, "Date", DateUtils.toGMT(new Date()));
        // response header
        writeHeaderField(writer, "Server", serverRef.get().getAssembly().getVersionInfo());
        // entity header
        if (response.getContentEncoding() != null) {
            writeHeaderField(writer, "Content-Encoding", response.getContentEncoding());
        }
        writeHeaderField(writer, "Content-Length", Long.toString(response.getContentLengthLong()));
        if (response.getContentLengthLong() > 0) {
            writeHeaderField(writer, "Content-Type", response.getContentType());
        }
        for (Cookie cookie : response.getCookies()) {
            writeHeaderField(writer, "Set-Cookie", renderCookie(cookie));
        }
        for (val e : response.getInnerHeaders().entrySet()) {
            String name = e.getKey();
            for (String value : e.getValue()) {
                writeHeaderField(writer, name, value);
            }
        }
    }

    private void writeHeaderField(Writer writer, String name, String value) throws IOException {
        writer.append(name).append(": ").append(value).append(CRLF);
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

    private void sendError(String message) throws IOException {

    }
}
