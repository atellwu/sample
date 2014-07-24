package http;

import java.net.*;
import java.io.*;

public class Server {

    private ServerSocket   ss;
    private Socket         socket;
    private PrintWriter    out;

    public Server() {
        try {
            ss = new ServerSocket(6363); // �˿�

            while (true) {
                socket = ss.accept();
                out = new PrintWriter(socket.getOutputStream(), true);

                
                
                String CurLine = ""; // Line read from standard in

                System.out.println("����Ҫ���͵���Ϣ (type 'quit' to exit): ");
                InputStreamReader converter = new InputStreamReader(System.in);

                BufferedReader in = new BufferedReader(converter);

                while (!(CurLine.equals("quit"))) {
                    CurLine = in.readLine();

                    if (!(CurLine.equals("quit"))) {

                        System.out.println("�����͵���: " + CurLine);
                        try {
                            out.write(CurLine);
                            out.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.exit(0);
                    }
                }
                

                out.close();
                socket.close();
            }

        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
