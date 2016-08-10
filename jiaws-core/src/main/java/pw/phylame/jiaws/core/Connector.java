/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package pw.phylame.jiaws.core;

import java.net.SocketAddress;
import java.util.List;

import pw.phylame.jiaws.spike.InputObject;

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
public interface Connector<I extends InputObject> extends Lifecycle, AutoCloseable {
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
    List<I> getUnhandled();
}
