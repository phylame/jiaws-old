package pw.phylame.jiaws.spike.http;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import pw.phylame.ycl.util.MultiMap;

public abstract class HttpObject {
    @Getter
    @Setter
    private String protocol;

    @Getter
    @Setter
    private String method;

    @Getter
    private final MultiMap<String, String> headers = new MultiMap<>();

    public final String getHeader(String name) {
        return headers.getOne(name);
    }

    public final Collection<String> getHeaders(String name) {
        return headers.get(name);
    }

    public final Collection<String> getHeaderNames() {
        return headers.keySet();
    }
}
