package pw.phylame.jiaws.util;

import lombok.Getter;

public class BadHttpException extends JiawsException {
    private static final long serialVersionUID = -6375723027120244374L;

    @Getter
    private int code;

    public BadHttpException(int code, String message) {
        super(message);
        this.code = code;
    }
}
