module demojuego {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires com.google.gson;

    exports application to javafx.graphics;
    opens application to javafx.fxml;

    opens model to javafx.fxml;
    exports model;

    exports vista;
    opens vista to javafx.fxml;
}