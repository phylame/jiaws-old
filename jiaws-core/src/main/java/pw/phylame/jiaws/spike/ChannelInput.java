package pw.phylame.jiaws.spike;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;

import lombok.NonNull;
import lombok.Value;

@Value
public class ChannelInput implements InputObject {
    @NonNull
    private ReadableByteChannel channel;

    @Override
    public void close() throws IOException {
        channel.close();
    }

    @Override
    public boolean isClosed() {
        return !channel.isOpen();
    }
}
