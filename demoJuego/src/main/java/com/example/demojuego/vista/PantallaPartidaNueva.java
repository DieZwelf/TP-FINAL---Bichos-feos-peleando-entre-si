package com.example.demojuego.vista;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PantallaPartidaNueva {

    public static void mostrar(Stage stage) {

        // Fondo del menú
        StackPane root = new StackPane();

        Image imagenFondo = new Image(
                PantallaInicio.class
                        .getResource("/images/fondo.jpg")
                        .toExternalForm()
        );

        ImageView fondo = new ImageView(imagenFondo);
        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());

// El fondo queda detrás de los demás elementos
        root.getChildren().add(fondo);


        Label titulo = new Label("¡NUEVA PARTIDA!");
        titulo.setFont(Font.font("Monospaced", 30));

        Label mensaje = new Label(
                "¡Tu aventura comienza ahora!"
        );
        mensaje.setFont(Font.font("Monospaced", 16));

        Button volver = new Button("Volver al menú");


        VBox contenido = new VBox(
                25,
                titulo,
                mensaje,
                volver
        );

        contenido.setAlignment(Pos.CENTER);



        Scene escena = new Scene(contenido, 900, 650);

        stage.setTitle("Monster Quest - Nueva partida");
        stage.setScene(escena);
    }
}