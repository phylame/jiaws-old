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
public interface Dispatcher {
    /**
     * Dispatches specified client socket in one way.
     * 
     * @param socket
     *            the socket to send response
     * @throws IOException
     *             if occur IO error
     */
    void dispatch(Socket socket) throws IOException;

    /**
     * Cancels all executions for socket.
     * 
     * @return list of request and response that never processed
     */
    List<Pair<ServletRequest, ServletResponse>> cancel();
}
