package pw.phylame.jiaws.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.util.Exceptions;

public class ChannelInputStream extends InputStream {
    private ReadableByteChannel channel;

    private volatile ByteBuffer buffer;

    public ChannelInputStream(ReadableByteChannel channel) {
        this(channel, IOConstanst.DEFAULT_BUFFER_SIZE);
    }

    public ChannelInputStream(@NonNull ReadableByteChannel channel, int bufferSize) {
        if (bufferSize < 0) {
            throw Exceptions.forIllegalArgument("buffer size < 0: {}", bufferSize);
        }
        this.channel = channel;
        buffer = ByteBuffer.allocate(bufferSize);
        buffer.flip();
    }

    private ByteBuffer buffer() throws IOException {
        if (buffer == null) {
            throw new IOException("Stream closed");
        }
        return buffer;
    }

    private void fill() throws IOException {
        val buf = buffer();
        buf.clear();
        channel.read(buf);
        buf.flip();
    }

    @Override
    public synchronized int read() throws IOException {
        val buf = buffer();
        if (!buf.hasRemaining()) {
            fill();
            if (!buf.hasRemaining())
                return -1;
        }
        return buf.get() & 0xff;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        val buf = buffer();
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        if (len <= buf.remaining()) {
            buf.get(b, off, len);
            return len;
        }
        if (!buf.hasRemaining()) {
            fill();
            if (!buf.hasRemaining()) {
                return 0;
            }
        }
        int total = 0;
        while (total < len) {
            int num = min(buf.remaining(), len - total);
            buf.get(b, off, num);
            off += num;
            total += num;
            if (!buf.hasRemaining()) {
                fill();
                if (!buf.hasRemaining()) {
                    return total;
                }
            }
        }
        return total;
    }

    private int min(int a, int b) {
        return a < b ? a : b;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        val buf = buffer();
        if (n < 0) {
            return 0;
        } else if (n < buf.remaining()) {
            buf.position(buf.position() + (int) n);
            return n;
        }
        if (!buf.hasRemaining()) {
            fill();
            if (!buf.hasRemaining()) {
                return 0;
            }
        }
        int total = 0;
        while (total < n) {
            int num = min(buf.remaining(), (int) (n - total));
            buf.position(buf.position() + num);
            total += num;
            if (!buf.hasRemaining()) {
                fill();
                if (!buf.hasRemaining()) {
                    return total;
                }
            }
        }
        return total;
    }

    @Override
    public int available() throws IOException {
        return buffer().remaining();
    }

    @Override
    public void close() throws IOException {
        buffer = null;
        channel = null;
    }
}
