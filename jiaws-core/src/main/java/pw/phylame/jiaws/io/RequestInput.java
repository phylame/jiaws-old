package pw.phylame.jiaws.io;

import java.io.IOException;

public interface RequestInput {
    /**
     * Reads a byte.
     * 
     * @return the byte or {@literal -1} if no remaining data
     * @throws IOException
     */
    int read() throws IOException;

    /**
     * Reads some bytes until CR or LF occurring.
     * 
     * @return number of bytes read
     * @throws IOException
     */
    int readLine(byte[] b, int off, int len) throws IOException;
}
