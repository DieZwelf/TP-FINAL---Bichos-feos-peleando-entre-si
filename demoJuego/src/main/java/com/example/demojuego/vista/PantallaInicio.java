
package com.example.demojuego.vista;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PantallaInicio extends PantallaMenuBase {

    public PantallaInicio(GestorPantallas pantallas) {
        super(pantallas);
    }

    // Mostrar el menú inicial.
    @Override
    public void mostrar(Stage stage) {
        stage.setTitle("Pokémon Ventura");
        mostrarMenu(stage);
    }

    // Botones propios del inicio.
    @Override
    protected void configurarBotones() {

        Button btnNuevaPartida =
                crearBotonEstilizado("NUEVA PARTIDA");

        Button btnCargarPartida =
                crearBotonEstilizado("CARGAR PARTIDA");

        Button btnSalir =
                crearBotonEstilizado("SALIR");

        btnNuevaPartida.setPrefSize(600, 100);
        btnCargarPartida.setPrefSize(600, 100);
        btnSalir.setPrefSize(300, 100);

        btnNuevaPartida.setOnAction(evento ->
                pantallas.mostrarNuevaPartida()
        );

        btnCargarPartida.setOnAction(evento ->
                pantallas.mostrarCargarPartida()
        );

        btnSalir.setOnAction(evento ->
                pantallas.salir()
        );

        getBotonesBox().getChildren().addAll(
                btnNuevaPartida,
                btnCargarPartida,
                btnSalir
        );
    }
}
