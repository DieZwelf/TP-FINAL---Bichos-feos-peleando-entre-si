
package com.example.demojuego.vista;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;

public abstract class PantallaMenuBase  implements IPantalla {

    protected final GestorPantallas pantallas;

    // Estos elementos se comparten entre las pantallas del menú.
    private static StackPane root;
    private static VBox botonesBox;
    private static Scene escena;
    private static MediaPlayer reproductor;
    private static Stage stageCompartido;

    protected PantallaMenuBase(GestorPantallas pantallas) {
        this.pantallas = pantallas;
    }

    public static VBox getBotonesBox() {
        return botonesBox;
    }

    // --------------------------------------------------
    // CREAR EL MENÚ COMPARTIDO UNA SOLA VEZ
    // --------------------------------------------------

    protected Scene mostrarMenu(Stage stage) {

        // Si todavía no existe la escena, la creamos.
        if (escena == null) {
            crearMenuCompartido(stage);
        }

        // Animamos el cambio de botones, no el fondo.
        cambiarBotones();
        return escena;
    }

    private void crearMenuCompartido(Stage stage) {

        stageCompartido = stage;

        root = new StackPane();

        // 1. Crear el reproductor de video una sola vez.
        URL archivoVideo = Objects.requireNonNull(
                getClass().getResource("/videos/fondo.mp4"),
                "No se encontró videos/fondo.mp4"
        );

        Media media = new Media(archivoVideo.toExternalForm());
        reproductor = new MediaPlayer(media);

        reproductor.setCycleCount(MediaPlayer.INDEFINITE);

        MediaView vistaVideo = new MediaView(reproductor);
        vistaVideo.setPreserveRatio(false);
        vistaVideo.fitWidthProperty().bind(root.widthProperty());
        vistaVideo.fitHeightProperty().bind(root.heightProperty());

        reproductor.setOnReady(reproductor::play);

        // 2. Crear el contenido que estará sobre el video.
        AnchorPane contenido = new AnchorPane();

        VBox titulo = new VBox(0);
        titulo.setAlignment(Pos.TOP_LEFT);

        Label linea1 = new Label("BICHOSTROFE");
        Label linea2 = new Label("VENTURA");

        String estiloTitulo =
                "-fx-text-fill: #ffde00;" +
                        "-fx-font-family: 'Monospaced';" +
                        "-fx-font-size: 100px;" +
                        "-fx-font-weight: bold;";

        linea1.setStyle(estiloTitulo);
        linea2.setStyle(estiloTitulo);

        titulo.getChildren().addAll(linea1, linea2);

        AnchorPane.setTopAnchor(titulo, 35.0);
        AnchorPane.setLeftAnchor(titulo, 45.0);

        // 3. Crear el contenedor que cambiará entre pantallas.
        botonesBox = new VBox(37);
        botonesBox.setAlignment(Pos.TOP_LEFT);
        botonesBox.setPrefWidth(600);
        botonesBox.setTranslateX(920);
        botonesBox.setTranslateY(120);

        contenido.getChildren().addAll(titulo, botonesBox);

        // El video se agrega primero, detrás del contenido.
        root.getChildren().addAll(vistaVideo, contenido);

        // 4. Crear una única escena para todo el menú.
        escena = new Scene(root, 1400, 800);

        URL css = Objects.requireNonNull(
                getClass().getResource(
                        "/images/pantallaMenu/estilo.css"
                ),
                "No se encontró estilo.css"
        );

        escena.getStylesheets().add(css.toExternalForm());

        stage.setScene(escena);
        stage.show();
    }

    // --------------------------------------------------
    // TRANSICIÓN SUAVE ENTRE LOS BOTONES
    // --------------------------------------------------

    private void cambiarBotones() {

        // Si es la primera pantalla, el contenedor está vacío.
        if (botonesBox.getChildren().isEmpty()) {
            configurarBotones();
            botonesBox.setOpacity(1);
            return;
        }

        // Desvanecer los botones actuales.
        FadeTransition salida = new FadeTransition(
                Duration.millis(180),
                botonesBox
        );

        salida.setFromValue(1);
        salida.setToValue(0);

        salida.setOnFinished(evento -> {

            // Reemplazar los botones cuando ya desaparecieron.
            botonesBox.getChildren().clear();
            configurarBotones();

            // Hacer aparecer los nuevos botones.
            FadeTransition entrada = new FadeTransition(
                    Duration.millis(220),
                    botonesBox
            );

            entrada.setFromValue(0);
            entrada.setToValue(1);
            entrada.play();
        });

        salida.play();
    }

    // --------------------------------------------------
    // CREAR BOTONES CON ESTILO COMPARTIDO
    // --------------------------------------------------

    protected Button crearBotonEstilizado(String texto) {

        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-menu");

        boton.setOnMouseEntered(evento ->
                boton.setText("▶ " + texto)
        );

        boton.setOnMouseExited(evento ->
                boton.setText(texto)
        );

        return boton;
    }

    // Cada pantalla define sus propios botones.
    protected abstract void configurarBotones();

    // --------------------------------------------------
    // LIBERAR EL VIDEO AL SALIR DEL JUEGO
    // --------------------------------------------------

    public static void liberarReproductor() {

        if (reproductor != null) {
            reproductor.stop();
            reproductor.dispose();
            reproductor = null;
        }

        root = null;
        botonesBox = null;
        escena = null;
        stageCompartido = null;
    }
}
