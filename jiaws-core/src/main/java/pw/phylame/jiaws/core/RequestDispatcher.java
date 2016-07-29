package pw.phylame.jiaws.core;

import java.net.Socket;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.Pair;

/**
 * Dispatches client request in some way.
 * <p>
 * How to dispatch the request is implemented by sub-class.
 */
public interface RequestDispatcher {
    /**
     * Dispatches specified request and response in one way.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @param processor
     *            processor for rendering response
     * @param socket
     *            the socket to send response
     */
    void dispatch(ServletRequest request, ServletResponse response, ProtocolProcessor processor, Socket socket);

    /**
     * Cancels all executions for socket.
     * 
     * @return list of request and response that never processed
     */
    List<Pair<ServletRequest, ServletResponse>> cancel();
}
