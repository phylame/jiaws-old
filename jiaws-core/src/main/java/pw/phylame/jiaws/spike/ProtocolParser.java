package pw.phylame.jiaws.spike;

import java.io.IOException;
import java.net.Socket;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import pw.phylame.jiaws.util.Pair;

public interface ProtocolParser {
    <REQ extends ServletRequest, RES extends ServletResponse> Pair<REQ, RES> parse(Socket socket) throws IOException;
}
