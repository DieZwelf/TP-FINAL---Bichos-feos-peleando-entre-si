package com.example.demojuego.vista;

import com.example.demojuego.vista.PantallaPartidaNueva;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.util.Objects;

public class PantallaInicio extends Application {

    @Override
    public void start(Stage stage) {

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

        VBox contenido = new VBox(22);
        contenido.setAlignment(Pos.CENTER);

        // Título
        Label titulo = new Label("BICHOS FEOS");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        titulo.setTextFill(Color.web("black"));
        titulo.setEffect(new DropShadow(4, Color.web("#2546a8")));

        Label subtitulo = new Label("¡TU AVENTURA COMIENZA!");
        subtitulo.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        subtitulo.setTextFill(Color.web("#244a65"));

        // Personaje dibujado con emojis


        // Botones del menú
        Button jugar = crearBoton("▶  NUEVA PARTIDA");
        Button continuar = crearBoton("▣  CONTINUAR");
        Button salir = crearBoton("✖  SALIR");

        jugar.setOnAction(e -> mostrarMensaje(
                root, "¡La aventura está por comenzar!"
        ));

        jugar.setOnAction(e -> {
            PantallaPartidaNueva nuevaPartida = new PantallaPartidaNueva();
            nuevaPartida.mostrar(stage);
        });

        continuar.setOnAction(e -> mostrarMensaje(
                root, "Todavía no hay partidas guardadas."
        ));

        salir.setOnAction(e -> stage.close());

        VBox menu = new VBox(12, jugar, continuar, salir);
        menu.setAlignment(Pos.CENTER);

        // Panel decorativo
        VBox panel = new VBox(18,
                titulo,
                subtitulo,
                menu
        );
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(540);
        panel.setMaxHeight(520);
        panel.setPadding(new javafx.geometry.Insets(30));



        // Decoración del paisaje
        Label nubes = new Label("☁                 ☁");
        nubes.setFont(Font.font(35));
        nubes.setTextFill(Color.WHITE);


        BorderPane escenario = new BorderPane();
        escenario.setTop(nubes);
        BorderPane.setAlignment(nubes, Pos.CENTER);
        escenario.setPadding(new javafx.geometry.Insets(15, 0, 12, 0));
        escenario.setMouseTransparent(true);

        root.getChildren().addAll(escenario, panel);

        Scene scene = new Scene(root, 900, 650);


        stage.setTitle("Bichos feos");
        Image icono = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/images/icono.png"),
                        "No se encontró icono.png en resources"
                )
        );

        stage.getIcons().add(icono);
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(550);
        stage.show();
    }

    private Button crearBoton(String texto) {
        Button boton = new Button(texto);

        boton.setPrefWidth(270);
        boton.setPrefHeight(45);
        boton.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));

        boton.setStyle(
                "-fx-background-color: #3979c3;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #244a85;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: #55a8e8;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-color: #244a85;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 8;" +
                                "-fx-cursor: hand;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: #3979c3;" +
                                "-fx-text-fill: white;" +
                                "-fx-background-radius: 8;" +
                                "-fx-border-color: #244a85;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 8;" +
                                "-fx-cursor: hand;"
                )
        );

        return boton;
    }

    private void mostrarMensaje(StackPane root, String mensaje) {
        Label aviso = new Label(mensaje);
        aviso.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        aviso.setTextFill(Color.WHITE);
        aviso.setStyle(
                "-fx-background-color: #244a65;" +
                        "-fx-padding: 20;" +
                        "-fx-background-radius: 10;"
        );

        StackPane.setAlignment(aviso, Pos.BOTTOM_CENTER);
        StackPane.setMargin(
                aviso, new javafx.geometry.Insets(0, 0, 25, 0)
        );

        root.getChildren().removeIf(
                nodo -> nodo instanceof Label
                        && "aviso".equals(nodo.getUserData())
        );

        aviso.setUserData("aviso");
        root.getChildren().add(aviso);
    }

    public static void main(String[] args) {
        launch(args);
    }
}