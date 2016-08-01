/*
 * Copyright 2016 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package pw.phylame.jiaws;

import java.net.InetSocketAddress;

import pw.phylame.jiaws.core.ConnectorConfig;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.ServerConfig;
import pw.phylame.jiaws.core.impl.BIOConnector;
import pw.phylame.jiaws.core.impl.SingleThreadDispatcher;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setMaxRequestHeaderCount(18);
        serverConfig.setMaxRequestHeaderSize(256);
        serverConfig.setAddress(new InetSocketAddress("localhost", 9999));
        ConnectorConfig connectorConfig = new ConnectorConfig();
        connectorConfig.setDispatcher(new SingleThreadDispatcher());
        serverConfig.setConnector(new BIOConnector(connectorConfig));

        Server server = new Server(serverConfig);
        server.start();
    }
}
