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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import pw.phylame.jiaws.ServerHolder;
import pw.phylame.jiaws.io.IOUtils;
import pw.phylame.jiaws.spike.InputObject;
import pw.phylame.jiaws.spike.ProtocolParser;

public abstract class AbstractDispatcher<I extends InputObject> extends ServerHolder implements Dispatcher<I> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected void process(ProtocolParser<? extends ServletRequest, ? extends ServletResponse, I> parser, I input) {
        try {
            val pair = parser.parse(input);
            // send request to filter and servlet
            // serverRef.get().handleRequest(pair.getFirst(), pair.getSecond());
            pair.getSecond().flushBuffer();
        } catch (Exception e) {
            logger.debug("Failed to dispatch input", e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }
}
