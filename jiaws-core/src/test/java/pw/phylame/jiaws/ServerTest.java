package pw.phylame.jiaws;

import java.net.InetSocketAddress;

import pw.phylame.jiaws.core.ConnectorConfig;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerConfig;
import pw.phylame.jiaws.core.impl.BIOConnector;
import pw.phylame.jiaws.core.impl.HttpProcessor;
import pw.phylame.jiaws.core.impl.SingleThreadDispatcher;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setMaxRequestHeaderCount(18);
        serverConfig.setMaxRequestHeaderSize(256);
        serverConfig.setAddress(new InetSocketAddress("localhost", 9999));
        ConnectorConfig connectorConfig = new ConnectorConfig();
        connectorConfig.setDispatcher(new SingleThreadDispatcher());
        connectorConfig.setProcessor(new HttpProcessor());
        serverConfig.setConnector(new BIOConnector(connectorConfig));
        Server server = new Server(serverConfig);
        server.start();
    }
}
