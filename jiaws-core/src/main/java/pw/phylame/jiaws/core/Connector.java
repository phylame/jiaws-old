package pw.phylame.jiaws.core;

import java.net.SocketAddress;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.Pair;

/**
 * The connector layer.
 * <p>
 * Purpose:
 * <ul>
 * <li>Receive client request</li>
 * <li>Parse request to <code>ServletRequest</code></li>
 * <li>Render <code>ServletResponse</code> as HTTP to client</li>
 * </ul>
 *
 */
public interface Connector extends Lifecycle, AutoCloseable {
    /**
     * Sets the address that the connector bind on.
     * 
     * @param address
     *            the socket address
     */
    void setAddress(SocketAddress address);

    /**
     * Gets all unhandled request and response.
     * 
     * @return list of unhandled request and response
     */
    List<Pair<ServletRequest, ServletResponse>> getUnhandled();
}
