
package vista;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PantallaPartidaNueva extends PantallaMenuBase {

    public PantallaPartidaNueva(GestorPantallas pantallas) {
        super(pantallas);
    }

    // Mostrar la pantalla de nueva partida.
    @Override
    public void mostrar(Stage stage) {
        mostrarMenu(stage);
    }

    // Botones propios de esta pantalla.
    @Override
    protected void configurarBotones() {

        Button btnComenzar =
                crearBotonEstilizado("COMENZAR AVENTURA");

        Button btnVolver =
                crearBotonEstilizado("VOLVER");

        btnComenzar.setPrefSize(600, 100);
        btnVolver.setPrefSize(300, 100);

        // Iniciar la partida.
        btnComenzar.setOnAction(evento ->
                pantallas.iniciarPartida()
        );

        // Volver al menú inicial.
        btnVolver.setOnAction(evento ->
                pantallas.mostrarInicio()
        );

        // Agregar los botones al contenedor compartido.
        getBotonesBox().getChildren().addAll(
                btnComenzar,
                btnVolver
        );
    }
}
