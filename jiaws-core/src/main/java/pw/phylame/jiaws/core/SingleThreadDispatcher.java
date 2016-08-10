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
import pw.phylame.jiaws.util.Exceptions;

public class SingleThreadDispatcher<I extends InputObject> extends AbstractDispatcher<I> {

    @Override
    public void dispatch(ProtocolParser<? extends ServletRequest, ? extends ServletResponse, I> parser, I input) {
        process(parser, input);
    }

    @Override
    public List<I> cancel() {
        throw Exceptions.forUnsupportedOperation("cancel operation of '%s' is unsupported", getClass().getName());
    }
}
