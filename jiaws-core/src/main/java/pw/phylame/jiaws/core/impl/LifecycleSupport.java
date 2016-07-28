package pw.phylame.jiaws.core.impl;

import java.io.IOException;

import lombok.Getter;
import pw.phylame.jiaws.core.Lifecycle;
import pw.phylame.jiaws.util.LifecycleStateException;

public abstract class LifecycleSupport implements Lifecycle {
    /**
     * Keeps the start/stop status.
     */
    @Getter
    private boolean started = false;

    @Override
    public final void start() throws LifecycleStateException, IOException {
        if (isStarted()) {
            throw new LifecycleStateException(String.format("%s is already started", toString()));
        }
        doStart();
        started = true;
    }

    protected abstract void doStart() throws IOException;

    @Override
    public boolean isStopped() {
        return !started;
    }

    @Override
    public final void stop() throws LifecycleStateException, IOException {
        if (isStopped()) {
            throw new LifecycleStateException(String.format("%s is already stopped", toString()));
        }
        doStop();
        started = false;
    }

    protected abstract void doStop() throws IOException;

    @Override
    public void restart() throws LifecycleStateException, IOException {
        stop();
        start();
    }
}
