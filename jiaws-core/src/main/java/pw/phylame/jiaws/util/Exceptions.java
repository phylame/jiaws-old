package pw.phylame.jiaws.util;

/**
 * Utility for exceptions.
 */
public final class Exceptions {
    private Exceptions() {
    }

    public static NullPointerException forNullPointer(String format, Object... args) {
        return new NullPointerException(String.format(format, args));
    }

    public static IllegalArgumentException forIllegalArgument(String format, Object... args) {
        return new IllegalArgumentException(String.format(format, args));
    }

    public static IllegalStateException forIllegalState(String format, Object... args) {
        return new IllegalStateException(String.format(format, args));
    }
}
