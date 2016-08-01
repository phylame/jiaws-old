package pw.phylame.jiaws.io;

import java.util.EventListener;

public interface ResponseWriteListener extends EventListener {
    void beforeWrite(ResponseWriteEvent e);

    void afetWrite(ResponseWriteEvent e);
}
