/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package pw.phylame.jiaws.core;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pw.phylame.jiaws.spike.InputObject;
import pw.phylame.jiaws.util.Validator;

public abstract class AbstractConnector<I extends InputObject> extends LifecycleSupport
        implements Connector<I>, ServerAware {
    /**
     * Holds weak reference of current server.
     */
    protected WeakReference<Server> serverRef;

    @Setter
    @NonNull
    protected SocketAddress address;

    /**
     * Configuration of the connector.
     */
    @Getter
    protected ConnectorConfig<I> config;

    @Getter
    private List<I> unhandled;

    protected AbstractConnector(@NonNull ConnectorConfig<I> config) {
        validateConfig(config);
        this.config = config;
    }

    protected void validateConfig(ConnectorConfig<I> config) {
        Validator.notNull(config.getDispatcher(), "Dispatcher cannot be null");
        Validator.notNull(config.getParser(), "Parser cannot be null");
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
        unhandled = config.getDispatcher().cancel();
    }

    @Override
    public void setServer(@NonNull Server server) {
        serverRef = new WeakReference<Server>(server);
        server.setRetainedTo(config.getDispatcher());
        server.setRetainedTo(config.getParser());
    }
}
