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

    private  VistaJugador vistaJugador;
    private  MapaView vistaMapa;

    public VistaJuego (Mapa mapa, Jugador jugador){
        vistaMapa = new MapaView(mapa);
        vistaJugador = new VistaJugador(jugador);
        vistaJugador.inicializar(jugador);

        getChildren().addAll(vistaMapa,vistaJugador);
    }

    public VistaJuego (Mapa mapa){
        this.vistaJugador = new VistaJugador();
        this.vistaMapa = new MapaView(mapa);

        getChildren().addAll(vistaMapa,vistaJugador);
    }

    public VistaJuego() {
        this.vistaJugador = new VistaJugador();
        this.vistaMapa = new MapaView();

        getChildren().addAll(vistaMapa,vistaJugador);
    }

    public VistaJugador getVistaJugador() {
        return vistaJugador;
    }

    public void mostrar(Stage stage)
    {
        Scene escena = new Scene(this,500,500);
        stage.setScene(escena);
        stage.setFullScreen(true);
        stage.show();
    }
}
