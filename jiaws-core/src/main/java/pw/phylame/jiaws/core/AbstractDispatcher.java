package pw.phylame.jiaws.core;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDispatcher implements RequestDispatcher, ServerAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected WeakReference<Server> serverRef;

    @Override
    public void setServer(Server server) {
        serverRef = new WeakReference<Server>(server);
    }

    protected void handleRequest(ServletRequest request, ServletResponse response, ProtocolProcessor processor,
            Socket socket) {
        serverRef.get().handleRequest(request, response);
        try {
            processor.render(response, socket);
            socket.shutdownOutput();
        } catch (IOException e) {
            logger.error("Cannot render response to socket", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Cannot close client socket", e);
            }
        }
    }
}
