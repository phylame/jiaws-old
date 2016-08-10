package pw.phylame.jiaws.io;

import java.io.IOException;
import java.io.OutputStream;

import lombok.NonNull;

public interface ContentSource {
    String getType();

    String getEncoding();

    long getLength();

    void writeTo(@NonNull OutputStream out) throws IOException;
}
