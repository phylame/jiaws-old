package pw.phylame.jiaws.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.val;
import pw.phylame.jiaws.util.Exceptions;

public class ResponseOutputStreamOld extends BufferedOutputStream {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int MIN_BUFFER_SIZE = 1024;

    @Getter
    private boolean flushed = false;

    /**
     * Remaining buffer data that should be written in next flushing operation.
     */
    private BufferRange remain = null;

    private List<ResponseWriteListener> listeners = new LinkedList<>();

    /**
     * Number of data that written. When content length is not set(not call
     * flushBuffer or setContentLength), count the written bytes.
     */
    private long writtenBytes = 0;

    public ResponseOutputStreamOld(OutputStream out, int size) {
        super(out, size);
    }

    public ResponseOutputStreamOld(OutputStream out) {
        super(out);
    }

    public int getBufferSize() {
        return buf.length;
    }

    /**
     * Sets the preferred buffer size for the body of the response
     * 
     * @param size
     *            the preferred buffer size
     * @throws IllegalStateException
     *             if this method is called after content has been written
     */
    public synchronized void setBufferSize(int size) throws IllegalStateException {
        if (isFlushed()) {
            throw Exceptions.forIllegalState("Cannot change buffer size after data has been written");
        }
        if (size == buf.length) {
            return;
        } else if (size < MIN_BUFFER_SIZE) {
            logger.info("Specified new size({}) < MIN_BUFFER_SIZE({})", size, MIN_BUFFER_SIZE);
            return;
        }
        if (count < size) {
            buf = Arrays.copyOf(buf, size);
        } else {
            remain = new BufferRange(buf, 0, count - size);
            buf = Arrays.copyOfRange(buf, count - size, count);
            count = size;
        }
    }

    /**
     * Clears the content of the underlying buffer.
     * 
     * @throws IllegalStateException
     *             if this method is called after content has been written
     */
    public synchronized void resetBuffer() throws IllegalStateException {
        if (isFlushed()) {
            throw Exceptions.forIllegalState("Response has been committed");
        }
        count = 0;
        writtenBytes = 0;
    }

    private boolean flushRemain() throws IOException {
        if (remain != null) {
            remain.writeTo(out);
            remain = null;
            return true;
        }
        return false;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        // current data size in buffer
        val current = count;
        boolean dispatched = false;
        if (remain != null || count >= buf.length) {
            dispatchBeforeWriteEvent();
            dispatched = true;
        }
        flushRemain();
        super.write(b);
        writtenBytes++;
        // data has been written to out, i.e. being committed
        flushed = current != count;
        if (dispatched) {
            dispatchAfterWriteEvent();
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        // current data size in buffer
        val current = count;
        boolean dispatched = false;
        if (remain != null || count >= buf.length) {
            dispatchBeforeWriteEvent();
            dispatched = true;
        }
        flushRemain();
        super.write(b, off, len);
        writtenBytes += len;
        // data has been written to out, i.e. being committed
        flushed = current != count;
        if (dispatched) {
            dispatchAfterWriteEvent();
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        dispatchBeforeWriteEvent();
        flushRemain();
        super.flush();
        flushed = true;
        dispatchAfterWriteEvent();
    }

    public void writeAndFlush(byte[] b) throws IOException {
        out.write(b);
    }

    public void writeAndFlush(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void addResponseWriteListener(ResponseWriteListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeResponseWriteListener(ResponseWriteListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    private void dispatchBeforeWriteEvent() throws IOException {
        val e = new ResponseWriteEvent(this);
        for (ResponseWriteListener l : listeners) {
            l.beforeWrite(e);
        }
    }

    private void dispatchAfterWriteEvent() throws IOException {
        val e = new ResponseWriteEvent(this);
        for (ResponseWriteListener l : listeners) {
            l.afterWrite(e);
        }
    }
}
