package pw.phylame.jiaws.io;

import java.util.EventObject;

public class ResponseWriteEvent extends EventObject {
    private static final long serialVersionUID = 1L;

    public ResponseWriteEvent(Object source) {
        super(source);
    }
}
