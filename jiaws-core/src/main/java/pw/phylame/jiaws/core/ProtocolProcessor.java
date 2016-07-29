package pw.phylame.jiaws.core;

import java.io.IOException;
import java.net.Socket;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.Pair;

/**
 * Processes client request and render response.
 *
 */
public interface ProtocolProcessor {
    /**
     * Parses socket to <code>ServletRequest</code>.
     * <p>
     * If the protocol of client request is invalid, the implementation should
     * return <code>null</code> and send an error to client.
     * 
     * @param socket
     *            the socket
     * @return pair of request and corresponding response or <code>null</code>
     *         if protocol is invalid
     * 
     * @throws IOException
     *             if occurs IO error
     */
    <REQ extends ServletRequest, RES extends ServletResponse> Pair<REQ, RES> parse(Socket socket) throws IOException;

    /**
     * Renders the <code>ServletResponse</code> to socket.
     * 
     * @param response
     *            the servlet response
     * @param socket
     *            the socket
     * @throws IOException
     *             if occurs IO error
     */
    <RES extends ServletResponse> void render(RES response, Socket socket) throws IOException;
}
