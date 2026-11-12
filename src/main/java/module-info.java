module demojuego {
    requires javafx.controls;
    requires javafx.media;
    requires com.google.gson;
requires java.sql;

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
