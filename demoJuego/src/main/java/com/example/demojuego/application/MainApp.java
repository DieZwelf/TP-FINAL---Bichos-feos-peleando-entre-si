
package com.example.demojuego.application;

import com.example.demojuego.vista.PantallaInicio;
import com.example.demojuego.vista.VistaJugador;
import javafx.application.Application;
import javafx.stage.Stage;

import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import com.example.demojuego.vista.VistaJuego;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bichos");
        stage.setFullScreen(true);

        PantallaInicio pantallaInicio = new PantallaInicio();


        pantallaInicio.start(stage);

    }

    public static void main(String[] args) {
        launch(args);
    }
}

