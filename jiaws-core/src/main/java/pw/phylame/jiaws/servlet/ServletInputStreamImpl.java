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

package pw.phylame.jiaws.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

import lombok.NonNull;
import pw.phylame.jiaws.util.ImplementUtils;

public class ServletInputStreamImpl extends ServletInputStream {
    private InputStream in;

    public ServletInputStreamImpl(@NonNull InputStream in) {
        this.in = in;
    }

    @Override
    public boolean isFinished() {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public boolean isReady() {
        return ImplementUtils.raiseForImpl();
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        ImplementUtils.raiseForImpl();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int hashCode() {
        return in.hashCode();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return in.read(b);
    }

    @Override
    public boolean equals(Object obj) {
        return in.equals(obj);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public String toString() {
        return in.toString();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
}
