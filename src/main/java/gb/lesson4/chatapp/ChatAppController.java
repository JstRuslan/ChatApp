package gb.lesson4.chatapp;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatAppController {
    String strDate = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
    private final ObservableList<String> listUser = FXCollections.observableArrayList("Oleg", "Marfa", "TommyLee", "Nina");

    @FXML
    private Button btnSend;

    @FXML
    private Button clearChat;

    @FXML
    private TextArea fieldChat;

    @FXML
    private TextField fieldMsg;

    @FXML
    private ListView<String> listViewUsers;

    public void setListViewUsers() {
        listViewUsers.getItems().addAll(listUser);
    }

    @FXML
    private MenuItem menuItemClose;

    @FXML
    void closeApp(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void aboutInfo(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(ChatAppController.class.getResource("About-view.fxml"));
        stage.setScene(new Scene(root, 250, 100));
        stage.setTitle("About");
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initOwner(((Node)event.getSource()).getScene().getWindow());
        stage.show();

    }


    @FXML
    void sendMsg(ActionEvent event) throws IOException {
        String msg = fieldMsg.getText().trim();
        if (msg.length() != 0) {
            fieldChat.appendText(">" + strDate + "\n" + msg + "\n");
            fieldMsg.clear();
        }

    }

    @FXML
    void pressEnter(KeyEvent event) {
        String msg = fieldMsg.getText().trim();

        if ((event.getCode() == KeyCode.ENTER) && (msg.length() != 0)) {
            fieldChat.appendText(">>>" + strDate + "\n" + msg + "\n");
            fieldMsg.clear();
        }
    }

    @FXML
    void clearFiledChat(ActionEvent event) {
        fieldChat.clear();
    }

}