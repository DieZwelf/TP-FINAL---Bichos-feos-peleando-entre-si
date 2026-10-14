module com.example.demojuego {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    exports com.example.demojuego.application to javafx.graphics;
    opens com.example.demojuego.application to javafx.fxml;

    opens com.example.demojuego.model to javafx.fxml;
    exports com.example.demojuego.model;

    exports com.example.demojuego.demoJuego;
    opens com.example.demojuego.demoJuego to javafx.fxml;

    exports com.example.demojuego;
    opens com.example.demojuego to javafx.fxml;

    exports com.example.demojuego.vista;
    opens com.example.demojuego.vista to javafx.fxml;
}