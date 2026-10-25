package com.example.demojuego.vista;

import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class PantallaPartidaNueva implements iPantalla{

    private static final String HOJA_ESTILOS = "/images/pantallaMenu/estilo.css";

    public static void iniciarPartida(Stage stage) {
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

        Label titulo = new Label("¡NUEVA PARTIDA!");
        Label mensaje = new Label("¡Tu aventura comienza ahora!");


        /**
         * ===================================
         */

        Button btnComenzar = new Button("Comenzar aventura");
        btnComenzar.setOnAction(e -> {
            iniciarPartida(stage);
        });

        VBox contenido = new VBox(
                25,
                titulo,
                mensaje,
                btnComenzar
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        root.getChildren().addAll(fondo,contenido);

        // Asignar las clases CSS: cada una activa un bloque de estilo.
        btnComenzar.getStyleClass().add("boton-retro");
        titulo.getStyleClass().add("titulo-retro");
        mensaje.getStyleClass().add("texto-retro");
        contenido.getStyleClass().add("panel-retro");

        // La Scene usa root como raíz (no contenido, que ya está dentro de root)
        Scene escena = new Scene(root, 900, 650);

        // Buscar el archivo en el classpath. Devuelve null si la ruta es incorrecta.
        URL css = PantallaInicio.class.getResource(HOJA_ESTILOS);

        //Si es nulo falla con un error más claro del que tiraba antes (NullPointerExcepction)
        Objects.requireNonNull(css, "No se encontró la hoja de estilos: " + HOJA_ESTILOS);

        //Registrarla en la escena. Se aplica a todos los nodos de esa escena.
        escena.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Monster Quest - Nueva partida");
        stage.setScene(escena);
        stage.setFullScreen(true);
    }
}