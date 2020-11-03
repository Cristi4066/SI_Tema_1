package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class App{
    private static String K1 = "1234567890123456",
                          K2 = "0123456789846212",
                          K3 = "asd24f1348200263";

    public static final int PORT = 8100;
    public static BufferedReader in;
    public static PrintWriter out;


    public static void makingContact() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);

                System.out.println("Waiting for a client...");
                Socket socket = serverSocket.accept();
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                out = new PrintWriter(socket.getOutputStream());

        }catch (IOException e) {
            System.err.println("Ooops... " + e);
        } finally {
            serverSocket.close();
        }
    }

    public static void main( String[] args ) throws IOException {
        makingContact();
        while(true) {
            String request = in.readLine();
            String cipher = "";
            if (request.equals("CBC")) {
                cipher = AES.encrypt(K1, K3);
            } else if (request.equals("OFB")) {
                cipher = AES.encrypt(K2, K3);
            } else{
                break;
            }
            out.println(cipher);
            out.flush();
        }
    }
}
