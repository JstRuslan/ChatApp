package gb.lesson4.chatapp.controllers;

import gb.lesson4.chatapp.ChatApp;
import gb.lesson4.chatapp.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {

    @FXML
    private TextField fieldLogin;

    @FXML
    private PasswordField fieldPassword;

    private Network network;

    private ChatApp chatApp;


    @FXML
    void closeAuthWindow(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void checkAuth() {
        String login = fieldLogin.getText().trim();
        String password = fieldPassword.getText().trim();

        if (login.length() == 0 || password.length() == 0) {
            chatApp.showAlert("Ошибка ввода при аутентификации", "Поля не должны быть пустыми!");
            fieldLogin.clear();
            fieldPassword.clear();
            return;
        }

        String authErrorMsg = network.sendAuthMsg(login, password);

        if(authErrorMsg == null){
            chatApp.showChatWindow();
        } else {
            chatApp.showAlert("Ошибка аутентификации", authErrorMsg);
            fieldLogin.clear();
            fieldPassword.clear();
        }

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setChatApp(ChatApp chatApp) {
        this.chatApp = chatApp;
    }

}
