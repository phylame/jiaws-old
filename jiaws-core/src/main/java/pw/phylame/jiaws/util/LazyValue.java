package pw.phylame.jiaws.util;

import lombok.NonNull;
import lombok.Synchronized;

public class LazyValue<T> {
    private boolean inited = false;

    protected T value = null;

    private Provider<T> provider;

    private T fallback;

    public LazyValue(@NonNull Provider<T> provider) {
        this(provider, null);
    }

    public LazyValue(@NonNull Provider<T> provider, T fallback) {
        this.provider = provider;
        this.fallback = fallback;
    }

    @Synchronized
    public T get() {
        if (!inited) {
            initValue();
        }
        return value;
    }

    private void initValue() {
        try {
            value = provider.provide();
        } catch (Exception e) {
            value = fallback;
        }
    }
}
