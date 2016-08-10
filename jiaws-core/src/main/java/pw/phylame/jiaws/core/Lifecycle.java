/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
