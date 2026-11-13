
package vista.juego;

import model.Jugador;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class VistaJugador extends Pane {

    private static final String RUTA_CAMINANDO =
            "/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando";

    private static final String RUTA_QUIETO =
            "/images/personajes/entrenador/entrenadorQuieto/entrenador";

    private static final double TAMANIO = 48;

    private final ImageView imagenJugador = new ImageView();

    // Sombra debajo del personaje.
    private final Ellipse sombra = new Ellipse(16, 5);

    // Evita regenerar el contorno en cada actualización.
    private final Map<Image, Image> imagenesConContorno =
            new IdentityHashMap<>();

    // Animaciones de caminata.
    private final Image[] framesAbajo = cargarFramesCaminando("");
    private final Image[] framesArriba = cargarFramesCaminando("A");
    private final Image[] framesIzquierda = cargarFramesCaminando("I");
    private final Image[] framesDerecha = cargarFramesCaminando("D");

    // Animaciones idle.
    private final Image[] idleAbajo = cargarFramesQuieto(1);
    private final Image[] idleDerecha = cargarFramesQuieto(3);
    private final Image[] idleIzquierda = cargarFramesQuieto(5);
    private final Image[] idleArriba = cargarFramesQuieto(7);

    public VistaJugador(Jugador jugador) {

        imagenJugador.setPreserveRatio(false);
        imagenJugador.setSmooth(false);
        imagenJugador.setCache(false);

        // Sombra ligeramente más grande.
        sombra.setFill(Color.rgb(0, 0, 0, 0.32));

        getChildren().addAll(sombra, imagenJugador);

        renderizar(jugador);
    }

    /**
     * Genera un borde oscuro de un píxel alrededor
     * de la silueta original del personaje.
     */
    private Image generarContorno(Image original) {

        Image existente = imagenesConContorno.get(original);

        if (existente != null) {
            return existente;
        }

        PixelReader lector = original.getPixelReader();

        if (lector == null) {
            return original;
        }

        int ancho = (int) original.getWidth();
        int alto = (int) original.getHeight();

        // Un píxel adicional alrededor de la imagen.
        WritableImage resultado =
                new WritableImage(ancho + 2, alto + 2);

        PixelWriter escritor = resultado.getPixelWriter();

        Color colorBorde = Color.rgb(35, 30, 38);

        // Dibujamos el borde alrededor de los píxeles visibles.
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                if (lector.getColor(x, y).getOpacity() <= 0.1) {
                    continue;
                }

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {

                        if (dx == 0 && dy == 0) {
                            continue;
                        }

                        int nx = x + dx;
                        int ny = y + dy;

                        if (nx < 0 || nx >= ancho ||
                                ny < 0 || ny >= alto) {
                            continue;
                        }

                        if (lector.getColor(nx, ny).getOpacity() <= 0.1) {
                            escritor.setColor(nx + 1, ny + 1, colorBorde);
                        }
                    }
                }
            }
        }

        // Dibujamos el sprite original encima del contorno.
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                escritor.setColor(
                        x + 1,
                        y + 1,
                        lector.getColor(x, y)
                );
            }
        }

        Image imagenFinal = resultado;
        imagenesConContorno.put(original, imagenFinal);

        return imagenFinal;
    }

    /**
     * Actualiza la posición y la animación del jugador.
     */
    public void renderizar(Jugador jugador) {

        double x = jugador.getX();
        double y = jugador.getY();

        Image[] frames;

        if (jugador.isEnMovimiento()) {

            frames = switch (jugador.getDireccion()) {
                case "ARRIBA" -> framesArriba;
                case "IZQUIERDA" -> framesIzquierda;
                case "DERECHA" -> framesDerecha;
                default -> framesAbajo;
            };

        } else {

            frames = switch (jugador.getDireccion()) {
                case "ARRIBA" -> idleArriba;
                case "IZQUIERDA" -> idleIzquierda;
                case "DERECHA" -> idleDerecha;
                default -> idleAbajo;
            };
        }

        Image frameActual =
                frames[jugador.getFrameActual() % frames.length];

        Image imagenConContorno = generarContorno(frameActual);

        // Conservamos el tamaño visual original del personaje.
        double anchoOriginal = frameActual.getWidth();
        double altoOriginal = frameActual.getHeight();

        double escalaX = TAMANIO / anchoOriginal;
        double escalaY = TAMANIO / altoOriginal;

        imagenJugador.setImage(imagenConContorno);

        imagenJugador.setFitWidth(TAMANIO + 2 * escalaX);
        imagenJugador.setFitHeight(TAMANIO + 2 * escalaY);

        imagenJugador.setX(x - escalaX);
        imagenJugador.setY(y - escalaY);

        // Sombra debajo de los pies.
        sombra.setCenterX(x + 24);
        sombra.setCenterY(y + 44);
    }

    private Image[] cargarFramesCaminando(String sufijo) {

        Image[] frames = new Image[4];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(
                    RUTA_CAMINANDO + sufijo + (i + 1) + ".png"
            );
        }

        return frames;
    }

    private Image[] cargarFramesQuieto(int primerNumero) {

        Image[] frames = new Image[2];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(
                    RUTA_QUIETO + (primerNumero + i) + ".png"
            );
        }

        return frames;
    }

    private Image cargarImagen(String ruta) {

        return new Image(
                Objects.requireNonNull(
                        getClass().getResourceAsStream(ruta),
                        "No se encontró la imagen: " + ruta
                )
        );
    }
}
