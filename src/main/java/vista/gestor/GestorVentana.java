
package vista.gestor;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Administra las propiedades y acciones de la ventana principal.
 */
public class GestorVentana {

    private final Stage stage;

    public GestorVentana(Stage stage) {
        this.stage = stage;
    }

    /**
     * Configura el título, icono y dimensiones iniciales.
     */
    public void configurarVentana() {

        stage.setTitle("Bichos feos");

        URL icono = getClass().getResource(
                "/images/pantallaMenu/icono.png"
        );

        if (icono != null) {
            stage.getIcons().setAll(
                    new Image(icono.toExternalForm())
            );
        }

        stage.setWidth(1400);
        stage.setHeight(800);
        stage.setResizable(true);
    }

    /**
     * Alterna el modo de pantalla completa.
     */
    public void alternarPantallaCompleta() {
        stage.setFullScreen(!stage.isFullScreen());
    }

    /**
     * Cierra la ventana principal.
     */
    public void cerrar() {
        stage.close();
    }
}
