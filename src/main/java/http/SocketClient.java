package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketClient {

    Socket socket;

    public SocketClient() {
        try {
            socket = new Socket("localhost", 6363);
            InputStream inputStream = socket.getInputStream();

            // 从Socket 中读出服务器端的返回
            int in;
            while ((in = inputStream.read()) != -1) {
                System.out.println(in);
            }
            System.out.println("== end ==");
            socket.close();
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        new SocketClient();
    }
}
