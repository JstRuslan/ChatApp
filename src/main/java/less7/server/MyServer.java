package less7.server;

import less7.server.authentication.AuthenticationService;
import less7.server.authentication.BaseAuthentication;
import less7.server.handler.ClientHandler;
import less7.server.models.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;
    private Socket socket;

    public MyServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        authenticationService = new BaseAuthentication();
        clients = new ArrayList<>();
    }

    public void start() {
        System.out.println("SERVER IS STARTED!");
        System.out.println("<------------------>");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitAndProcessNewClientConnection() throws IOException {
        System.out.println("Wait a client");
        socket = serverSocket.accept();
        System.out.println("Client is connected");

        processClientConnection(socket);
    }

    private void processClientConnection(Socket socket) throws IOException {
        ClientHandler handler = new ClientHandler(this, socket);
        handler.handle();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        broadcastMessage(Command.SERVER_MSG_CMD_PREFIX, clientHandler, clientHandler.getUsername() + " connected to chart");
        broadcastMessage(Command.USERADD_CMD_PREFIX, clientHandler, clientHandler.getUsername());
        updateUserlist(clients, clientHandler);
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        broadcastMessage(Command.USERREM_CMD_PREFIX, clientHandler, clientHandler.getUsername());
        broadcastMessage(Command.SERVER_MSG_CMD_PREFIX, clientHandler, clientHandler.getUsername() + " disconnected from chat");
        clients.remove(clientHandler);
    }

    public synchronized boolean isUsernameBusy(String username) {
        if (clients.isEmpty()) {
            return false;
        }
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private void updateUserlist(List<ClientHandler> clients, ClientHandler clientHandler) {
        if (clients.size()==0){
            return;
        }
        ArrayList<String> userList = new ArrayList<>();
        for (ClientHandler s : clients) {
            userList.add(s.getUsername());
        }

        try {
            clientHandler.sendServerMessage(Command.USERLIST_CMD_PREFIX, "Update userslist on client from Server");
            clientHandler.sendUserlist(userList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String cmdPrefix, ClientHandler sender, String msg) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }

            if (cmdPrefix.equals(Command.SERVER_MSG_CMD_PREFIX)
            || cmdPrefix.equals(Command.USERADD_CMD_PREFIX)
            || cmdPrefix.equals(Command.USERREM_CMD_PREFIX)) {
                client.sendServerMessage(cmdPrefix, msg);
            }
            else {
                client.sendMessage(cmdPrefix, sender.getUsername(), msg);
            }
        }
    }

    public synchronized void privateMessage(String cmdPrefix, String receiverUsername, String msg, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(receiverUsername)) {
                client.sendMessage(cmdPrefix, sender.getUsername(), msg);
                return;
            }
        }
        sender.sendServerMessage(cmdPrefix, "User " + receiverUsername + " offline");
    }
}