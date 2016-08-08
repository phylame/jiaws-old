package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lombok.val;
import pw.phylame.jiaws.core.AbstractConnector;
import pw.phylame.jiaws.core.ConnectorConfig;

public class NIOConnector extends AbstractConnector {
    private ServerSocketChannel ssc;

    /**
     * Status for stop receiving client request.
     */
    private volatile boolean cancelled = false;

    protected NIOConnector(ConnectorConfig config) {
        super(config);
    }

    @Override
    protected void doStart() throws IOException {
        super.doStart();
        val selector = Selector.open();
        logger.info("{}@{} starting...", getClass().getSimpleName(), hashCode());
        ssc = ServerSocketChannel.open();
        logger.info("Binding address {}...", address);
        ssc.bind(address);
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        val dispatcher = config.getDispatcher();
        while (!cancelled && selector.select() > 0) {
            val i = selector.selectedKeys().iterator();
            while (i.hasNext()) {
                val key = i.next();
                i.remove();
                if (key.isAcceptable()) {
                    val sc = ((ServerSocketChannel) key.channel()).accept();
                    sc.configureBlocking(false);
                    sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(8192));
                } else if (key.isReadable()) {
                    recv((SocketChannel) key.channel(), (ByteBuffer) key.attachment());
                    key.interestOps(SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    send((SocketChannel) key.channel());
                }
            }
        }
    }

    @Override
    protected void doStop() throws IOException {
        cancelled = true;
        super.doStop();
        if (ssc != null && ssc.isOpen()) {
            ssc.close();
        }
    }

    private void recv(SocketChannel sc, ByteBuffer buffer) throws IOException {
        // todo: parse http and dispatch
    }

    private void send(SocketChannel sc) throws IOException {
        // todo: send http to client
        sc.close();
    }

}
