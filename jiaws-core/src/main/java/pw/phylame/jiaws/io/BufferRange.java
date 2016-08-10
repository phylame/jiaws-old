package pw.phylame.jiaws.io;

import java.io.IOException;
import java.io.OutputStream;

import lombok.Getter;
import lombok.NonNull;
import pw.phylame.jiaws.util.Exceptions;

public class BufferRange {
    private final byte[] buf;

    @Getter
    private final int offet;

    @Getter
    private final int length;

    public BufferRange(@NonNull byte[] buf, int offset, int length) {
        if (offset < 0 || offset >= buf.length) {
            throw Exceptions.forIllegalArgument("'offset' must be in [0, %d], actual(%d)", buf.length - 1, offset);
        }
        if (offset + length > buf.length) {
            throw Exceptions.forIllegalArgument("'length' must be in [0, %d]", buf.length - offset);
        }
        this.buf = buf;
        this.offet = offset;
        this.length = length;
    }

    public void writeTo(@NonNull OutputStream out) throws IOException {
        out.write(buf, offet, length);
    }
}
