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

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.AllArgsConstructor;
import pw.phylame.jiaws.core.AbstractDispatcher;
import pw.phylame.jiaws.core.ProtocolProcessor;
import pw.phylame.jiaws.core.RequestDispatcher;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.util.Pair;

public class ExecutorServiceDispatcher extends AbstractDispatcher implements RequestDispatcher, ServerAware {
    private final ExecutorService executorService;

    public ExecutorServiceDispatcher(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void dispatch(ServletRequest request, ServletResponse response, ProtocolProcessor processor, Socket socket) {
        executorService.submit(new DispatcherTask(request, response, processor, socket));
    }

    @Override
    public List<Pair<ServletRequest, ServletResponse>> cancel() {
        List<Pair<ServletRequest, ServletResponse>> result = new ArrayList<>();
        for (Runnable r : executorService.shutdownNow()) {
            DispatcherTask task = (DispatcherTask) r;
            result.add(new Pair<ServletRequest, ServletResponse>(task.request, task.response));
        }
        return result;
    }

    @AllArgsConstructor
    private class DispatcherTask implements Runnable {
        private ServletRequest request;
        private ServletResponse response;
        private ProtocolProcessor processor;
        private Socket socket;

        @Override
        public void run() {
            handleRequest(request, response, processor, socket);
        }
    }
}
