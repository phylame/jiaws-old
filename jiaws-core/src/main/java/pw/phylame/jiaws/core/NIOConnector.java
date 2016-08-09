package pw.phylame.jiaws.core;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lombok.val;
import pw.phylame.jiaws.io.IOUtils;
import pw.phylame.jiaws.spike.ChannelInput;

public class NIOConnector extends AbstractConnector<ChannelInput> {
    private Selector selector;

    private ServerSocketChannel ssc;

    /**
     * Status for stop receiving client request.
     */
    private volatile boolean cancelled = false;

    public NIOConnector(ConnectorConfig<ChannelInput> config) {
        super(config);
    }

    @Override
    protected void doStart() throws IOException {
        super.doStart();
        logger.info("{}@{} starting...", getClass().getSimpleName(), hashCode());

        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        ssc.bind(address);
        logger.info("Bound address {}...", address);
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        val dispatcher = config.getDispatcher();
        val parser = config.getParser();

        while (!cancelled && selector.select() > 0) {
            val i = selector.selectedKeys().iterator();
            while (i.hasNext()) {
                val key = i.next();
                i.remove();
                if (key.isAcceptable()) {
                    val sc = ((ServerSocketChannel) key.channel()).accept();
                    sc.configureBlocking(false);
                    sc.register(key.selector(), SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    dispatcher.dispatch(parser, new ChannelInput((SocketChannel) key.channel()));
                    // after dispatching, the socket channel will be closed
                }
            }
        }
    }

    @Override
    protected void doStop() throws IOException {
        cancelled = true;
        super.doStop();
        IOUtils.closeQuietly(ssc);
        ssc = null;
        IOUtils.closeQuietly(selector);
        selector = null;
    }
}
