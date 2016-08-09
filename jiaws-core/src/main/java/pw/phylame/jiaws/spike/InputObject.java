package pw.phylame.jiaws.spike;

import java.io.Closeable;

/**
 * Interface for indicating an object may be parsed by <code>ProtocolParser</code>.
 *
 */
public interface InputObject extends Closeable {
    boolean isClosed();
}
