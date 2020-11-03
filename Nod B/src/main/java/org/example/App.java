package org.example;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class App
{
    private static String K3 = "asd24f1348200263";
    private static int q;
    private static String k;
    private static String iV;

    private static final String  serverAddress = "127.0.0.1";
    private static final int PORT = 8101;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    private static void makingContact() throws IOException {
        try {
            socket = new Socket(serverAddress, PORT);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

    private static String type(String text){
        iV = text.substring(text.length()-32);
        k = text.substring(3, text.length()-33);
        return text.substring(0,3);
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
        //b1 = new BigInteger(s1.getBytes()).toString(2);
        b2 = new BigInteger(s2.getBytes()).toString(2);

        String xor = XORTest2(s1,b2);

        return new String(new BigInteger(xor, 2).toByteArray());
    }

    public static String Xor2(String s1, String s2){
        String b1,b2;
        b1 = new BigInteger(s1.getBytes()).toString(2);
        b2 = new BigInteger(s2.getBytes()).toString(2);

        String xor = XORTest2(b1,b2);

        return new String(new BigInteger(xor, 2).toByteArray());
    }


    public static void main( String[] args ) throws IOException {
        makingContact();
        FileWriter myWriter = new FileWriter("out.txt");
        String file = "C:\\Users\\cristi\\IdeaProjects\\SI Tema 1\\q.txt";
        q = Integer.parseInt(readingFromFile(file));
        while(true){

            String text = in.readLine();
            if(text.equals("_DONE_")) {myWriter.close(); return;}
            String type = type(text);
            k = AES.decrypt(k,K3);
            String previosData = "";
            for(int i = 0; i < q; i++) {
                text = in.readLine();
                if(text.equals("_DONE_")) {myWriter.close(); return;}
                if (type.equals("OFB")) {
                    if(previosData.isEmpty()){
                        previosData = AES.encrypt(iV, k);

                        myWriter.write(Xor(text, previosData));
                    }
                    else{
                        previosData = AES.encrypt(previosData, k);
                        myWriter.write(Xor(text, previosData));
                    }
                } else {
                    if(previosData.isEmpty()) {
                        String smt = AES.decrypt(text, k);
                        myWriter.write(Xor(smt, iV));
                        previosData = text;
                    }
                    else{
                        String smt = AES.decrypt(text, k);
                        myWriter.write(Xor(smt, previosData));
                        previosData = text;
                    }
                }

            }
        }
    }
}
