package pw.phylame.jiaws.util;

import lombok.Getter;
import lombok.NonNull;

/**
 * Exception for request with bad protocol.
 *
 */
public class ProtocolException extends JiawsException {
    private static final long serialVersionUID = 646686496222492271L;

    /**
     * The name of protocol.
     */
    @Getter
    @NonNull
    private String protocol;

    public ProtocolException(String protocol) {
        super();
        this.protocol = protocol;
    }

    public ProtocolException(String message, String protocol) {
        super(message);
        this.protocol = protocol;
    }

}
