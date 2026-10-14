
        package com.example.demojuego.vista;
// Importaciones de JavaFX para la ventana, los controles y el diseño.
import com.example.demojuego.vista.PantallaPartidaNueva;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class PantallaInicio extends Application {

    @Override
    public void start(Stage stage) {

        // ==================================================
        // 1. CONTENEDOR PRINCIPAL
        // ==================================================
        // StackPane permite colocar el video detrás del menú.
        StackPane root = new StackPane();

        // ==================================================
        // 2. VIDEO DE FONDO
        // ==================================================
        // Busca el video dentro de src/main/resources/videos/.
        URL archivoVideo = getClass().getResource("/videos/fondo.mp4");

        // Si no encuentra el archivo, muestra un error claro.
        if (archivoVideo == null) {
            throw new IllegalStateException(
                    "No se encontró videos/fondo.mp4 en resources"
            );
        }

        // Crea el reproductor y configura el video para repetirse.
        Media media = new Media(archivoVideo.toExternalForm());
        MediaPlayer reproductor = new MediaPlayer(media);
        reproductor.setCycleCount(MediaPlayer.INDEFINITE);

        // MediaView muestra el video en pantalla.
        MediaView fondoAnimado = new MediaView(reproductor);

        // Estira el video para cubrir toda la ventana.
        fondoAnimado.setPreserveRatio(false);
        fondoAnimado.fitWidthProperty().bind(root.widthProperty());
        fondoAnimado.fitHeightProperty().bind(root.heightProperty());

        // Se agrega primero para que quede detrás de los demás elementos.
        root.getChildren().add(fondoAnimado);

        // Reproduce el video cuando está listo.
        reproductor.setOnReady(reproductor::play);

        // Informa errores de reproducción en la consola.
        reproductor.setOnError(() ->
                System.out.println(
                        "Error al reproducir el video: "
                                + reproductor.getError()
                )
        );

        // Libera los recursos del video al cerrar la ventana.
        stage.setOnHidden(e -> reproductor.dispose());

        // ==================================================
        // 3. DISEÑO GENERAL DEL MENÚ
        // ==================================================
        // BorderPane permite separar el título de las opciones.
        BorderPane contentLayout = new BorderPane();

        // Márgenes internos: arriba, derecha, abajo e izquierda.
        contentLayout.setPadding(new Insets(35, 45, 35, 45));

        // ==================================================
        // 4. TÍTULO ARRIBA A LA IZQUIERDA
        // ==================================================
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.TOP_LEFT);

        // Primera línea del título.
        Text tituloPrincipal = new Text("POKÉMON");
        tituloPrincipal.setFill(Color.web("#ffde00"));
        tituloPrincipal.setStroke(Color.web("#3b4cca"));
        tituloPrincipal.setStrokeWidth(2.5);
        tituloPrincipal.setFont(Font.font("System", 48));

        // Segunda línea del título.
        Text segundaLinea = new Text("VENTURA");
        segundaLinea.setFill(Color.web("#ffde00"));
        segundaLinea.setStroke(Color.web("#3b4cca"));
        segundaLinea.setStrokeWidth(2.5);
        segundaLinea.setFont(Font.font("System", 56));

        // Texto pequeño debajo del título.
        Text subtitulo = new Text("UNA AVENTURA COMIENZA...");
        subtitulo.setFill(Color.WHITE);
        subtitulo.setFont(Font.font("Monospaced", 13));

        // Agrega los tres textos al contenedor del título.
        titleBox.getChildren().addAll(
                tituloPrincipal,
                segundaLinea,
                subtitulo
        );

        // Coloca el título en la parte superior.
        contentLayout.setTop(titleBox);

        // ==================================================
        // 5. BOTONES DEL MENÚ
        // ==================================================

        // Separación vertical entre los botones: 40 píxeles.
        VBox buttonsBox = new VBox(40);

        // Alinea todos los botones desde el mismo borde izquierdo.
        buttonsBox.setAlignment(Pos.TOP_LEFT);

        // Ancho del área que contiene los botones.
        buttonsBox.setPrefWidth(600);

        // Crea los botones usando el método de estilo personalizado.
        Button btnNuevaPartida =
                crearBotonEstilizado("NUEVA PARTIDA");

        Button btnCargarPartida =
                crearBotonEstilizado("CARGAR PARTIDA");

        Button btnSalir =
                crearBotonEstilizado("SALIR");

        // Ancho y alto de los dos botones principales.
        btnNuevaPartida.setPrefSize(600, 100);
        btnCargarPartida.setPrefSize(600, 100);

        // SALIR mide la mitad del ancho de los otros botones.
        btnSalir.setPrefSize(300, 100);

        // --------------------------------------------------
        // ACCIONES DE LOS BOTONES
        // --------------------------------------------------

        // Abre la pantalla para comenzar una partida nueva.
        btnNuevaPartida.setOnAction(e -> {
            PantallaPartidaNueva nuevaPartida =
                    new PantallaPartidaNueva();

            nuevaPartida.mostrar(stage);
        });

        // Por ahora, CARGAR PARTIDA solo muestra un mensaje.
        btnCargarPartida.setOnAction(e ->
                System.out.println("Cargando partida...")
        );

        // Cierra la ventana cuando se pulsa SALIR.
        btnSalir.setOnAction(e -> stage.close());

        // Agrega los botones en el orden en que aparecerán.
        buttonsBox.getChildren().addAll(
                btnNuevaPartida,
                btnCargarPartida,
                btnSalir
        );

        // Coloca el menú en el sector derecho de BorderPane.
        // El título permanece arriba y no es reemplazado.
        contentLayout.setRight(buttonsBox);

        // Alinea el menú a la izquierda dentro de su sector.
        // Así, SALIR comienza en el mismo punto que los demás.
        BorderPane.setAlignment(buttonsBox, Pos.CENTER_LEFT);

        // --------------------------------------------------
        // AJUSTES FINOS DE POSICIÓN
        // --------------------------------------------------

        // Mueve el menú horizontalmente.
        // Negativo = izquierda. Positivo = derecha.
        buttonsBox.setTranslateX(-20);

        // Mueve el menú verticalmente.
        // Positivo = abajo. Negativo = arriba.
        buttonsBox.setTranslateY(-90);

        // Agrega el diseño por encima del video.
        root.getChildren().add(contentLayout);

        // ==================================================
        // 6. ESCENA Y CONFIGURACIÓN DE LA VENTANA
        // ==================================================

        // Tamaño inicial de la ventana.
        Scene scene = new Scene(root, 1400, 800);

        stage.setTitle("Pokémon Ventura - Inicio");

        // Carga el icono de la aplicación desde resources/images/.
        Image icono = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream(
                                "/images/icono.png"
                        ),
                        "No se encontró icono.png en resources"
                )
        );

        stage.getIcons().add(icono);

        // Configura la escena y los tamaños mínimos.
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // Muestra la ventana.
        stage.show();
    }

    // ==================================================
    // 7. MÉTODO PARA CREAR LOS BOTONES CON ESTILO RETRO
    // ==================================================
    // Crea botones con estética de videojuego pixel art.
    private Button crearBotonEstilizado(String texto) {

        Button boton = new Button(texto);

        // Tamaño del botón.
        boton.setPrefWidth(600);
        boton.setPrefHeight(100);

        // El botón no tendrá fondo ni marco.
        boton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-color: transparent;" +
                        "-fx-border-width: 0;" +
                        "-fx-border-insets: 0;" +
                        "-fx-background-radius: 0;" +
                        "-fx-border-radius: 0;" +
                        "-fx-padding: 0 0 0 35;" +
                        "-fx-alignment: center-left;" +
                        "-fx-text-fill: #f5f0d0;" +
                        "-fx-font-family: 'Monospaced';" +
                        "-fx-font-size: 25px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // Al pasar el mouse, destaca el texto en amarillo.
        boton.setOnMouseEntered(e -> {
            boton.setText("▶ " + texto);
            boton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-insets: 0;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 0;" +
                            "-fx-background-radius: 0;" +
                            "-fx-border-radius: 0;" +
                            "-fx-padding: 0 0 0 35;" +
                            "-fx-alignment: center-left;" +
                            "-fx-text-fill: #ffde00;" +
                            "-fx-font-family: 'Monospaced';" +
                            "-fx-font-size: 25px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
            );
        });

        // Cuando el mouse sale, vuelve al aspecto original.
        boton.setOnMouseExited(e -> {
            boton.setText(texto);
            boton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-insets: 0;" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-width: 0;" +
                            "-fx-background-radius: 0;" +
                            "-fx-border-radius: 0;" +
                            "-fx-padding: 0 0 0 35;" +
                            "-fx-alignment: center-left;" +
                            "-fx-text-fill: #f5f0d0;" +
                            "-fx-font-family: 'Monospaced';" +
                            "-fx-font-size: 25px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
            );
        });

        return boton;
    }

    // Punto de entrada de la aplicación.
    public static void main(String[] args) {
        launch(args);
    }
}
