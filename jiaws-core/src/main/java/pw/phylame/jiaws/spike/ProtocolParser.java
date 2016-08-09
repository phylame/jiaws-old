package pw.phylame.jiaws.spike;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.jiaws.util.values.Pair;

public interface ProtocolParser<Q extends ServletRequest, S extends ServletResponse, I extends InputObject> {
    Pair<Q, S> parse(I input) throws IOException, ProtocolException;
}
