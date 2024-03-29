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

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.spike.InputObject;
import pw.phylame.jiaws.spike.ProtocolParser;

/**
 * Dispatches client request in some way.
 * <p>
 * How to dispatch the request is implemented by sub-class.
 */
public interface Dispatcher<I extends InputObject> {
    /**
     * Dispatches specified client input in one way.
     * <p>
     * The input should be closed after sending response to client.
     * 
     * @param parser
     *            the protocol parser for the input
     * @param input
     *            the input for <code>ProtocolParser</code>
     */
    void dispatch(ProtocolParser<? extends ServletRequest, ? extends ServletResponse, I> parser, I input);

    /**
     * Cancels all executions for input.
     * 
     * @return list of request input that never processed
     */
    List<I> cancel();
}
