package pw.phylame.jiaws.util;

import lombok.Getter;

public class HttpException extends ProtocolException {
    private static final long serialVersionUID = -6375723027120244374L;

    public static final String NAME = "http";

    @Getter
    private int code;

    public HttpException(int code) {
        super(NAME);
        this.code = code;
    }

    public HttpException(String message, int code) {
        super(message, NAME);
        this.code = code;
    }
}
