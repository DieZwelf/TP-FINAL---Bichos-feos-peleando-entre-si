package vista.menu;

import model.TipoJugador;
import vista.GestorPantallas;
import vista.base.PantallaMenuBase;

import javafx.scene.control.Button;

public class PantallaPartidaNueva extends PantallaMenuBase {

    public PantallaPartidaNueva(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected void configurarBotones() {
        Button comenzar = crearBotonEstilizado("COMENZAR AVENTURA");
        Button volver = crearBotonEstilizado("VOLVER");

        comenzar.setPrefSize(600, 100);
        volver.setPrefSize(300, 100);
        comenzar.setOnAction(e -> pantallas.iniciarPartida());
        volver.setOnAction(e -> pantallas.mostrarInicio());

        getBotonesBox().getChildren().addAll(comenzar, volver);
    }
}
