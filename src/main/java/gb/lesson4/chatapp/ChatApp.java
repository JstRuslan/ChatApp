package gb.lesson4.chatapp;

import gb.lesson4.chatapp.controllers.ChatAppController;
import gb.lesson4.chatapp.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("MyChat");
        stage.setResizable(false);
        stage.setScene(scene);

        Network network = new Network();
        ChatAppController chatAppController = fxmlLoader.getController();

        chatAppController.setNetwork(network);

        network.connect();
        network.waitMsg(chatAppController);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}