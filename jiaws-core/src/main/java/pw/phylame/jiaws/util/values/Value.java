package pw.phylame.jiaws.util.values;

public abstract class Value<T> {
    protected T value;

    public T get() {
        return value;
    }
}
