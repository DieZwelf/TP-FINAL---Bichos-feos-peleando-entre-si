module demojuego {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;

    exports application to javafx.graphics;
    opens application to javafx.fxml;

    exports model;
    opens model to javafx.fxml;

    exports vista;
    exports vista.base;
    exports vista.menu;
    exports vista.juego;
    exports vista.combate;
}
