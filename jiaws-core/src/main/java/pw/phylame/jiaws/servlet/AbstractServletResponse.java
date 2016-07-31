/*
 * Copyright 2014-2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.jiaws.servlet;

import lombok.Getter;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.phylame.jiaws.apraw.ResponseWriter;
import pw.phylame.jiaws.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

public abstract class AbstractServletResponse implements ServletResponse {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ResponseWriter writer;

    private String characterEncoding = null;

    /**
     * The content type without charset.
     */
    private String contentType = null;

    private long contentLength;

    /**
     * State of the response. 0: fresh, 1: written by getWriter, 2: written by getOutputStream
     */
    private int state = FRESH;

    private static final int FRESH = 0;
    private static final int BY_GET_WRITER = 1;
    private static final int BY_GET_OUTPUT_STREAM = 2;

    @Getter
    private Locale locale = Locale.getDefault();

    @Override
    public String getCharacterEncoding() {
        return characterEncoding != null ? characterEncoding : "ISO-8859-1";
    }

    @Override
    public String getContentType() {
        return contentType == null || characterEncoding == null
                ? contentType
                : contentType + "; charset=" + characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ensureResponseFresh();
        state = BY_GET_OUTPUT_STREAM;
        return writer.openOutputStream();
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
        // not auto flush
        return new PrintWriter(new OutputStreamWriter(writer.openOutputStream(), encoding), false);
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
            contentType = type;
            val encoding = StringUtils.getValueOfName(type, "charset", ";", false);
            if (StringUtils.isNotEmpty(encoding)) {
                setCharacterEncoding(encoding);
            }
        }
    }

    @Override
    public int getBufferSize() {
        return writer.getBufferSize();
    }

    @Override
    public void setBufferSize(int size) {
        ensureNotCommitted("Cannot set buffer size after response has been committed");
        writer.setBufferSize(size);
    }

    @Override
    public void flushBuffer() throws IOException {
        if (isCommitted()) {
            return;
        }
        writer.flush();
    }

    @Override
    public void resetBuffer() {
        ensureNotCommitted("Cannot reset buffer after response has been committed");
        // todo: keep status code and headers, clear body content in buffer
        writer.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return writer.isCommitted();
    }

    @Override
    public void reset() {
        ensureNotCommitted("Cannot call reset() after response has been committed");
    }

    @Override
    public void setLocale(Locale loc) {
        if (loc != null && !isCommitted()) {
            locale = loc;
        }
    }

    private void ensureNotCommitted(String message) throws IllegalStateException {
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
