package pw.phylame.jiaws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lombok.val;
import pw.phylame.jiaws.core.Assembly;
import pw.phylame.jiaws.io.TextContentSource;
import pw.phylame.jiaws.spike.http.HttpResponse;

public class NIOTest {

    public static void main(String[] args) throws Exception {
        try (val selector = Selector.open()) {
            val ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress("localhost", 9001));
            ssc.configureBlocking(false);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.select() > 0) {
                val i = selector.selectedKeys().iterator();
                while (i.hasNext()) {
                    val key = i.next();
                    i.remove();
                    if (key.isAcceptable()) {
                        val sc = ((ServerSocketChannel) key.channel()).accept();
                        sc.configureBlocking(false);
                        sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(24));
                    } else if (key.isReadable()) {
                        recv((SocketChannel) key.channel(), (ByteBuffer) key.attachment());
                        key.interestOps(SelectionKey.OP_WRITE);
                    } else if (key.isWritable()) {
                        send((SocketChannel) key.channel());
                    } else {
                        // System.out.println(key.readyOps());
                    }
                }
            }
        }
    }

    private static void recv(SocketChannel sc, ByteBuffer buffer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size;
        byte[] bytes;
        while ((size = sc.read(buffer)) > 0) {
            buffer.flip();
            bytes = new byte[size];
            buffer.get(bytes);
            baos.write(bytes);
            buffer.clear();
        }
        System.out.println("recv:");
        System.out.println(new String(baos.toByteArray()));
    }

    public static final String CRLF = "\r\n";

    private static void send(SocketChannel sc) throws IOException {
        val response = new HttpResponse();
        response.setServerAssembly(new Assembly());
        String text = "<html><head><title>Test</title></head><body><h2>Hello, Jiaws</h2></body></html>";
        response.setContent(new TextContentSource(text, "UTF-8", "text/html"));
        response.renderTo(sc.socket().getOutputStream());
        sc.close();
    }

    private static void printKey(SelectionKey key) {
        System.out.println("attach: " + ((key.attachment() != null) ? "yes" : "no"));
        System.out.println("readable: " + key.isReadable());
        System.out.println("acceptable: " + key.isAcceptable());
        System.out.println("connectable: " + key.isConnectable());
        System.out.println("valid: " + key.isValid());
        System.out.println("ops: " + key.interestOps());
    }

}
