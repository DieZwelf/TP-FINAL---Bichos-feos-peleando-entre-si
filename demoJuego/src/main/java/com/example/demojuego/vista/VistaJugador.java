package com.example.demojuego.vista;

import com.example.demojuego.controlador.JuegoController;
import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

public class VistaJugador extends Pane{

    private ImageView vistaJugador;
    private Jugador jugador;
    private Image[] framesAbajo;
    private Image[] framesArriba;
    private Image[] framesIzquierda;
    private Image[] framesDerecha;

    public VistaJugador(Image[] framesAbajo, Image[] framesArriba, Image[] framesIzquierda, Image[] framesDerecha, ImageView vistaJugador) {
        this.framesAbajo = framesAbajo;
        this.framesArriba = framesArriba;
        this.framesIzquierda = framesIzquierda;
        this.framesDerecha = framesDerecha;
        this.vistaJugador = vistaJugador;
    }

    public VistaJugador(Image[] framesAbajo, Image[] framesArriba, Image[] framesIzquierda, Image[] framesDerecha, ImageView vistaJugador, Node... nodes) {
        super(nodes);
        this.framesAbajo = framesAbajo;
        this.framesArriba = framesArriba;
        this.framesIzquierda = framesIzquierda;
        this.framesDerecha = framesDerecha;
        this.vistaJugador = vistaJugador;
    }

    public VistaJugador (Jugador jugador){
        this.jugador = jugador;
    }

    public VistaJugador (){

    }


    private void cargarFrames() {
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
    }

    public void mostrar(Stage stage) {

        cargarFrames();
        // 3. Crear ImageView del Jugador y configurar tamaño
        vistaJugador = new ImageView(framesAbajo[0]);

        double anchoDeseado = 64; // Subido a 64px para mejor visibilidad estilo retro
        vistaJugador.setFitWidth(anchoDeseado);
        vistaJugador.setPreserveRatio(true);
        vistaJugador.setSmooth(false);

        getChildren().add(vistaJugador);
    }

    public void inicializar(Jugador jugador) {
        this.jugador = jugador;

        // Si todavía no se cargaron las imágenes, cargarlas.
        if (framesAbajo == null) {
            cargarFrames();
        }

        // Crear el ImageView si todavía no existe.
        if (vistaJugador == null) {
            vistaJugador = new ImageView();
            vistaJugador.setFitWidth(64);
            vistaJugador.setPreserveRatio(true);
            vistaJugador.setSmooth(false);
        }

        // Asignar la imagen inicial.
        vistaJugador.setImage(framesAbajo[0]);

        // Agregarlo al Pane solamente si todavía no está agregado.
        if (!getChildren().contains(vistaJugador)) {
            getChildren().add(vistaJugador);
        }

        renderizar(jugador);
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