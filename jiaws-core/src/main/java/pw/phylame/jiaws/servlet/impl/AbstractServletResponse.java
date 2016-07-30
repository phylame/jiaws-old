/*
 * Copyright 2016 Peng Wan <phylame@163.com>
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

package pw.phylame.jiaws.servlet.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.util.StringUtils;

public class AbstractServletResponse implements ServletResponse {

    private String characterEncoding = null;

    private String contentType = null;

    @Getter
    private long contentLengthLong;

    @Getter
    private int bufferSize = 0;

    /**
     * Indicates that the status code and headers has been written.
     */
    @Getter
    private boolean committed = false;

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
        if (contentType == null || contentType.contains("charset=") || characterEncoding == null) {
            return contentType;
        }
        return contentType + "; charset=" + characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ensureBodyFresh();
        state = BY_GET_OUTPUT_STREAM;
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        ensureBodyFresh();
        state = BY_GET_WRITER;
        val encoding = getCharacterEncoding();
        if (!Charset.isSupported(encoding)) {
            throw new UnsupportedEncodingException(String.format("Character encoding '%s' is unsupported", encoding));
        }
        return null;
    }

    @Override
    public void setCharacterEncoding(@NonNull String charset) {
        // todo check committed
        characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        setContentLengthLong(len);
    }

    @Override
    public void setContentType(@NonNull String type) {
        contentType = type;
        val encoding = StringUtils.getValueOfName(type, "charset", ";", false);
        if (encoding != null) {
            setCharacterEncoding(encoding);
        }
    }

    @Override
    public void setBufferSize(int size) {
        ensureNotCommitted();
        // todo: resize buffer size
        bufferSize = size;
    }

    @Override
    public void flushBuffer() throws IOException {
        // writeStatusAndHeaders();
        // todo: write all data to client
    }

    // protected abstract void writeStatusAndHeaders() throws IOException;

    @Override
    public void resetBuffer() {
        ensureNotCommitted();
        // todo: keep status code and headers, clear body content in buffer
    }

    @Override
    public void reset() {
        ensureNotCommitted();
    }

    @Override
    public void setLocale(@NonNull Locale loc) {
        if (isCommitted()) {
            return;
        }
        if (!encodingUpdated) {
            // TODO set character encoding by locale
        }
        locale = loc;
    }

    private void ensureNotCommitted() throws IllegalStateException {
        if (isCommitted()) {
            throw new IllegalStateException("Response has been committed");
        }
    }

    private void ensureBodyFresh() throws IllegalStateException {
        if (state != FRESH) {
            throw new IllegalStateException((state == BY_GET_WRITER ? "getWriter" : "getOutputStream")
                    + "() has already been called for this response");
        }
    }
}
