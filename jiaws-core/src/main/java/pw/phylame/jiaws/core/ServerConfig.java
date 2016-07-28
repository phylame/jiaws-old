package pw.phylame.jiaws.core;

import java.net.SocketAddress;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ServerConfig {
    @NonNull
    private SocketAddress address;

    @NonNull
    private Connector connector;

    /**
     * Limit of request header count.
     */
    private int maxRequestHeaderCount = 100;

    /**
     * Limit of size of each request header.
     */
    private int maxRequestHeaderSize = 8190;
}
