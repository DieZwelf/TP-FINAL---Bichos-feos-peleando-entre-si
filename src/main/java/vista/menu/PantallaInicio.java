package vista.menu;

import vista.GestorPantallas;
import vista.base.PantallaMenuBase;

import javafx.scene.control.Button;

public class PantallaInicio extends PantallaMenuBase {

    public PantallaInicio(GestorPantallas pantallas) {
        super(pantallas);
    }

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
