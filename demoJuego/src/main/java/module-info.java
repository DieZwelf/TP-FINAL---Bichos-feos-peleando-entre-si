module com.example.demojuego {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    exports com.example.demojuego.application to javafx.graphics;
    opens com.example.demojuego.application to javafx.fxml;

    opens com.example.demojuego.model to javafx.fxml;
    exports com.example.demojuego.model;

    exports com.example.demojuego.vista;
    opens com.example.demojuego.vista to javafx.fxml;
}