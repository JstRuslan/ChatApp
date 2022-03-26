package less7.server.handler;

import less7.server.MyServer;
import less7.server.authentication.AuthenticationService;
import less7.server.models.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler {

    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;


    public ClientHandler(MyServer myServer, Socket socket) {

        this.myServer = myServer;
        this.socket = socket;
    }

    public void handle() throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMsg();
            }
            catch (IOException e) {
                e.printStackTrace();
                try {
                    myServer.unSubscribe(this);
                    socket.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            if (socket.isConnected()) {
                String msg = in.readUTF();
                if (msg.startsWith(Command.AUTH_CMD_PREFIX)) {
                    boolean isSuccessAuth = processAuthentication(msg);
                    if (isSuccessAuth) {
                        break;
                    }
                }
                else {
                    out.writeUTF(Command.AUTHERR_CMD_PREFIX + " Error authentication");
                    System.out.println("A bad try of the authentication");
                }
            }
            else continue;
        }

    }

    private boolean processAuthentication(String msg) throws IOException {
        String[] parts = msg.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(Command.AUTHERR_CMD_PREFIX + " Error of authentication");
            System.out.println("Error of authentication");
            return false;
        }
        String login = parts[1];
        String password = parts[2];

        AuthenticationService auth = myServer.getAuthenticationService();

        auth.startAuthentication();

        username = auth.getUsernameByLoginAndPassword(login, password);

        if (username != null) {

            if (myServer.isUsernameBusy(username)) {
                out.writeUTF(Command.AUTHERR_CMD_PREFIX + " " + "Login is used already");
                System.out.println("Login is used already");
                auth.endAuthentication();
                return false;
            }

            out.writeUTF(Command.AUTHOK_CMD_PREFIX + " " + username);
            myServer.subscribe(this);
            System.out.println("User " + username + " added to chat");
            auth.endAuthentication();

            return true;

        }
        else {
            out.writeUTF(Command.AUTHERR_CMD_PREFIX + " " + "Wrong login or password");
            System.out.println("Wrong login or password");
            auth.endAuthentication();
            return false;
        }

    }

    private synchronized void readMsg() throws IOException {
        String cmdPrefix;
        while (true) {
            if (socket.isConnected()) {
                String msg = in.readUTF();
                System.out.println("message | " + username + ": " + msg);
                if (msg.startsWith(Command.STOP_SERVER_CMD_PREFIX)) {
                    System.exit(0);
                }
                else if (msg.startsWith(Command.END_CLIENT_CMD_PREFIX)) {
                    myServer.unSubscribe(this);
                    return;
                }
                else if (msg.startsWith(Command.PRIVATE_MSG_CMD_PREFIX)) {
                    String[] parts = msg.split("\\s+", 3);
                    cmdPrefix = parts[0];
                    String receiverUsername = parts[1];
                    String message = parts[2];
                    myServer.privateMessage(cmdPrefix, receiverUsername, message, this);
                }
                else {
                    cmdPrefix = Command.CLIENT_MSG_CMD_PREFIX;
                    myServer.broadcastMessage(cmdPrefix, this, msg);
                }
            }
            else continue;
        }
    }


    public void sendServerMessage(String cmdPrefix, String message) throws IOException {
        out.writeUTF(String.format("%s: %s",cmdPrefix, message));
    }
    public void sendMessage(String cmdPrefix, String sender, String message) throws IOException {
        out.writeUTF(String.format("%s <%s> %s", cmdPrefix, sender, message));
    }

    public void sendUserlist(ArrayList<String> userList) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(userList);
    }

    public String getUsername() {
        return username;
    }
}

