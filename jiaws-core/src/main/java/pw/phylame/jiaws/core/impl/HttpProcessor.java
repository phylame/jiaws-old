package pw.phylame.jiaws.core.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

import lombok.val;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.SocketProcessor;
import pw.phylame.jiaws.servlet.impl.HttpServletRequestImpl;
import pw.phylame.jiaws.util.JiawsException;
import pw.phylame.jiaws.util.MultiValueMap;

public class HttpProcessor implements SocketProcessor {
    private WeakReference<Server> server;

    private boolean inHeader = false;

    private int maxHeaderCount;

    private int maxHeaderSize;

    private HttpServletRequestImpl requestImpl = new HttpServletRequestImpl();

    private MultiValueMap<String, String> headers = new MultiValueMap<>();

    private MultiValueMap<String, String> parameters = new MultiValueMap<>();

    @Override
    public void setServer(Server server) {
        this.server = new WeakReference<>(server);
        maxHeaderCount = server.getConfig().getMaxRequestHeaderCount();
        maxHeaderSize = server.getConfig().getMaxRequestHeaderSize();
    }

    @Override
    public void process(Socket socket) throws Exception {

        InputStream in = new BufferedInputStream(socket.getInputStream());
        StringBuilder b = new StringBuilder();
        // request line
        parseRequestLine(in, b);
        // read all header
        inHeader = true;
        while (!(line = readLine(in)).isEmpty()) {

        }
        inHeader = false;
    }

    private void parseRequestLine(InputStream in, StringBuilder b) throws IOException, JiawsException {
        b.setLength(0);
        int ch, order = 0;
        while ((ch = in.read()) != -1) {
            if (ch != ' ') {
                b.append((char) ch);
            } else {
                switch (order) {
                case 0:// method
                break;
                case 1:// request URL
                break;
                case 2:// http version
                break;
                }
                b.setLength(0);
                ++order;
            }
        }
    }

    private boolean isEOL(int ch, InputStream in) throws IOException {
        if (ch == '\r') {
            int next = in.read();
            if (next == '\n') {
                return true;
            }
        }
        return false;
    }

    public String readLine(InputStream in) throws IOException, JiawsException {
        StringBuilder b = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1) {
            if (ch == '\r') {
                int next = in.read();
                if (next == '\n') {
                    break;
                }
            }
            if (inHeader && b.length() > maxHeaderSize) {

            }
            b.append((char) ch);
        }
        return b.toString();
    }
}
