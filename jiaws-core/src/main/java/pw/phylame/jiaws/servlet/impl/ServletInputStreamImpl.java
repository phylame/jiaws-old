package pw.phylame.jiaws.servlet.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import lombok.NonNull;
import pw.phylame.jiaws.util.ImplementUtils;

public class ServletInputStreamImpl extends ServletInputStream {
    private InputStream in;

    public ServletInputStreamImpl(@NonNull InputStream in) {
        this.in = in;
    }

    @Override
    public boolean isFinished() {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public boolean isReady() {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        ImplementUtils.raiseForImpl();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int hashCode() {
        return in.hashCode();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public boolean equals(Object obj) {
        return in.equals(obj);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public String toString() {
        return in.toString();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
}
