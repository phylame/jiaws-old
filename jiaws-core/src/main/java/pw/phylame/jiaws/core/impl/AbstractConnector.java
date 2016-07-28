package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;

import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.core.Connector;
import pw.phylame.jiaws.core.ConnectorConfig;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.util.Validator;

public abstract class AbstractConnector extends LifecycleSupport implements Connector {
    protected ConnectorConfig config;

    @Setter
    @NonNull
    protected SocketAddress address;

    protected WeakReference<Server> server;

    protected AbstractConnector(@NonNull ConnectorConfig config) {
        validateConfig(config);
        this.config = config;
    }

    protected void validateConfig(ConnectorConfig config) {
        Validator.notNull(config.getDispatcher(), "Dispatcher cannot be null");
    }

    @Override
    public final void close() throws Exception {
        if (!isStopped()) {
            stop();
        }
    }

    @Override
    protected void doStart() throws IOException {
        Validator.notNull(address, "Required socket address is null");
    }

    @Override
    protected void doStop() throws IOException {
        config.getDispatcher().cancel();
    }

    @Override
    public void setServer(@NonNull Server server) {
        this.server = new WeakReference<Server>(server);
        config.getDispatcher().setServer(server);
    }
}
