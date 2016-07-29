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
