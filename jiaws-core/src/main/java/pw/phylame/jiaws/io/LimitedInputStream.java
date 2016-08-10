package pw.phylame.jiaws.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import pw.phylame.jiaws.util.Exceptions;

public class LimitedInputStream extends FilterInputStream {
    private long count = 0;

    private final long limit;

    public LimitedInputStream(InputStream in, long limit) {
        super(in);
        if (limit < 0) {
            throw Exceptions.forIllegalArgument("limit < 0: %s", limit);
        }
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        ensureOpen();
        if (count++ < limit) {
            return in.read();
        } else {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int n;
        if (count + len <= limit) {
            n = in.read(b, off, len);
            count += n;
            return n;
        } else {
            n = (int) (limit - count);
            in.read(b, off, n);
            count = limit;
            return n;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        ensureOpen();
        if (n <= 0) {
            return 0;
        } else if (count + n <= limit) { // in range
            n = in.skip(n);
            count += n;
            return n;
        } else { // out of range
            n = limit - count;
            count = limit;
            return n;
        }
    }

    @Override
    public int available() throws IOException {
        ensureOpen();
        return Math.min(super.available(), (int) (limit - count));
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private void ensureOpen() throws IOException {
        if (count < 0) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    public void close() throws IOException {
        count = -1;
    }
}
