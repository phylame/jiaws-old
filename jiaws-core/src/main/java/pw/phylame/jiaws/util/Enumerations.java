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

    private static class IteratorEnumeration<E> implements Enumeration<E> {
        private final Iterator<E> iterator;

        public IteratorEnumeration(Collection<E> c) {
            this.iterator = c.iterator();
        }

        public IteratorEnumeration(Iterator<E> i) {
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
}
