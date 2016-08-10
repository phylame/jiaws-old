package pw.phylame.jiaws.io;

import java.io.IOException;
import java.util.EventListener;

public interface ResponseWriteListener extends EventListener {
    void beforeWrite(ResponseWriteEvent e) throws IOException;

    void afterWrite(ResponseWriteEvent e) throws IOException;
}
