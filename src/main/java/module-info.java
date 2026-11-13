/**
 * Define la configuración modular del proyecto
 *
 * Declara los módulos externos necesarios, como JavaFX, Gson y SQL,
 * y especifica qué paquetes del proyecto se exportan para que puedan
 * ser utilizados por otros módulos y cuáles se habilitan para reflexión.
 */
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
    exports vista.inventario;
    exports vista.equipo;
    exports vista.juego.mapa;
    exports vista.juego.animaciones;
    exports vista.gestor;

}
