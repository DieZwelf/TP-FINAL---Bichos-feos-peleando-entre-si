module com.example.demojuego {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demojuego to javafx.fxml;
    exports com.example.demojuego;
}