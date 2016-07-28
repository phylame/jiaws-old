package pw.phylame.jiaws.core;

import java.net.Socket;

import javax.servlet.http.HttpServletRequest;

public interface HttpParser {
    HttpServletRequest parse(Socket socket);
}
