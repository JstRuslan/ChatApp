package gb.lesson4.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("MyChat");
        stage.setResizable(false);
        stage.setScene(scene);

        ChatAppController myChatAppController = fxmlLoader.getController();
        myChatAppController.setListViewUsers();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}