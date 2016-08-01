/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.net.ServerSocket;

import lombok.val;
import pw.phylame.jiaws.core.AbstractConnector;
import pw.phylame.jiaws.core.ConnectorConfig;

public class BIOConnector extends AbstractConnector {

    private ServerSocket serverSocket;

    public BIOConnector(ConnectorConfig config) {
        super(config);
    }

    /**
     * Status for stop receiving client request.
     */
    private volatile boolean cancelled = false;

    @Override
    protected void doStart() throws IOException {
        super.doStart();
        logger.info("{}@{} starting...", getClass().getSimpleName(), hashCode());
        serverSocket = new ServerSocket();
        logger.info("Binding address {}...", address);
        serverSocket.bind(address);
        val dispatcher = config.getDispatcher();
        while (!cancelled) {
            val socket = serverSocket.accept();
            if (socket.isConnected()) {
                try {
                    dispatcher.dispatch(socket);
                } catch (Exception e) {
                    logger.error("Error occured in dispatcher", e);
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                }
            }
        }
    }

    @Override
    protected void doStop() throws IOException {
        cancelled = true;
        super.doStop();
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
