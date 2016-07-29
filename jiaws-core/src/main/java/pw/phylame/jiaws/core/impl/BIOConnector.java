package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.net.ServerSocket;

import lombok.val;
import pw.phylame.jiaws.core.AbstractConnector;
import pw.phylame.jiaws.core.ConnectorConfig;

public class BIOConnector extends AbstractConnector {

    private ServerSocket serverSocket;

    public BIOConnector(ConnectorConfig config) {
        super(config);
    }

    /**
     * Status for stop receiving client request.
     */
    private volatile boolean cancelled = false;

    @Override
    protected void doStart() throws IOException {
        super.doStart();
        logger.info("{}@{} starting...", getClass().getSimpleName(), hashCode());
        serverSocket = new ServerSocket();
        logger.info("Binding address {}...", address);
        serverSocket.bind(address);
        val processor = config.getProcessor();
        val dispatcher = config.getDispatcher();
        while (!cancelled) {
            val socket = serverSocket.accept();
            if (socket.isConnected()) {
                val pair = processor.parse(socket);
                // bad request
                if (pair == null) {
                    continue;
                }
                socket.shutdownInput();
                dispatcher.dispatch(pair.getFirst(), pair.getSecond(), processor, socket);
            }
        }
    }

    @Override
    protected void doStop() throws IOException {
        cancelled = true;
        super.doStop();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
