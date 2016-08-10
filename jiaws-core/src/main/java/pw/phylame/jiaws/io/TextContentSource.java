package pw.phylame.jiaws.io;

import java.io.IOException;
import java.io.OutputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class TextContentSource implements ContentSource {
    @NonNull
    private final String text;

    @NonNull
    private final String charset;

    @NonNull
    private final String mime;

    @Override
    public String getType() {
        return mime + "; charset=" + charset;
    }

    @Override
    public long getLength() {
        return text.length();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(text.getBytes(charset));
    }

    @Override
    public String getEncoding() {
        return null;
    }

}
