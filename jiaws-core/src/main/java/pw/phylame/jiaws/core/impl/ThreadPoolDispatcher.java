package pw.phylame.jiaws.core.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import lombok.NonNull;
import pw.phylame.jiaws.core.RequestDispatcher;
import pw.phylame.jiaws.core.Server;
import pw.phylame.jiaws.core.SocketProcessor;

public class ThreadPoolDispatcher implements RequestDispatcher {
    private final ExecutorService threadPool;

    private final SocketProcessor socketProcessor;

    protected WeakReference<Server> server;

    public ThreadPoolDispatcher(@NonNull ExecutorService executorService, @NonNull SocketProcessor socketProcessor) {
        this.threadPool = executorService;
        this.socketProcessor = socketProcessor;
    }

    @Override
    public void setServer(@NonNull Server server) {
        this.server = new WeakReference<>(server);
        socketProcessor.setServer(server);
    }

    @Override
    public void dispatch(@NonNull Socket socket) {
        System.out.println("receiving socket: " + socket);
        threadPool.submit(new SocketHandle(socket));
    }

    @Override
    public List<Socket> cancel() {
        List<Socket> results = new ArrayList<>();
        for (Runnable r : threadPool.shutdownNow()) {
            results.add(((SocketHandle) r).socket);
        }
        return results;
    }

    private class SocketHandle implements Runnable {
        private final Socket socket;

        private SocketHandle(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socketProcessor.process(socket);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }

}
