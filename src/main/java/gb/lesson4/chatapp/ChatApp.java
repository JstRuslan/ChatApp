package gb.lesson4.chatapp;

import gb.lesson4.chatapp.controllers.AuthController;
import gb.lesson4.chatapp.controllers.ChatAppController;
import gb.lesson4.chatapp.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import less7.server.models.Command;

import java.io.IOException;

public class ChatApp extends Application {

    private Stage primaryStage;
    private Stage authStage;
    private Network network;
    private ChatAppController chatAppController;

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        network = new Network();
        network.connect();

        openAuthDialog();
        createChatWindows();


    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(ChatApp.class.getResource("Auth-view.fxml"));
        authStage = new Stage();
        Scene scene = new Scene(authLoader.load());
        authStage.setScene(scene);
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setTitle("Log in");
        authStage.initStyle(StageStyle.UNDECORATED);
        authStage.setResizable(false);
        authStage.show();

        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setChatApp(this);

    }

    private void createChatWindows() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MyChat");
        primaryStage.setResizable(false);
//        primaryStage.setAlwaysOnTop(true);


        chatAppController = fxmlLoader.getController();
        chatAppController.setNetwork(network);
        network.setChatApp(chatAppController);

    }

    public void showChatWindow() {
        authStage.close();
        primaryStage.show();
        network.waitMsg();
        chatAppController.setNickName(network.getUsername());

    }

    public void showAlert(String title, String errorMsg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMsg);
        alert.show();
    }

    @Override
    public void stop(){
        network.sendMsg(Command.END_CLIENT_CMD_PREFIX);
    }

    public static void main(String[] args) {
        launch();
    }
}