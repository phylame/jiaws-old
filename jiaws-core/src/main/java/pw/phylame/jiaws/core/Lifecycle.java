package pw.phylame.jiaws.core;

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
     */
    void start() throws LifecycleStateException;

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
     */
    void stop() throws LifecycleStateException;

    /**
     * Does restart(stop then start) action of the object.
     * 
     * @throws LifecycleStateException
     *             if is already stopped
     */
    void restart() throws LifecycleStateException;
}
