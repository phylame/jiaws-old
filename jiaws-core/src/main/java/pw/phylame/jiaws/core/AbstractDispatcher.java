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

package pw.phylame.jiaws.core;

import java.lang.ref.WeakReference;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.val;
import pw.phylame.jiaws.spike.ProtocolParser;
import pw.phylame.jiaws.util.HttpUtils;
import pw.phylame.jiaws.util.SocketUtils;

public abstract class AbstractDispatcher implements Dispatcher, ServerAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected WeakReference<Server> serverRef;

    private final ProtocolParser parser;

    protected AbstractDispatcher(@NonNull ProtocolParser parser) {
        this.parser = parser;
    }

    @Override
    public void setServer(Server server) {
        serverRef = new WeakReference<Server>(server);
        server.setRetainedTo(parser);
    }

    protected void processSocket(Socket socket) {
        try {
            val pair = parser.parse(socket);
            socket.shutdownInput();
            serverRef.get().handleRequest(pair.getFirst(), pair.getSecond());
            HttpUtils.flushResponse(pair.getSecond());
        } catch (Exception e) {
            logger.debug("Failed to dispatch socket", e);
        } finally {
            SocketUtils.cleanup(socket);
        }
    }
}
