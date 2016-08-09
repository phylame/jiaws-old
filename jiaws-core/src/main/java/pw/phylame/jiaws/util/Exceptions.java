package pw.phylame.jiaws.util;

/**
 * Utility for exceptions.
 */
public final class Exceptions {
    private Exceptions() {
    }

    public static RuntimeException forRuntime(String format, Object... args) {
        return new RuntimeException(String.format(format, args));
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

    public static UnsupportedOperationException forUnsupportedOperation(String format, Object... args) {
        return new UnsupportedOperationException(String.format(format, args));
    }

    public static ProtocolException forProtocol(String protocol, String format, Object... args) {
        return new ProtocolException(String.format(format, args), protocol);
    }

    public static HttpException forHttp(String format, Object... args) {
        return new HttpException(String.format(format, args));
    }
}
