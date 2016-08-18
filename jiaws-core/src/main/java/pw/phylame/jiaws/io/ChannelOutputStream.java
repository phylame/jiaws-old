package pw.phylame.jiaws.io;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import lombok.NonNull;
import pw.phylame.jiaws.util.Exceptions;

public class ChannelOutputStream extends OutputStream {
    private WritableByteChannel channel;

    private volatile ByteBuffer buffer;

    public ChannelOutputStream(WritableByteChannel channel) {
        this(channel, IOConstanst.DEFAULT_BUFFER_SIZE);
    }

    public ChannelOutputStream(@NonNull WritableByteChannel channel, int bufferSize) {
        if (bufferSize < 0) {
            throw Exceptions.forIllegalArgument("buffer size < 0: {}", bufferSize);
        }
        this.channel = channel;
        buffer = ByteBuffer.allocate(bufferSize);
        buffer.clear();
    }

    private void flushBuffer() throws IOException {
        if (buffer.position() > 0) {
            buffer.flip();
            channel.write(buffer);
            buffer.clear();
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (!buffer.hasRemaining()) {
            flushBuffer();
        }
        buffer.put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // if (len >= buffer.remaining()) {
        // flushBuffer();
        // out.write(b, off, len);
        //
        // return;
        // }
        // if (len > buf.length - count) {
        // flushBuffer();
        // }
        // System.arraycopy(b, off, buf, count, len);
        // count += len;
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
    }

    @Override
    public void close() throws IOException {
        flushBuffer();
        buffer = null;
        channel = null;
    }

}
