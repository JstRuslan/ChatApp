package less7.server;

import less7.server.authentication.AuthenticationService;
import less7.server.authentication.BaseAuthentication;
import less7.server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthenticationService authenticationService;
    private final List<ClientHandler> clients;

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
        Socket socket = serverSocket.accept();
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

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) {
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

    public synchronized void broadcastMessage(String cmdPrefix, String msg, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(cmdPrefix, sender.getUsername(), msg);
        }
    }

    public synchronized void privateMessage(String cmdPrefix, String receiverUsername, String msg, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(receiverUsername)) {
                client.sendMessage(cmdPrefix, sender.getUsername(), msg);
                return;
            }
        }
        sender.sendMessage("User "+ receiverUsername + " offline" );
    }
}