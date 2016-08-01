package pw.phylame.jiaws.spike;

import java.io.IOException;
import java.net.Socket;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.jiaws.util.values.Pair;

public interface ProtocolParser {
    <REQ extends ServletRequest, RES extends ServletResponse> Pair<REQ, RES> parse(Socket socket)
            throws IOException, ProtocolException;
}
