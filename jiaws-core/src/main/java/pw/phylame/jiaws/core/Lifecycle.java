package pw.phylame.jiaws.core;

import java.io.IOException;

import pw.phylame.jiaws.util.LifecycleStateException;

public interface Lifecycle {
    /**
     * Tests the object is started or not.
     * 
     * @return <code>true</code> if is started, otherwise <code>false</code>
     */
    boolean isStarted();

    /**
     * Does start action of the object.
     * 
     * @throws LifecycleStateException
     *             if is already started
     * @throws IOException
     *             if occurs IO error
     */
    void start() throws LifecycleStateException, IOException;

    /**
     * Tests the object is stopped or not.
     * 
     * @return <code>true</code> if is stopped, otherwise <code>false</code>
     */
    boolean isStopped();

    /**
     * Does stop action of the object.
     * 
     * @throws LifecycleStateException
     *             if is already stopped
     * @throws IOException
     *             if occurs IO error
     */
    void stop() throws LifecycleStateException, IOException;

    /**
     * Does restart(stop then start) action of the object.
     * 
     * @throws LifecycleStateException
     *             if is already stopped
     * @throws IOException
     *             if occurs IO error
     */
    void restart() throws LifecycleStateException, IOException;
}
