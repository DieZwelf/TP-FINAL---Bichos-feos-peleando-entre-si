package com.example.demojuego.vista;

// Importaciones de JavaFX para la ventana, los controles y el diseño.
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class PantallaInicio {

    public void start(Stage stage) {

        // ==================================================
        // 1. CONTENEDOR PRINCIPAL
        // ==================================================
        StackPane root = new StackPane();

        // ==================================================
        // 2. VIDEO DE FONDO
        // ==================================================
        URL archivoVideo = getClass().getResource("/videos/fondo.mp4");

        if (archivoVideo == null) {
            throw new IllegalStateException(
                    "No se encontró videos/fondo.mp4 en resources"
            );
        }

        Media media = new Media(archivoVideo.toExternalForm());
        MediaPlayer reproductor = new MediaPlayer(media);
        reproductor.setCycleCount(MediaPlayer.INDEFINITE);

        MediaView fondoAnimado = new MediaView(reproductor);
        fondoAnimado.setPreserveRatio(false);
        fondoAnimado.fitWidthProperty().bind(root.widthProperty());
        fondoAnimado.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().add(fondoAnimado);

        reproductor.setOnReady(reproductor::play);
        reproductor.setOnError(() ->
                System.out.println(
                        "Error al reproducir el video: "
                                + reproductor.getError()
                )
        );

        stage.setOnHidden(e -> reproductor.dispose());

        // ==================================================
        // 3. DISEÑO LIBRE / ABSOLUTO (AnchorPane)
        // ==================================================
        // AnchorPane permite desacoplar los elementos para que no
        // se muevan entre sí al cambiar de tamaño.
        AnchorPane contentLayout = new AnchorPane();

        // ==================================================
        // 4. TÍTULO ARRIBA A LA IZQUIERDA
        // ==================================================
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.TOP_LEFT);

        Label tituloPrincipal = new Label("BICHOSTROFE");


        Label segundaLinea = new Label("VENTURA");

        String estiloSubtitulo =
                "-fx-text-fill: #ffde00;" +                   // Amarillo Pokémon puro
                        "-fx-font-family: 'Monospaced';" +
                        "-fx-font-size: 100px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5px 0 0 5px;";

        segundaLinea.setStyle(estiloSubtitulo);
        tituloPrincipal.setStyle(estiloSubtitulo);


        titleBox.getChildren().addAll(
                tituloPrincipal,
                segundaLinea
        );

        // Fija el título a 35px de la parte superior y 45px de la izquierda
        AnchorPane.setTopAnchor(titleBox, 35.0);
        AnchorPane.setLeftAnchor(titleBox, 45.0);

        // ==================================================
        // 5. BOTONES DEL MENÚ (POSICIONAMIENTO MANUAL)
        // ==================================================
        VBox buttonsBox = new VBox(37);
        buttonsBox.setAlignment(Pos.TOP_LEFT);
        buttonsBox.setPrefWidth(600);

        Button btnNuevaPartida = crearBotonEstilizado("NUEVA PARTIDA");
        Button btnCargarPartida = crearBotonEstilizado("CARGAR PARTIDA");
        Button btnSalir = crearBotonEstilizado("SALIR");

        btnNuevaPartida.setPrefSize(600, 100);
        btnCargarPartida.setPrefSize(600, 100);
        btnSalir.setPrefSize(300, 100);

        // ACCIONES
        btnNuevaPartida.setOnAction(e -> {
            PantallaPartidaNueva.mostrar(stage);
        });

        btnCargarPartida.setOnAction(e ->
                System.out.println("Cargando partida...")
        );

        btnSalir.setOnAction(e -> stage.close());

        buttonsBox.getChildren().addAll(
                btnNuevaPartida,
                btnCargarPartida,
                btnSalir
        );

        // --------------------------------------------------
        // CONTROLES MANUALES DE POSICIÓN
        // --------------------------------------------------
        // Mueve todo el grupo de botones a la posición X e Y exacta en la pantalla:
        // - Aumentar X = Mover a la derecha | Disminuir X = Mover a la izquierda
        // - Aumentar Y = Mover hacia abajo  | Disminuir Y = Mover hacia arriba
        buttonsBox.setTranslateX(920);
        buttonsBox.setTranslateY(120);

        // Añadimos título y botones al AnchorPane
        contentLayout.getChildren().addAll(titleBox, buttonsBox);

        // Colocamos el diseño por encima del video
        root.getChildren().add(contentLayout);

        // ==================================================
        // 6. ESCENA Y CONFIGURACIÓN DE LA VENTANA
        // ==================================================
        Scene scene = new Scene(root, 1400, 800);

        stage.setTitle("Pokémon Ventura - Inicio");

        Image icono = new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream("/images/pantallaMenu/icono.png"),
                        "No se encontró icono.png en resources"
                )
        );

        stage.getIcons().add(icono);
        URL css = getClass().getResource("/images/pantallaMenu/estilo.css");
        scene.getStylesheets().add(Objects.requireNonNull(css, "No se encontró estilo.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    // ==================================================
    // 7. METODO PARA CREAR LOS BOTONES CON ESTILO RETRO
    // ==================================================
    private Button crearBotonEstilizado(String texto) {
        Button boton = new Button(texto);
        //Aca en la direccion si ponen boton-retro queda con una cajita, no me decido
        boton.getStyleClass().add("boton-menu");   // el estilo viene del CSS

        boton.setOnMouseEntered(e -> boton.setText("▶ " + texto));
        boton.setOnMouseExited(e -> boton.setText(texto));

        return boton;
    }

}