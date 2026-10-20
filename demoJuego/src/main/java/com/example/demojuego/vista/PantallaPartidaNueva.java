package com.example.demojuego.vista;

import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
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

public class PantallaPartidaNueva implements iPantalla{

    public void iniciarPartida(Stage stage) {
        Mapa mapa = new Mapa(10, 10);
        Jugador jugador = new Jugador(0, 0, 3);

        VistaJuego vistaJuego = new VistaJuego(mapa, jugador);
        vistaJuego.mostrar(stage);
    }

    public static void mostrar(Stage stage) {

        // Fondo del menú
        StackPane root = new StackPane();

        Image imagenFondo = new Image(
                PantallaInicio.class
                        .getResource("/images/pantallaMenu/fondo.jpg")
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

        /**
         * ===================================
         */

        Button llamarVistaJuego = new Button("Volver al menú");
        llamarVistaJuego.setOnAction(e -> {
            new VistaJuego().mostrar(stage);
        });

        VBox contenido = new VBox(
                25,
                titulo,
                mensaje,
                llamarVistaJuego
        );

        contenido.setAlignment(Pos.CENTER);



        Scene escena = new Scene(contenido, 900, 650);
        stage.setTitle("Monster Quest - Nueva partida");
        stage.setFullScreen(true);
        stage.setScene(escena);
    }
}