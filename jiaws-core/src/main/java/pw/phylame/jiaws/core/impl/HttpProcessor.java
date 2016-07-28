package pw.phylame.jiaws.core.impl;

import java.io.BufferedInputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

import javax.servlet.ServletInputStream;

import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.SocketProcessor;
import pw.phylame.jiaws.servlet.impl.ServletInputStreamImpl;

public class HttpProcessor implements SocketProcessor {
    private WeakReference<Server> server;

    @Override
    public void setServer(Server server) {
        this.server = new WeakReference<>(server);
    }

    @Override
    public void process(Socket socket) throws Exception {
        ServletInputStream sis = new ServletInputStreamImpl(new BufferedInputStream(socket.getInputStream()));
        byte[] buf = new byte[256];
        int num;
        String line;
        while ((num = sis.readLine(buf, 0, buf.length)) != -1) {
            line = new String(buf, 0, num - 2); // no \r and \n
            System.out.println(line);
        }
    }
}
