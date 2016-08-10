package pw.phylame.jiaws.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.servlet.AbstractServletResponse;
import pw.phylame.jiaws.util.Exceptions;

public class ResponseOutputStream extends FilterOutputStream {
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    public static final int MIN_BUFFER_SIZE = 1024;

    private byte[] buf;

    /**
     * Remaining buffer data that should be written in next flushing operation.
     */
    private BufferRange remain = null;

    private int count;

    /**
     * Total number of data written to the stream.
     */
    @Getter
    private int total = 0;

    /**
     * State indicating that buffer has been flushed to underlying stream.
     */
    @Getter
    private boolean committed = false;

    /**
     * Reference of corresponding servlet response.
     */
    protected WeakReference<AbstractServletResponse> responseRef;

    @Setter
    private OnFirstCommitListener onFirstCommitListener;

    public ResponseOutputStream(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    public ResponseOutputStream(@NonNull OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw Exceptions.forIllegalArgument("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    public void setResponse(@NonNull AbstractServletResponse response) {
        responseRef = new WeakReference<AbstractServletResponse>(response);
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
        if (isCommitted()) {
            throw Exceptions.forIllegalState("Cannot change buffer size after data has been written");
        }
        if (size == buf.length) {
            return;
        } else if (size < MIN_BUFFER_SIZE) {
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
        if (isCommitted()) {
            throw Exceptions.forIllegalState("Response has been committed");
        }
        count = 0;
        total = 0;
    }

    // true: written data, false: not
    private boolean flushRemain() throws IOException {
        if (remain != null) {
            remain.writeTo(out);
            remain = null;
            return true;
        }
        return false;
    }

    // true: written data, false: not
    private boolean flushBuffer() throws IOException {
        boolean state = flushRemain();
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
            state = true;
        }
        return state;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        System.out.println("write byte");
        if (count >= buf.length) {
            setCommitted(flushBuffer());
        }
        buf[count++] = (byte) b;
        total++;
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        System.out.println("write bytes");
        if (len >= buf.length) {
            setCommitted(flushBuffer());
            out.write(b, off, len);
            if (len > 0) { // data written
                setCommitted(true);
            }
            total += len;
            return;
        }
        if (len > buf.length - count) {
            setCommitted(flushBuffer());
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
        total += len;
    }

    @Override
    public synchronized void flush() throws IOException {
        setCommitted(flushBuffer());
        out.flush();
    }

    private void setCommitted(boolean committed) throws IOException {
        if (this.committed) { // already committed
            return;
        }
        this.committed = committed;
        if (onFirstCommitListener != null) {
            onFirstCommitListener.beforCommit(this);
        }
    }

    public static interface OnFirstCommitListener {
        /**
         * Called on first committing to underlying stream
         * 
         * @param out
         *            the <code>ResponseOutputStream</code> object
         * @throws IOException
         *             if occur IO error when calling method of out
         */
        void beforCommit(ResponseOutputStream out) throws IOException;
    }
}
