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

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import lombok.AllArgsConstructor;
import pw.phylame.jiaws.core.AbstractDispatcher;
import pw.phylame.jiaws.core.Dispatcher;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.spike.ProtocolParser;

public class ExecutorServiceDispatcher extends AbstractDispatcher implements Dispatcher, ServerAware {
    private final ExecutorService executorService;

    public ExecutorServiceDispatcher(ProtocolParser parser, ExecutorService executorService) {
        super(parser);
        this.executorService = executorService;
    }

    @Override
    public void dispatch(Socket socket) {
        executorService.submit(new DispatcherTask(socket));
    }

    @Override
    public List<Socket> cancel() {
        List<Socket> result = new ArrayList<>();
        for (Runnable r : executorService.shutdownNow()) {
            result.add(((DispatcherTask) r).socket);
        }
        return result;
    }

    @AllArgsConstructor
    private class DispatcherTask implements Runnable {
        private Socket socket;

        @Override
        public void run() {
            processSocket(socket);
        }
    }
}
