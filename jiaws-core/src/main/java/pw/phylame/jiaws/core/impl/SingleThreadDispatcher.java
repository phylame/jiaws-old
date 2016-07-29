package pw.phylame.jiaws.core.impl;

import java.net.Socket;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.core.AbstractDispatcher;
import pw.phylame.jiaws.core.ProtocolProcessor;
import pw.phylame.jiaws.core.RequestDispatcher;
import pw.phylame.jiaws.util.Pair;

public class SingleThreadDispatcher extends AbstractDispatcher implements RequestDispatcher {

    @Override
    public void dispatch(ServletRequest request, ServletResponse response, ProtocolProcessor processor, Socket socket) {
        handleRequest(request, response, processor, socket);
    }

    @Override
    public List<Pair<ServletRequest, ServletResponse>> cancel() {
        throw new UnsupportedOperationException("Unsupported cancel operation for single thread dispatcher");
    }
}
