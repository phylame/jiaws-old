package pw.phylame.jiaws.util;

public class MutableLazyValue<T> extends LazyValue<T> {
    public MutableLazyValue(Provider<T> provider) {
        super(provider);
    }

    public void set(T value) {
        this.value = value;
    }
}
