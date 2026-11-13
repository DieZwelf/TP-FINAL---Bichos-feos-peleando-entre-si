
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

/**
 * La clase VISTAJUGADOR representa visualmente al personaje del jugador.
 * Hereda de Pane y contiene la imagen del personaje, su sombra
 * y las animaciones correspondientes a sus movimientos y estados quietos.
 * Utiliza diferentes sprites según la dirección y el estado del jugador.
 * También genera y almacena contornos para mejorar la apariencia visual.
 * Tiene un constructor que recibe al jugador.
 * Otros métodos: generarContorno(), renderizar(), cargarFramesCaminando(),
 * cargarFramesQuieto() y cargarImagen().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class VistaJugador extends Pane {

    private static final String RUTA_CAMINANDO = "/images/personajes/entrenador/entrenadorCaminando/entrenadorCaminando";

    private static final String RUTA_QUIETO = "/images/personajes/entrenador/entrenadorQuieto/entrenador";

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

    /**
     * Construye la representación visual del jugador.
     * Configura la imagen, la sombra y renderiza su estado inicial.
     *
     * @param jugador jugador cuya representación visual se construye
     */
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
     * Almacena el resultado para evitar generar nuevamente
     * el contorno de una imagen que ya fue procesada.
     *
     * @param original imagen original del personaje
     * @return imagen original con el contorno generado
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
     * Selecciona los frames correspondientes según su dirección
     * y determina si está caminando o permanece quieto.
     * También actualiza la sombra y las dimensiones de la imagen.
     *
     * @param jugador jugador cuyo estado se representa visualmente
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

    /**
     * Carga los cuatro frames correspondientes a la animación de caminata.
     *
     * @param sufijo sufijo que identifica la dirección de los sprites
     * @return arreglo con los frames de la animación
     */
    private Image[] cargarFramesCaminando(String sufijo) {

        Image[] frames = new Image[4];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(RUTA_CAMINANDO + sufijo + (i + 1) + ".png");
        }

        return frames;
    }

    /**
     * Carga los dos frames correspondientes a la animación de reposo.
     *
     * @param primerNumero número inicial de los archivos de imagen
     * @return arreglo con los frames de reposo
     */
    private Image[] cargarFramesQuieto(int primerNumero) {

        Image[] frames = new Image[2];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = cargarImagen(
                    RUTA_QUIETO + (primerNumero + i) + ".png"
            );
        }

        return frames;
    }

    /**
     * Carga una imagen a partir de su ruta dentro de los recursos del proyecto.
     *
     * @param ruta ubicación del archivo de imagen
     * @return imagen cargada
     */
    private Image cargarImagen(String ruta) {

        return new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(ruta), "No se encontró la imagen: " + ruta)
        );
    }
}
