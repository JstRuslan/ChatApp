package gb.lesson4.chatapp.models;

import gb.lesson4.chatapp.controllers.ChatAppController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8186;
    private DataInputStream in;
    private DataOutputStream out;

    private final String host;
    private final int port;
    private Socket mySocket;


    public Network() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        Thread tConnect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(host, port);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());

                    mySocket = getSocket(socket);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Connection is absent");
                }
            }
        }, "Client connect");
        tConnect.setDaemon(true);
        tConnect.start();
    }


    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Message doesn`t send!");
        }
    }


    public void waitMsg(ChatAppController chatAppController) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (mySocket == null) {
                            continue;
                        }
                        String msg = in.readUTF();
                        chatAppController.appendMsg(msg);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Wait Msg");
        t.setDaemon(true);
        t.start();

    }

    private Socket getSocket(Socket socket) {
        return socket;
    }
}