
package vista.gestor;

import javafx.scene.image.Image; //Cargar imágenes
import javafx.stage.Stage; //Administrar ventanas de JavaFX
import java.net.URL; // Representar ubicaciones de recursos

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

        if (icono != null) { // Comprobar que icono no sea null
            stage.getIcons().setAll(
                    new Image(icono.toExternalForm()) //Convierte la referencia del recurso icono en una dirección
                                                      // que JavaFX puede utilizar para cargar la imagen
            );
        }

        stage.setWidth(1400);
        stage.setHeight(800); // Tamaño de la ventana
        stage.setResizable(true); // Este método permite que el usuario pueda modificar las dimensiones de la ventana del juego.
                                  // Al pasarle true, habilitamos esa posibilidad
    }

    /**
     * Alterna el modo de pantalla completa.
     */
    public void alternarPantallaCompleta() {
        stage.setFullScreen(!stage.isFullScreen()); //Este método alterna el estado de pantalla completa de la ventana.
        // Utiliza isFullScreen() para consultar el estado actual y el operador ! para invertirlo. Después, setFullScreen() aplica el nuevo estado
    }

    /**
     * Cierra la ventana principal.
     */
    public void cerrar() {
        stage.close(); //Cierra la ventana
    }
}
