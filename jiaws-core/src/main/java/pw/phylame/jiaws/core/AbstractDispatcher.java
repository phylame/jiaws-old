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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import pw.phylame.jiaws.spike.ProtocolParser;

public abstract class AbstractDispatcher implements Dispatcher, ServerAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected WeakReference<Server> serverRef;

    private ProtocolParser parser;

    @Override
    public void setServer(Server server) {
        serverRef = new WeakReference<Server>(server);
        server.setRetainedTo(parser);
    }

    @Override
    public void dispatch(Socket socket) throws IOException {
        val pair = parser.parse(socket);
        // bad request
        if (pair == null) {
            return;
        }
        socket.shutdownInput();
        try {
            doService(pair.getFirst(), pair.getSecond(), socket);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void handToServer(ServletRequest request, ServletResponse response) {
        serverRef.get().handleRequest(request, response);
    }

    protected abstract void doService(ServletRequest request, ServletResponse response, Socket socket) throws Exception;

    protected void cleanupSocket(Socket socket) {
        try {
            socket.shutdownOutput();
        } catch (IOException e) {
            logger.error("Cannot render response to socket", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Cannot close client socket", e);
            }
        }
    }
}
