package pw.phylame.jiaws.core;

import java.net.SocketAddress;

public interface Connector extends Lifecycle, ServerAware, AutoCloseable {
    void setAddress(SocketAddress address);
}
