package pw.phylame.jiaws.util;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.util.values.Triple;

public final class SocketUtils {
    private static final Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    private SocketUtils() {
    }

    public static void cleanup(Socket socket) {
        if (socket == null) {
            return;
        }
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }
            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        } catch (IOException e) {
            logger.error("Cannot clean up socket", e);
        } finally {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Cannot close client socket", e);
                }
            }
        }
    }

    // ip, hostname, port
    public static Triple<String, String, Integer> getLocalBind(@NonNull Socket socket) {
        val address = socket.getLocalAddress();
        return new Triple<>(address.getHostAddress(), address.getHostName(), socket.getLocalPort());
    }

    public static Triple<String, String, Integer> getRemoteBind(@NonNull Socket socket) {
        val address = socket.getInetAddress();
        return new Triple<>(address.getHostAddress(), address.getHostName(), socket.getPort());
    }
}
