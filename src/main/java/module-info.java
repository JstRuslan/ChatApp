module gb.lesson4.chatapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens gb.lesson4.chatapp to javafx.fxml;
    exports gb.lesson4.chatapp;
}