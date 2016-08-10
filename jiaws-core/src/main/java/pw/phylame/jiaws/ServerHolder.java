package pw.phylame.jiaws;

import java.lang.ref.WeakReference;

import lombok.NonNull;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerAware;

public abstract class ServerHolder implements ServerAware {
    protected WeakReference<Server> serverRef;

    @Override
    public void setServer(@NonNull Server server) {
        serverRef = new WeakReference<>(server);
    }
}
