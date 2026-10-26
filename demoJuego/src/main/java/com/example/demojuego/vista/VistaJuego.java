package com.example.demojuego.vista;

import com.example.demojuego.model.Mapa;
import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class VistaJuego extends Pane implements IPantalla {

    private final Jugador jugador;
    private final VistaJugador vistaJugador;
    private final VistaMapa vistaMapa;
    private JuegoController controlador;
    private GestorPantallas pantallas;

    public VistaJuego(Mapa mapa, Jugador jugador) {
        this.jugador = jugador;
        this.vistaMapa = new VistaMapa(mapa);
        this.vistaJugador = new VistaJugador(jugador);

        getChildren().addAll(vistaMapa, vistaJugador);
    }

    public VistaJugador getVistaJugador() {
        return vistaJugador;
    }

    @Override
    public void mostrar(Stage stage)
    {
        Scene escena = new Scene(this,500,500);

        controlador = new JuegoController(jugador,vistaJugador,escena);
        controlador.iniciarGameLoop();

        pantallas.mostrarEscena(escena);
    }
}
