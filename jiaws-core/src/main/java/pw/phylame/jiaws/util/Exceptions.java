package pw.phylame.jiaws.util;

/**
 * Utility for exceptions.
 */
public final class Exceptions extends pw.phylame.ycl.util.Exceptions {
    private Exceptions() {
    }

    public static ProtocolException forProtocol(String protocol, String format, Object... args) {
        return new ProtocolException(String.format(format, args), protocol);
    }

    public static HttpException forHttp(String format, Object... args) {
        return new HttpException(String.format(format, args));
    }
}
