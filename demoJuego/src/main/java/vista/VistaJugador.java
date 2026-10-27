package vista;

import model.Jugador;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class VistaJugador extends Pane {

    private static final String RUTA = "/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando";

    private final ImageView imagenJugador = new ImageView();
    private final Image[] framesAbajo = cargarFrames("");
    private final Image[] framesArriba = cargarFrames("A");
    private final Image[] framesIzquierda = cargarFrames("I");
    private final Image[] framesDerecha = cargarFrames("D");

    public VistaJugador(Jugador jugador) {
        imagenJugador.setFitWidth(48);
        imagenJugador.setPreserveRatio(true);
        imagenJugador.setSmooth(false);

        getChildren().add(imagenJugador);
        renderizar(jugador);
    }

    /** Actualiza la posición y la imagen en pantalla. */
    public void renderizar(Jugador jugador) {
        imagenJugador.setX(jugador.getX());
        imagenJugador.setY(jugador.getY());

        Image[] frames = switch (jugador.getDireccion()) {
            case "ARRIBA" -> framesArriba;
            case "IZQUIERDA" -> framesIzquierda;
            case "DERECHA" -> framesDerecha;
            default -> framesAbajo;
        };

        imagenJugador.setImage(frames[jugador.getFrameActual() % frames.length]);
    }

    // sufijo: "" (abajo), "A", "I" o "D"
    private Image[] cargarFrames(String sufijo) {
        Image[] frames = new Image[4];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(RUTA + sufijo + (i + 1) + ".png");
        }
        return frames;
    }

    private Image cargarImagen(String ruta) {
        return new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(ruta), "No se encontró: " + ruta));
    }
}