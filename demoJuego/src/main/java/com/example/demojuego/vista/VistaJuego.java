package com.example.demojuego.vista;

import com.example.demojuego.model.Mapa;
import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

public class VistaJuego extends Pane implements iPantalla{

    private final Jugador jugador;
    private final VistaJugador vistaJugador;
    private final MapaView vistaMapa;
    private JuegoController controlador;

    public VistaJuego(Mapa mapa, Jugador jugador) {
        this.jugador = jugador;
        this.vistaMapa = new MapaView(mapa);
        this.vistaJugador = new VistaJugador(jugador);

        getChildren().addAll(vistaMapa, vistaJugador);
    }

    public VistaJugador getVistaJugador() {
        return vistaJugador;
    }

    public void mostrar(Stage stage)
    {
        Scene escena = new Scene(this,500,500);

        controlador = new JuegoController(jugador,vistaJugador,escena);
        controlador.iniciarGameLoop();

        stage.setScene(escena);
        stage.setFullScreen(true);
    }
}
