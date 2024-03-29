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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.ycl.util.StringUtils;

public abstract class AbstractServletResponse
        implements ServletResponse, ServerAware, ResponseOutputStream.OnFirstCommitListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final ResponseOutputStream out;

    private String characterEncoding = null;

    /**
     * The content type without character encoding.
     */
    private String contentType = null;

    @Getter(lombok.AccessLevel.PROTECTED)
    private Long contentLength;

    /**
     * State of the response. 0: fresh, 1: written by getWriter, 2: written by getOutputStream
     */
    private int state = FRESH;

    private static final int FRESH = 0;
    private static final int BY_GET_WRITER = 1;
    private static final int BY_GET_OUTPUT_STREAM = 2;

    @Getter
    private Locale locale = Locale.getDefault();

    /**
     * State indicating that flushBuffer has been called.
     */
    @Getter
    private boolean flushed = false;

    /**
     * Holds weak reference of current server.
     */
    protected WeakReference<Server> serverRef;

    @Setter
    protected Socket socket;

    public AbstractServletResponse(@NonNull ResponseOutputStream out) {
        this.out = out;
        out.setResponse(this);
        out.setOnFirstCommitListener(this);
    }

    @Override
    public void setServer(@NonNull Server server) {
        serverRef = new WeakReference<Server>(server);
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding != null ? characterEncoding : "ISO-8859-1";
    }

    @Override
    public String getContentType() {
        return contentType == null || characterEncoding == null ? contentType
                : contentType + "; charset=" + characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ensureResponseFresh();
        state = BY_GET_OUTPUT_STREAM;
        return new ServletResponseOutputStream(out);
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        ensureResponseFresh();
        String encoding = characterEncoding;
        if (encoding == null) {
            encoding = "ISO-8859-1";
        } else if (!Charset.isSupported(characterEncoding)) {
            throw new UnsupportedEncodingException(String.format("Character encoding '%s' is unsupported", encoding));
        }
        state = BY_GET_WRITER;
        // no auto flush
        return new PrintWriter(new OutputStreamWriter(out, encoding), false);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        if (charset != null && !isCommitted()) {
            characterEncoding = charset;
        }
    }

    @Override
    public void setContentLength(int len) {
        setContentLengthLong(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        if (!isCommitted()) {
            contentLength = len;
        }
    }

    @Override
    public void setContentType(String type) {
        if (type != null && !isCommitted()) {
            StringBuilder b = new StringBuilder();
            String first, second;
            for (val pair : StringUtils.getNamedPairs(type, ";")) {
                first = pair.getFirst();
                second = pair.getSecond();
                if (first.equalsIgnoreCase("charset") && !second.isEmpty()) {
                    setCharacterEncoding(second);
                    continue;
                }
                b.append(first).append(second.isEmpty() ? "" : "=" + second);
            }
            contentType = b.toString();
        }
    }

    @Override
    public int getBufferSize() {
        return out.getBufferSize();
    }

    @Override
    public void setBufferSize(int size) {
        ensureNotCommitted("Cannot set buffer size after response has been committed");
        out.setBufferSize(size);
    }

    @Override
    public void flushBuffer() throws IOException {
        out.flush();
        flushed = true;
    }

    @Override
    public void resetBuffer() {
        ensureNotCommitted("Cannot reset buffer after response has been committed");
        out.resetBuffer();
        state = FRESH;
    }

    @Override
    public boolean isCommitted() {
        return out.isCommitted();
    }

    @Override
    public void reset() {
        resetBuffer();
    }

    @Override
    public void setLocale(Locale loc) {
        if (loc != null && !isCommitted()) {
            locale = loc;
        }
    }

    protected void ensureNotCommitted(String message) throws IllegalStateException {
        if (isCommitted()) {
            throw new IllegalStateException(message);
        }
    }

    private void ensureResponseFresh() throws IllegalStateException {
        if (state != FRESH) {
            throw new IllegalStateException((state == BY_GET_WRITER ? "getWriter" : "getOutputStream")
                    + "() has already been called for this response");
        }
    }
}
