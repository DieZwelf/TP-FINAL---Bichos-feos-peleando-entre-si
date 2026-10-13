package com.example.demojuego;

import com.example.demojuego.model.Mapa;
import com.example.demojuego.vista.MapaView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        Mapa mapa = new Mapa(50, 80);
        MapaView mapaView = new MapaView(mapa);

        ScrollPane scrollPane = new ScrollPane(mapaView);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);

        Scene scene = new Scene(scrollPane, 1000, 700);

        stage.setTitle("Mapa de prueba");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}