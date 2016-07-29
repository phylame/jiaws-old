package pw.phylame.jiaws.core;

import lombok.NonNull;

/**
 * Indicates that the class may hold current server.
 */
public interface ServerAware {
    void setServer(Server server);
}
