
package vista.menu;

import vista.gestor.GestorPantallas;
import vista.base.PantallaMenuBase;

import javafx.scene.control.Button;

/**
 * La clase PANTALLAINICIO representa el menú principal del juego.
 * Hereda de PantallaMenuBase.
 * Tiene un constructor que recibe el gestor de pantallas.
 * Otros métodos: configurarBotones(): crea y configura los botones
 * para iniciar una partida, cargar una partida o salir del juego.
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class PantallaInicio extends PantallaMenuBase {

    /**
     * Construye la pantalla del menú principal.
     *
     * @param pantallas gestor encargado de la navegación entre pantallas
     */
    public PantallaInicio(GestorPantallas pantallas) {
        super(pantallas);
    }

    /**
     * Configura los botones del menú principal y sus respectivas acciones.
     * Permite iniciar una partida nueva, cargar una existente o salir.
     */
    @Override
    protected void configurarBotones() {
        Button nuevaPartida = crearBotonEstilizado("NUEVA PARTIDA");
        Button cargarPartida = crearBotonEstilizado("CARGAR PARTIDA");
        Button salir = crearBotonEstilizado("SALIR");

        nuevaPartida.setPrefSize(600, 100);
        cargarPartida.setPrefSize(600, 100);
        salir.setPrefSize(300, 100);

        nuevaPartida.setOnAction(e -> pantallas.mostrarNuevaPartida());
        cargarPartida.setOnAction(e -> pantallas.mostrarCargarPartida());
        salir.setOnAction(e -> pantallas.salir());

        getBotonesBox().getChildren().addAll(nuevaPartida, cargarPartida, salir);
    }
}
