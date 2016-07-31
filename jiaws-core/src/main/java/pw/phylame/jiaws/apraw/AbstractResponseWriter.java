/*
 * Copyright 2014-2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.phylame.jiaws.apraw;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public abstract class AbstractResponseWriter implements ResponseWriter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int MIN_BUFFER_SIZE = 1024;

    public static final int DEFAULT_BUFFER_SIZE = 8192;

    protected WeakReference<ServletResponse> responseRef;

    private ByteBuffer buffer = null;

    @Getter
    private boolean written = false;

    protected AbstractResponseWriter() {
        this(DEFAULT_BUFFER_SIZE);
    }

    protected AbstractResponseWriter(int bufferSize) {
        buffer = ByteBuffer.allocate(bufferSize);
    }

    @Override
    public void setResponse(ServletResponse response) {
        responseRef = new WeakReference<>(response);
    }

    @Override
    public int getBufferSize() {
        return buffer.capacity();
    }

    @Override
    public final void setBufferSize(int capacity) throws IllegalStateException {
        if (isWritten()) {
            throw new IllegalStateException("Cannot change buffer size after data has been written");
        }
        if (capacity < MIN_BUFFER_SIZE) {
            logger.info("Specified new capacity({}) < MIN_BUFFER_SIZE({})", capacity, MIN_BUFFER_SIZE);
            return;
        }
        buffer = ByteBuffer.allocate(capacity);
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return isWritten();
    }

    @Override
    public void flush() throws IOException {
        written = true;
    }
}
