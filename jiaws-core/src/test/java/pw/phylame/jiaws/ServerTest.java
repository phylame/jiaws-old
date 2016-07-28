package pw.phylame.jiaws;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import pw.phylame.jiaws.core.ConnectorConfig;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerConfig;
import pw.phylame.jiaws.core.impl.BIOConnector;
import pw.phylame.jiaws.core.impl.HttpProcessor;
import pw.phylame.jiaws.core.impl.ThreadPoolDispatcher;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setAddress(new InetSocketAddress("localhost", 9999));
        ConnectorConfig connectorConfig = new ConnectorConfig();
        connectorConfig
                .setDispatcher(new ThreadPoolDispatcher(Executors.newFixedThreadPool(4), new HttpProcessor()));
        serverConfig.setConnector(new BIOConnector(connectorConfig));
        Server server = new Server(serverConfig);
        server.start();
    }
}
