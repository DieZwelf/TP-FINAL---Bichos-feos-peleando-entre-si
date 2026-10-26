
package com.example.demojuego.application;

import com.example.demojuego.vista.CargadorMapaTiled;
import com.example.demojuego.vista.GestorPantallas;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {


   @Override
    public void start(Stage stage) {
        stage.setTitle("Bichos");
        stage.setFullScreen(true);

        GestorPantallas pantallas = new GestorPantallas(stage);


        pantallas.mostrarInicio();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

