module com.example.demojuego {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demojuego to javafx.fxml;
    exports com.example.demojuego;

    opens application to javafx.fxml;
    exports application;

    opens model to javafx.fxml;
    exports model;


}