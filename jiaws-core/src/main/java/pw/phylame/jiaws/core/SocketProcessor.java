package pw.phylame.jiaws.core;

import java.net.Socket;

public interface SocketProcessor extends ServerAware {
    void process(Socket socket) throws Exception;
}
