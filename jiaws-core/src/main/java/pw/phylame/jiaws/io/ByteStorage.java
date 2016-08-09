/*
 * Copyright 2016 Peng Wan <phylame@163.com>
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

package pw.phylame.jiaws.io;

import pw.phylame.jiaws.util.Validator;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteStorage {
    private byte[] buf;

    private int count;

    public ByteStorage() {
        this(32);
    }

    public ByteStorage(int capacity) {
        buf = new byte[capacity];
    }

    public int capacity() {
        return buf.length;
    }

    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > 0)
            ensureCapacityInternal(minimumCapacity);
    }

    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        if (minimumCapacity - buf.length > 0)
            expandCapacity(minimumCapacity);
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = buf.length * 2 + 2;
        if (newCapacity - minimumCapacity < 0)
            newCapacity = minimumCapacity;
        if (newCapacity < 0) {
            if (minimumCapacity < 0) // overflow
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    public final int getLength() {
        return count;
    }

    public final void setLength(int newLength) {
        if (newLength < 0)
            throw new IndexOutOfBoundsException("Bytes index out of range: " + newLength);
        ensureCapacityInternal(newLength);
        if (count < newLength) {
            Arrays.fill(buf, count, newLength, (byte) 0);
        }
        count = newLength;
    }

    public final ByteStorage append(byte b) {
        ensureCapacityInternal(count + 1);
        buf[count++] = b;
        return this;
    }

    public final ByteStorage append(byte[] b) {
        return append(b, 0, b.length);
    }

    public final ByteStorage append(byte[] b, int off, int len) {
        Validator.notNull(b);
        if (len <= 0) {
            return this;
        }
        ensureCapacityInternal(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
        return this;
    }

    public String toString(String encoding) throws UnsupportedEncodingException {
        return new String(buf, 0, count, encoding);
    }
}
