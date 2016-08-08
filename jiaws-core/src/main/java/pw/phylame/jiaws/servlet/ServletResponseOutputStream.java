package pw.phylame.jiaws.servlet;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import lombok.NonNull;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.jiaws.util.ImplementUtils;

public class ServletResponseOutputStream extends ServletOutputStream {
    private final ResponseOutputStream out;

    public ServletResponseOutputStream(@NonNull ResponseOutputStream out) {
        this.out = out;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        ImplementUtils.raiseForImpl();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
