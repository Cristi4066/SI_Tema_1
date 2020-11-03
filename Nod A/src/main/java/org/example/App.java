package org.example;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.math.BigInteger;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.lang.Object;


public class App{
    private static String K3 = "asd24f1348200263";
    private static int q;

    private static final String  serverAddress = "127.0.0.1";
    private static final int PORTK = 8100;
    private static Socket socket;
    private static PrintWriter outK;
    private static BufferedReader inK;
    private static Random rand = new Random();

    public static final int PORTB = 8101;
    public static BufferedReader inB;
    public static PrintWriter outB;

    public static void makingContactB() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORTB);

            System.out.println("Waiting for a client...");
            Socket socket = serverSocket.accept();
            inB = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            outB = new PrintWriter(socket.getOutputStream());

        }catch (IOException e) {
            System.err.println("Ooops... " + e);
        } finally {
            serverSocket.close();
        }
    }

    private static void makingContactK() throws IOException {
        try {
            socket = new Socket(serverAddress, PORTK);
            outK = new PrintWriter(socket.getOutputStream());
            inK = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("No server listening... " + e);
        }
    }

    private static String readingFromFile(String path){
        String data = "";
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            data = myReader.nextLine();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }

    private static String initV(){
        String iV = "";
        for(int i = 0; i < 32; i++){
            int x = rand.nextInt(16);
            if(x < 10)
                iV += x;
            else{
                iV += (char)('f' - (x - 10));
            }
        }
        return iV;
    }

    public static String XORTest2(String str1, String str2){

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str1.length(); i++) {
                sb.append(str1.charAt(i)^str2.charAt(i));
            }
            return sb.toString();

    }

    public static String Xor(String s1, String s2){
        String b1,b2;
        b1 = new BigInteger(s1.getBytes()).toString(2);
        b2 = new BigInteger(s2.getBytes()).toString(2);

        return XORTest2(b1,b2);


    }

    private static String readingRow(int row){
        String data = "";
        try {
            File myObj = new File("C:\\Users\\cristi\\IdeaProjects\\SI Tema 1\\Nod A\\in.txt");
            Scanner myReader = new Scanner(myObj);
            for(int i = 0; i < row; i++)
                if(myReader.hasNextLine())
                    data = myReader.nextLine();
                else return "";
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return data;
    }


    public static void main(String[] args) throws IOException {
        String file = "C:\\Users\\cristi\\IdeaProjects\\SI Tema 1\\q.txt";
        q = Integer.parseInt(readingFromFile(file));
        String text = "";
        makingContactK();
        makingContactB();
        Boolean stop = false;
        int row = 1;

        System.out.println("Conected");
        while(true) {
            //  K1 sau K2
            int keyNum = rand.nextInt(2);
            String type;
            if (keyNum == 0) {
                type = "CBC";

            } else {
                type = "OFB";
            }
            //cererea chei
            outK.println(type);
            outK.flush();
            String key = inK.readLine();

            String iV = initV();

            // transmiterea lui B tip, cheie si vectorul initial
            outB.println(type + key + iV);
            outB.flush();
            key = AES.decrypt(key, K3);


            //criptarea textului
            String previousData = "";
            for(int i = 0; i < q; i++) {
                while(text.length() < 16){
                    if(readingRow(row).isEmpty()) {
                        for (int j = 0; j < 16 - text.length(); j++)
                            text += ' ';
                        stop = true;
                    }
                    else {
                        if(!text.isEmpty())
                            text = text + '\n';
                        text += readingRow(row);
                        row ++;
                    }
                }
                String bloc = text.substring(0,16);
                text = text.substring(16);
                if (keyNum == 1) {

                    if(previousData.isEmpty()) {
                        String smt = AES.encrypt(iV, key);
                        previousData = smt;

                        outB.println(Xor(bloc, smt));
                        outB.flush();
                    }
                    else{
                        String smt = AES.encrypt(previousData, key);
                        previousData = smt;

                        outB.println(Xor(bloc, smt));
                        outB.flush();
                    }
                } else {
                    if(previousData.isEmpty()) {
                        previousData = AES.encrypt(Xor(bloc, iV), key);
                    }
                    else{
                        previousData = AES.encrypt(Xor(bloc,previousData),key);
                    }
                    outB.println(previousData);
                    outB.flush();

                }
                if(stop) {
                    outB.println("_DONE_");
                    outB.flush();
                    return;
                }
            }
        }
    }
}
