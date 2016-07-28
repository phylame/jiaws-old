package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.net.ServerSocket;

import lombok.val;
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
        System.out.println("Connector starting...");
        serverSocket = new ServerSocket();
        System.out.printf("Binding address %s...\n", address);
        serverSocket.bind(address);
        val dispatcher = config.getDispatcher();
        while (!cancelled) {
            dispatcher.dispatch(serverSocket.accept());
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
