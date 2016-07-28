package pw.phylame.jiaws.util;

import lombok.NonNull;

public class LazyValue<T> {
    protected T value = null;

    private Provider<T> provider;

    public LazyValue(@NonNull Provider<T> provider) {
        this.provider = provider;
    }

    public T get() {
        if (value == null) {
            initValue();
        }
        return value;
    }

    private void initValue() {
        value = provider.provide();
    }
}
