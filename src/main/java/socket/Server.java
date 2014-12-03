package socket;

import java.net.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class Server {

    private ServerSocket   ss;
    private Socket         socket;
    private PrintWriter    out;

    public Server() throws InterruptedException {
        try {
            ss = new ServerSocket(6363,2); 
//            ss.
            
            System.out.println("---------------");
            while (true) {
                socket = ss.accept();
                out = new PrintWriter(socket.getOutputStream(), true);

                
                
                String CurLine = ""; // Line read from standard in

                System.out.println("a client connected: " + socket.getRemoteSocketAddress());
//                InputStreamReader converter = new InputStreamReader(System.in);
//
//                BufferedReader in = new BufferedReader(converter);
//
//                while (!(CurLine.equals("quit"))) {
//                    CurLine = in.readLine();
//
//                    if (!(CurLine.equals("quit"))) {
//
//                        System.out.println("line: " + CurLine);
//                        try {
//                            out.write(CurLine);
//                            out.flush();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        System.exit(0);
//                    }
//                }
                

//                out.close();
//                socket.close();
            }

        } catch (IOException e) {
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Server();
    }
}
