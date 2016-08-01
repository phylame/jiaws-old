package pw.phylame.jiaws.util.values;

import lombok.Getter;

public abstract class ValueHolder<T> {
    protected T value;

    public T get() {
        return value;
    }
}
