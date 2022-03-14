module gb.lesson4.chatapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens gb.lesson4.chatapp to javafx.fxml;
    exports gb.lesson4.chatapp;
    exports gb.lesson4.chatapp.controllers;
    opens gb.lesson4.chatapp.controllers to javafx.fxml;
}