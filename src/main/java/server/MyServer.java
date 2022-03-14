package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class MyServer {
    public static final int SERVER_PORT = 8186;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Socket myClientSocket;

    public static void main(String[] args) {


        Thread tConnect = new Thread(() -> {

            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);) {

                while (true) {
                    System.out.println("Wait connect...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connect");

                    in = new DataInputStream(clientSocket.getInputStream());
                    out = new DataOutputStream(clientSocket.getOutputStream());

                    myClientSocket = getClientSocket(clientSocket);


                    try {
                        while (true) {
                            String  msg = in.readUTF();

                            if (msg.equals("/server-stop")) {
                                System.out.println("Server is STOP!");
                                System.exit(0);
                            }

                            System.out.println("Клиент: " + msg);
//                            out.writeUTF(msg.toUpperCase());

                        }
                    }
                    catch (IOException e) {
                        clientSocket.close();
                        System.out.println("Client disconnected");
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();

            }
        }, "Thread Connection");


        Thread tSendMsg = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                try {
                    while (true) {
                        String msg = sc.nextLine().trim();
                        if (msg.isEmpty()) {
                            continue;
                        }
                        if(myClientSocket.isClosed()) {
                            System.out.println("Client disconnected! Wait...");
                            continue;
                        } else out.writeUTF("Server: " + msg);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Message not send!");
                }
            }
        }, "Thread answer from server");

        tConnect.start();
        tSendMsg.start();

        try {
            tConnect.join();
            tSendMsg.join();

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Конец программы");
    }

    private static Socket getClientSocket(Socket socket) {
        return socket;
    }
}

