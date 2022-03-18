package less7.server.handler;

import less7.server.MyServer;
import less7.server.authentication.AuthenticationService;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + password
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/cMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/sMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/pMsg"; // + msg
    private static final String STOP_SERVER_CMD_PREFIX = "/stop";
    private static final String END_CLIENT_CMD_PREFIX = "/end";
    private MyServer myServer;
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public ClientHandler(MyServer myServer, Socket socket) {

        this.myServer = myServer;
        clientSocket = socket;
    }

    public void handle() throws IOException {
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMsg();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            if (clientSocket.isConnected()) {
                String msg = in.readUTF();
                if (msg.startsWith(AUTH_CMD_PREFIX)) {
                    boolean isSuccessAuth = processAuthentication(msg);
                    if (isSuccessAuth) {
                        break;
                    }
                }
                else {
                    out.writeUTF(AUTHERR_CMD_PREFIX + " Error authentication");
                    System.out.println("A bad try of the authentication");
                }
            }
            else continue;
        }

    }

    private boolean processAuthentication(String msg) throws IOException {
        String[] parts = msg.split("\\s+");
        if (parts.length != 3) {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Error of authentication");
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
                out.writeUTF(AUTHERR_CMD_PREFIX + " " + "Login is used already");
                System.out.println("Login is used already");
                auth.endAuthentication();
                return false;
            }

            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            myServer.subscribe(this);
            System.out.println("User " + username + " added to chat");
            auth.endAuthentication();

            return true;

        }
        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " " + "Wrong login or password");
            System.out.println("Wrong login or password");
            auth.endAuthentication();
            return false;
        }

    }

    private void readMsg() throws IOException {
        String cmdPrefix;
        while (true) {
            if (clientSocket.isConnected()) {
                String msg = in.readUTF();
                System.out.println("message | " + username + ": " + msg);
                if (msg.startsWith(STOP_SERVER_CMD_PREFIX)) {
                    System.exit(0);
                }
                else if (msg.startsWith(END_CLIENT_CMD_PREFIX)) {
                    return;
                }
                else if (msg.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                    String[] parts = msg.split("\\s+", 3);
                    cmdPrefix = parts[0];
                    String receiverUsername = parts[1];
                    myServer.privateMessage(cmdPrefix, receiverUsername, msg, this);
                }
                else {
                    cmdPrefix = CLIENT_MSG_CMD_PREFIX;
                    myServer.broadcastMessage(cmdPrefix, msg, this);
                }
            }
            else continue;
        }
    }


    public void sendMessage(String message) throws IOException {
        out.writeUTF(String.format("%s", message));
    }
    public void sendMessage(String cmdPrefix, String sender, String message) throws IOException {
        out.writeUTF(String.format("%s %s %s", cmdPrefix, sender, message));
    }

    public String getUsername() {
        return username;
    }
}
