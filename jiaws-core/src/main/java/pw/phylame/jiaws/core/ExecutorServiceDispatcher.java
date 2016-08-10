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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import pw.phylame.jiaws.spike.InputObject;
import pw.phylame.jiaws.spike.ProtocolParser;

@AllArgsConstructor
public class ExecutorServiceDispatcher<I extends InputObject> extends AbstractDispatcher<I> {
    @NonNull
    private final ExecutorService executor;

    @Override
    public void dispatch(ProtocolParser<? extends ServletRequest, ? extends ServletResponse, I> parser, I input) {
        executor.submit(new DispatcherTask(parser, input));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<I> cancel() {
        List<I> result = new ArrayList<>();
        for (Runnable r : executor.shutdownNow()) {
            result.add(((DispatcherTask) r).input);
        }
        return result;
    }

    @AllArgsConstructor
    private class DispatcherTask implements Runnable {
        private ProtocolParser<? extends ServletRequest, ? extends ServletResponse, I> parser;
        private I input;

        @Override
        public void run() {
            process(parser, input);
        }
    }
}
