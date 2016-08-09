package pw.phylame.jiaws.spike;

import java.io.IOException;
import java.net.Socket;

import lombok.NonNull;
import lombok.Value;

@Value
public class SocketInput implements InputObject {
    @NonNull
    private Socket socket;

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
