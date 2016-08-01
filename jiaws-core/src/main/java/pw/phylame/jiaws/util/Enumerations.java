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

package pw.phylame.jiaws.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import lombok.NonNull;

public final class Enumerations {
    private Enumerations() {
    }

    public static <E> Enumeration<E> emptyEnumeration() {
        return new IteratorEnumeration<>(Collections.<E>emptyIterator());
    }

    public static <E> Enumeration<E> forIterator(@NonNull Iterator<E> iterator) {
        return new IteratorEnumeration<>(iterator);
    }

    public static <E> Enumeration<E> forCollection(@NonNull Collection<E> collection) {
        return new IteratorEnumeration<>(collection);
    }

    public static <E> Iterator<E> asIterator(Enumeration<E> e) {
        return new EnumerationIterator<>(e);
    }

    public static <E> Iterable<E> asIterable(Enumeration<E> e) {
        return new SimpleIterable<>(asIterator(e));
    }

    private static class IteratorEnumeration<E> implements Enumeration<E> {
        private final Iterator<E> iterator;

        private IteratorEnumeration(Collection<E> c) {
            this.iterator = c.iterator();
        }

        private IteratorEnumeration(Iterator<E> i) {
            this.iterator = i;
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public E nextElement() {
            return iterator.next();
        }
    }

    private static class EnumerationIterator<E> implements Iterator<E> {
        private final Enumeration<E> e;

        private EnumerationIterator(Enumeration<E> e) {
            this.e = e;
        }

        @Override
        public boolean hasNext() {
            return e.hasMoreElements();
        }

        @Override
        public E next() {
            return e.nextElement();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unsupported remove operation");
        }
    }

    private static class SimpleIterable<T> implements Iterable<T> {
        private final Iterator<T> i;

        private SimpleIterable(Iterator<T> i) {
            this.i = i;
        }

        @Override
        public Iterator<T> iterator() {
            return i;
        }

    }
}
