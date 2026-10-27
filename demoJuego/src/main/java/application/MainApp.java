
package application;

import vista.GestorPantallas;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {


   @Override
    public void start(Stage stage) {

        GestorPantallas pantallas = new GestorPantallas(stage);


        pantallas.mostrarInicio();

    }


    public static void main(String[] args) {
        launch(args);
    }
}

