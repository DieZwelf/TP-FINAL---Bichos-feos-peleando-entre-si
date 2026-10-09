package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Metodo start para que no tire error");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
