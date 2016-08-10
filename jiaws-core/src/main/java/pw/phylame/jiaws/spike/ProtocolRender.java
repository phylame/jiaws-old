package pw.phylame.jiaws.spike;

import java.io.IOException;

import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.ProtocolException;

public interface ProtocolRender<R extends ServletResponse, O extends OutputObject> {
    void render(R response, O output) throws IOException, ProtocolException;
}
