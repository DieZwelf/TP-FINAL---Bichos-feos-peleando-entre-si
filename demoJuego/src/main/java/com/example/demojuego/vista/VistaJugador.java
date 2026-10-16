package com.example.demojuego.vista;

import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class VistaJugador {

    // Arreglos con las imágenes ya cargadas
    private Image[] framesAbajo;
    private Image[] framesArriba;
    private Image[] framesIzquierda;
    private Image[] framesDerecha;

    private ImageView vistaJugador;
    private Pane root; // Contenedor del mapa y jugador

    public void mostrar(Stage stage) {

        // 1. Crear mapa
        Mapa mapa = new Mapa(50, 80);
        MapaView mapaView = new MapaView(mapa);

        // 2. Cargar Arreglos de Imágenes del Jugador
        framesAbajo = new Image[]{
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando1.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando2.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando3.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando4.png")
        };
        framesArriba = new Image[]{
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoA1.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoA2.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoA3.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoA4.png")
        };
        framesIzquierda = new Image[]{
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoI1.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoI2.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoI3.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoI4.png")
        };
        framesDerecha = new Image[]{
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoD1.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoD2.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoD3.png"),
                cargarImagen("/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminandoD4.png")
        };

        // 3. Crear ImageView del Jugador y configurar tamaño
        vistaJugador = new ImageView(framesAbajo[0]);

        double anchoDeseado = 64; // Subido a 64px para mejor visibilidad estilo retro
        vistaJugador.setFitWidth(anchoDeseado);
        vistaJugador.setPreserveRatio(true);
        vistaJugador.setSmooth(false);

        // 4. INSTANCIAR EL PANE CONTENEDOR (Soluciona el NullPointerException)
        root = new Pane();
        root.getChildren().addAll(mapaView, vistaJugador); // Capa 0: Mapa, Capa 1: Jugador

        // 5. Meter todo dentro del ScrollPane
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);

        Scene scene = new Scene(scrollPane, 1000, 700);

        // 6. Instanciar Modelo y Controlador
        Jugador jugador = new Jugador(200, 200, 4.0);
        JuegoController controller = new JuegoController(jugador, this, scene);
        controller.iniciarGameLoop();

        stage.setTitle("Bichostrofe Ventura - Juego");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Actualiza la posición y la imagen en pantalla
     */
    public void renderizar(Jugador jugador) {
        vistaJugador.setX(jugador.getX());
        vistaJugador.setY(jugador.getY());

        Image[] arregloDireccion = switch (jugador.getDireccion()) {
            case "ARRIBA" -> framesArriba;
            case "IZQUIERDA" -> framesIzquierda;
            case "DERECHA" -> framesDerecha;
            default -> framesAbajo;
        };

        // Cambiar la imagen que muestra el ImageView
        int frame = jugador.getFrameActual() % arregloDireccion.length;
        vistaJugador.setImage(arregloDireccion[frame]);
    }

    private Image cargarImagen(String ruta) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(ruta), "No se encontró: " + ruta
        ));
    }
}