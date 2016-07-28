package pw.phylame.jiaws;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import pw.phylame.jiaws.core.ConnectorConfig;
import pw.phylame.jiaws.core.RequestDispatcher;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerConfig;
import pw.phylame.jiaws.core.SocketProcessor;
import pw.phylame.jiaws.core.impl.BIOConnector;
import pw.phylame.jiaws.core.impl.HttpProcessor;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setMaxRequestHeaderCount(12);
        serverConfig.setMaxRequestHeaderSize(10);
        serverConfig.setAddress(new InetSocketAddress("localhost", 9999));
        ConnectorConfig connectorConfig = new ConnectorConfig();
        SimpleDispatcher dispatcher = new SimpleDispatcher();
        dispatcher.processor = new HttpProcessor();
        connectorConfig.setDispatcher(dispatcher);
        serverConfig.setConnector(new BIOConnector(connectorConfig));
        Server server = new Server(serverConfig);
        server.start();
    }

    private static class SimpleDispatcher implements RequestDispatcher {
        private int count = 0;
        private SocketProcessor processor;

        @Override
        public void setServer(Server server) {
            // TODO Auto-generated method stub

        }

        @Override
        public void dispatch(Socket socket) {
            System.out.printf("current: %d request\n", ++count);
            // TODO Auto-generated method stub
            try {
                processor.process(socket);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        public List<Socket> cancel() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
