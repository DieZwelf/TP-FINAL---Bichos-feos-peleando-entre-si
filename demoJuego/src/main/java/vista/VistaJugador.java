
package vista;

import model.Jugador;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;

public class VistaJugador extends Pane {

    private static final String RUTA_CAMINANDO = "/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando";

    private static final String RUTA_QUIETO = "/images/personajes/entrenador/entrenadorQuieto/entrenador";

    private final ImageView imagenJugador = new ImageView();

    // Animaciones de caminata: 4 frames por dirección.
    private final Image[] framesAbajo = cargarFramesCaminando("");
    private final Image[] framesArriba = cargarFramesCaminando("A");
    private final Image[] framesIzquierda = cargarFramesCaminando("I");
    private final Image[] framesDerecha = cargarFramesCaminando("D");

    // Animaciones idle: 2 frames por dirección.
    // Abajo: entrenador1.png y entrenador2.png.
    // Derecha: entrenador3.png y entrenador4.png.
    // Izquierda: entrenador5.png y entrenador6.png.
    // Arriba: entrenador7.png y entrenador8.png.
    private final Image[] idleAbajo = cargarFramesQuieto(1);
    private final Image[] idleDerecha = cargarFramesQuieto(3);
    private final Image[] idleIzquierda = cargarFramesQuieto(5);
    private final Image[] idleArriba = cargarFramesQuieto(7);

    public VistaJugador(Jugador jugador) {
        imagenJugador.setFitWidth(48);
        imagenJugador.setFitHeight(48);
        imagenJugador.setPreserveRatio(false);
        imagenJugador.setSmooth(false);

        getChildren().add(imagenJugador);

        renderizar(jugador);
    }

    /**
     * Actualiza la posición y la animación del jugador.
     */
    public void renderizar(Jugador jugador) {
        imagenJugador.setX(jugador.getX());
        imagenJugador.setY(jugador.getY());

        boolean caminando = jugador.isEnMovimiento();

        Image[] frames;

        if (caminando) {
            frames = switch (jugador.getDireccion()) {
                case "ARRIBA" -> framesArriba;
                case "IZQUIERDA" -> framesIzquierda;
                case "DERECHA" -> framesDerecha;
                default -> framesAbajo;
            };

            imagenJugador.setImage(frames[jugador.getFrameActual() % frames.length]);
        } else {
            frames = switch (jugador.getDireccion()) {
                case "ARRIBA" -> idleArriba;
                case "IZQUIERDA" -> idleIzquierda;
                case "DERECHA" -> idleDerecha;
                default -> idleAbajo;
            };

            // El frame cambia entre 0 y 1 para generar el idle.
            imagenJugador.setImage(frames[jugador.getFrameActual() % frames.length]);
        }
    }

    /**
     * Carga los cuatro frames de caminata de una dirección.
     */
    private Image[] cargarFramesCaminando(String sufijo) {
        Image[] frames = new Image[4];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(RUTA_CAMINANDO + sufijo + (i + 1) + ".png");
        }

        return frames;
    }

    /**
     * Carga dos frames idle consecutivos.
     */
    private Image[] cargarFramesQuieto(int primerNumero) {
        Image[] frames = new Image[2];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(RUTA_QUIETO + (primerNumero + i) + ".png");
        }

        return frames;
    }

    private Image cargarImagen(String ruta) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(ruta),
                        "No se encontró la imagen: " + ruta));
    }
}
