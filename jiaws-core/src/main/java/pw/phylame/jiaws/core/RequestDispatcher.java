package pw.phylame.jiaws.core;

import java.net.Socket;
import java.util.List;

public interface RequestDispatcher extends ServerAware {
    /**
     * Dispatches specified socket in one way.
     * 
     * @param socket
     *            the client socket
     */
    void dispatch(Socket socket);

    /**
     * Cancels all executions for socket.
     * 
     * @return list of socket that never processed
     */
    List<Socket> cancel();
}
