package pw.phylame.jiaws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import lombok.val;

public class Client {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Task()).start();
        }
    }

    static class Task implements Runnable {
        static volatile int count = 0;

        @Override
        public void run() {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 9001));
                val p = new PrintStream(socket.getOutputStream());
                p.println("qwerty");
                p.println();
                p.println("hello world");
                p.flush();

                val r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = r.readLine()) != null) {
                    System.out.println(line);
                }
                ++count;
                System.out.println(count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
