package gb.lesson4.chatapp.controllers;


import gb.lesson4.chatapp.models.Network;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import less7.server.models.Command;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatAppController {
    String strDate = new SimpleDateFormat("hh:mm:ss").format(new Date());
    private Network network;


    @FXML
    private TextArea fieldChat;

    @FXML
    private TextField fieldMsg;


    @FXML
    private ListView<String> listViewUsers;

    @FXML
    private Label labelNickname;

    private String selectedRecipient;


    public void initialize() {

        listViewUsers.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = listViewUsers.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                listViewUsers.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    }
                    else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    public synchronized void addUserFromList(String username) {
        listViewUsers.getItems().add(username);
    }
    public synchronized void removeUserFromList(String username) {
        listViewUsers.getItems().remove(username);
    }

    public synchronized void updateUserList(ArrayList<String> userList) {
        listViewUsers.getItems().addAll(userList);
    }

    @FXML
    void closeApp() {
        System.exit(0);
    }

    @FXML
    void aboutInfo(ActionEvent event) throws IOException {
        Stage infoStage = new Stage();
        Parent root = FXMLLoader.load(ChatAppController.class.getResource("About-view.fxml"));
        infoStage.setScene(new Scene(root, 250, 100));
        infoStage.setTitle("About");
        infoStage.initModality(Modality.APPLICATION_MODAL);
//        infoStage.initOwner(((Node)event.getSource()).getScene().getWindow());
        infoStage.show();

    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    void sendMsg() {
        String msg = fieldMsg.getText().trim();
        fieldMsg.clear();
        if (msg.isEmpty()) {
            return;
        }
        if (selectedRecipient != null) {
            network.sendMsg(Command.PRIVATE_MSG_CMD_PREFIX + " " + selectedRecipient + " " + msg);
        }
        else {
            network.sendMsg(msg);
        }
        appendMsg(msg);

    }

    @FXML
    void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sendMsg();
        }
    }

    @FXML
    void clearFieldChat() {
        fieldChat.clear();
    }

    public synchronized void appendMsg(String msg) {
        fieldChat.appendText(">" + strDate + "<");
        fieldChat.appendText(System.lineSeparator());
        fieldChat.appendText(msg);
        fieldChat.appendText(System.lineSeparator());
    }

    public void setNickName(String username) {
        labelNickname.setText(username);
    }

}