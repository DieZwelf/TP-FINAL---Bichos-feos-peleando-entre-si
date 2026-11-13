
package vista.menu;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import vista.GestorPantallas;
import vista.base.PantallaMenuBase;

public class PantallaPartidaNueva extends PantallaMenuBase {

    public PantallaPartidaNueva(GestorPantallas pantallas) {
        super(pantallas);
    }

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
