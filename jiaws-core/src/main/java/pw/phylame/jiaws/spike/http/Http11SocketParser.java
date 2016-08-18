package pw.phylame.jiaws.spike.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.val;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerAware;
import pw.phylame.jiaws.io.ResponseOutputStream;
import pw.phylame.jiaws.servlet.JiawsHttpRequest;
import pw.phylame.jiaws.servlet.http.JiawsHttpResponse;
import pw.phylame.jiaws.spike.ProtocolParser;
import pw.phylame.jiaws.spike.SocketInput;
import pw.phylame.jiaws.util.ProtocolException;
import pw.phylame.jiaws.util.values.Pair;

public class Http11SocketParser
        implements ProtocolParser<JiawsHttpRequest, JiawsHttpResponse, SocketInput>, ServerAware {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WeakReference<Server> serverRef;

    private int maxHeaderCount;

    private int maxHeaderSize;

    @Override
    public void setServer(Server server) {
        serverRef = new WeakReference<>(server);
        maxHeaderCount = server.getConfig().getMaxRequestHeaderCount();
        maxHeaderSize = server.getConfig().getMaxRequestHeaderSize();
    }

    @Override
    public Pair<JiawsHttpRequest, JiawsHttpResponse> parse(SocketInput input) throws IOException, ProtocolException {
        val socket = input.getSocket();
        InputStream in = new BufferedInputStream(socket.getInputStream());
        StringBuilder b = new StringBuilder();
        val request = new JiawsHttpRequest();
        val hr = HttpRequest.from(in);
        val response = new JiawsHttpResponse(new ResponseOutputStream(socket.getOutputStream()));
        response.setSocket(socket);
        return new Pair<JiawsHttpRequest, JiawsHttpResponse>(request, response);
    }
}
