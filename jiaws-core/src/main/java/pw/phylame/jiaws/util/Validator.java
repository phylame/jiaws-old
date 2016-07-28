package pw.phylame.jiaws.util;

public final class Validator {
    private Validator() {
    }

    public static <T> T notNull(T o) {
        return notNull(o, "");
    }

    public static <T> T notNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }
}
