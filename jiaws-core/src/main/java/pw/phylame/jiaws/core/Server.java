package pw.phylame.jiaws.core;

import java.io.IOException;

import lombok.NonNull;
import pw.phylame.jiaws.core.impl.LifecycleSupport;
import pw.phylame.jiaws.util.LifecycleStateException;
import pw.phylame.jiaws.util.Validator;

public class Server extends LifecycleSupport {
    private final ServerConfig config;

    public Server(@NonNull ServerConfig config) {
        this.config = config;
        init();
    }

    protected void init() {
        Validator.notNull(config.getAddress(), "Address cannot be null");
        Validator.notNull(config.getConnector(), "Connector cannot be null");
        config.getConnector().setAddress(config.getAddress());
        config.getConnector().setServer(this);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!isStopped()) {
                    try {
                        Server.this.stop();
                    } catch (LifecycleStateException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    Context addWebapp(@NonNull String path) {
        return null;
    }

    @Override
    protected void doStart() throws IOException {
        System.out.println("Starting server...");
        try {
            config.getConnector().start();
        } catch (LifecycleStateException e) {
            throw new IllegalStateException("Connector is already started", e);
        }
    }

    @Override
    protected void doStop() throws IOException {
        System.out.println("Stopping server...");
        try {
            config.getConnector().stop();
        } catch (LifecycleStateException e) {
            throw new IllegalStateException("Connector is already stopped", e);
        }
    }
}
