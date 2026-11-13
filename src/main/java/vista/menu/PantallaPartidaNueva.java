
package vista.menu;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import vista.gestor.GestorPantallas;
import vista.base.PantallaMenuBase;

/**
 * La clase PANTALLAPARTIDANUEVA representa la pantalla desde la cual
 * el jugador puede comenzar una nueva partida o regresar al menú principal.
 * Hereda de PantallaMenuBase.
 * Tiene un constructor que recibe el gestor de pantallas.
 * Otros métodos: configurarBotones(): configura los componentes visuales
 * y las acciones de los botones de la pantalla.
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class PantallaPartidaNueva extends PantallaMenuBase {

    /**
     * Construye la pantalla de nueva partida.
     *
     * @param pantallas gestor encargado de la navegación entre pantallas
     */
    public PantallaPartidaNueva(GestorPantallas pantallas) {
        super(pantallas);
    }

    /**
     * Configura los botones y los componentes visuales de la pantalla.
     * Permite comenzar una nueva aventura o regresar al menú inicial.
     */
    @Override
    protected void configurarBotones() {

        ocultarEncabezado();

        VBox contenedor = getBotonesBox();
        contenedor.getStyleClass().add("contenedor-nueva-partida");
        contenedor.getChildren().clear();

        Label titulo = new Label("NUEVA AVENTURA");
        titulo.getStyleClass().add("titulo-nueva-partida");

        Label subtitulo = new Label(
                "UNA NUEVA HISTORIA COMIENZA..."
        );
        subtitulo.getStyleClass().add("subtitulo-nueva-partida");

        VBox encabezado = new VBox(10, titulo, subtitulo);
        encabezado.getStyleClass().add("encabezado-nueva-partida");

        Button comenzar = new Button("COMENZAR AVENTURA");
        comenzar.getStyleClass().add("boton-comenzar");

        comenzar.setOnAction(e ->
                pantallas.iniciarPartida()
        );

        Button volver = new Button("VOLVER");
        volver.getStyleClass().add("boton-volver-nueva");

        volver.setOnAction(e ->
                pantallas.mostrarInicio()
        );

        contenedor.getChildren().addAll(
                encabezado,
                comenzar,
                volver
        );
    }
}
