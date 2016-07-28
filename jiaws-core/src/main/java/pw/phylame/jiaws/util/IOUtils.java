package pw.phylame.jiaws.util;

import java.io.IOException;
import java.io.InputStream;

import lombok.NonNull;

public final class IOUtils {
    private IOUtils() {
    }

    public static final int DEFAULT_BUFFER_SIZE = 8190;

    public static String readLine(@NonNull InputStream in) throws IOException {
        StringBuilder b = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1) {
            b.append(ch);
            if (ch == '\n') {
                break;
            }
        }
        return b.toString();
    }
}
