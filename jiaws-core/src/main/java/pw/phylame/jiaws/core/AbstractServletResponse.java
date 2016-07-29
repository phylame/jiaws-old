package pw.phylame.jiaws.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.util.HttpUtils;
import pw.phylame.jiaws.util.StringUtils;

public class AbstractServletResponse implements ServletResponse {

    @Setter
    @NonNull
    private String characterEncoding = null;

    private String contentType = null;

    /**
     * Indicates that the body is written by getWriter or getOutputStream.
     */
    private boolean bodyWritten = false;

    /**
     * Indicates that the body is written by getWriter.
     */
    private boolean writtenByWriter = false;

    @Getter
    private long contentLengthLong;

    @Getter
    private int bufferSize = 0;

    /**
     * Indicates that the status code and headers has been written.
     */
    @Getter
    private boolean committed = false;

    @Getter
    private Locale locale = Locale.getDefault();

    @Override
    public String getCharacterEncoding() {
        return characterEncoding != null ? characterEncoding : "ISO-8859-1";
    }

    @Override
    public String getContentType() {
        if (contentType == null) {
            return null;
        }
        return contentType.contains("charset=") || characterEncoding == null ? contentType
                : contentType + "; charset=" + characterEncoding;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        ensureBodyFresh();
        bodyWritten = true;
        writtenByWriter = false;
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        ensureBodyFresh();
        bodyWritten = true;
        writtenByWriter = true;
        return null;
    }

    @Override
    public void setContentLength(int len) {
        setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        contentType = type;
        if (!bodyWritten || !isCommitted()) {
            String encoding = HttpUtils.getCharsetForContentType(type);
            if (type != null) {
                setCharacterEncoding(type);
            }
        }
    }

    @Override
    public void setBufferSize(int size) {
        ensureNotCommitted();
    }

    @Override
    public void flushBuffer() throws IOException {
        // writeStatusAndHeaders();
    }

    // protected abstract void writeStatusAndHeaders() throws IOException;

    @Override
    public void resetBuffer() {
        ensureNotCommitted();
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
        if (bodyWritten) {
            throw new IllegalStateException((writtenByWriter ? "getWriter" : "getOutputStream")
                    + "() has already been called for this response");
        }
    }
}
