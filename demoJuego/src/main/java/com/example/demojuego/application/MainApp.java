
package com.example.demojuego.application;

import com.example.demojuego.vista.PantallaInicio;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        PantallaInicio inicio = new PantallaInicio();
        inicio.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

