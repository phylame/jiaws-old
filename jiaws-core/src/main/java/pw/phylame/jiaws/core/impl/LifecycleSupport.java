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

package pw.phylame.jiaws.core.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import pw.phylame.jiaws.core.Lifecycle;
import pw.phylame.jiaws.util.LifecycleStateException;

public abstract class LifecycleSupport implements Lifecycle {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
