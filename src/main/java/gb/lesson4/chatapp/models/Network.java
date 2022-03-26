package gb.lesson4.chatapp.models;

import gb.lesson4.chatapp.controllers.ChatAppController;
import javafx.application.Platform;
import less7.server.models.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Network {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8186;
    private DataInputStream in;
    private DataOutputStream out;

    private final String host;
    private final int port;
    private Socket socket;
    private ChatAppController chatAppController;

    private String username;


    public Network() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setChatApp(ChatAppController chatAppController) {
        this.chatAppController = chatAppController;
    }


    public void connect() {
        Thread tConnect = new Thread(() -> {
            try {
                socket = new Socket(host, port);
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Connection is absent");
            }
        });
        tConnect.setDaemon(true);
        tConnect.start();
    }


    public String sendAuthMsg(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", Command.AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();
            if (response.startsWith(Command.AUTHOK_CMD_PREFIX)) {
                this.username = response.split("\\s+", 2)[1];
                return null;
            }
            else {
                return response.split("\\s+", 2)[1];
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
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

    public void waitMsg() {
        Thread tWaitMsg = new Thread(() -> {
            try {
                while (true) {
                    if (socket == null) {
                        continue;
                    }
                    String msg = in.readUTF();
                    if (msg.startsWith(Command.USERADD_CMD_PREFIX)) {
                        Platform.runLater(() -> {
                            chatAppController.addUserFromList(msg.split("\\s+", 3)[1]);
                        });
                        continue;
                    }
                    else if (msg.startsWith(Command.USERREM_CMD_PREFIX)) {
                        Platform.runLater(() -> {
                            chatAppController.removeUserFromList(msg.split("\\s+", 3)[1]);

                        });
                        continue;
                    }
                    else if (msg.startsWith(Command.USERLIST_CMD_PREFIX)) {
                        ArrayList<String> connectedUser = receiveUserListFormServer();
                        Platform.runLater(() -> {
                            chatAppController.updateUserList(connectedUser);

                        });
                        continue;
                    }
                    Platform.runLater(() -> {
                        chatAppController.appendMsg(msg);

                    });
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        tWaitMsg.setDaemon(true);
        tWaitMsg.start();
    }

    private ArrayList<String> receiveUserListFormServer() {
        ArrayList<String> list = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            list = (ArrayList<String>)ois.readObject();
            return list;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getUsername() {
        return username;
    }

}
