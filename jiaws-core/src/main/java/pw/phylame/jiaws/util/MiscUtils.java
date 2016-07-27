package pw.phylame.jiaws.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import lombok.NonNull;

public final class MiscUtils {
    private MiscUtils() {
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> m) {
        return !isEmpty(m);
    }

    public static <E> E getFirst(@NonNull Iterable<E> iterable) {
        return getFirst(iterable.iterator());
    }

    public static <E> E getFirst(@NonNull Iterator<E> iterator) {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
