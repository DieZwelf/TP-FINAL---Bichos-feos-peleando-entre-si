
package vista.gestor;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;
import vista.base.PantallaMenuBase;

import java.net.URL;

/**
 * Administra el video de fondo utilizado en los menús.
 *
 * Mantiene una única instancia del reproductor para reutilizarla
 * al navegar entre las distintas pantallas.
 */
public class GestorVideoMenu {

    private final GestorPantallas gestorPantallas;

    private MediaPlayer reproductor;

    public GestorVideoMenu(GestorPantallas gestorPantallas) {
        this.gestorPantallas = gestorPantallas;
    }

    /**
     * Obtiene el reproductor, creándolo si todavía no existe.
     */
    public MediaPlayer obtenerReproductor() {

        if (reproductor == null) {

            URL archivoVideo = getClass().getResource( // getClass() Obtiene la clase del objeto actual, en este caso GestorVideoMenu.
                    "/videos/fondo.mp4" // ruta
            );

            if (archivoVideo == null) {
                throw new IllegalStateException(
                        "No se encontró /videos/fondo.mp4"
                );
            }

            Media media = new Media(archivoVideo.toExternalForm()); //archivoVideo.toExternalForm() convierte la ubicación del recurso en una dirección que JavaFX puede utilizar.

            reproductor = new MediaPlayer(media);

            reproductor.setCycleCount(MediaPlayer.INDEFINITE); // Esto configura cuántas veces se repetirá el video. MediaPlayer.INDEFINITE significa que se reproducirá indefinidamente.

            reproductor.setOnReady(() -> {

                if (gestorPantallas.getPantallaActualInterna()
                        instanceof PantallaMenuBase) {
                    reproductor.play(); // comienza a reproducir el video
                }
            });

            reproductor.setOnError(() -> { // Define qué hacer si el reproductor tiene un error

                System.err.println( // Imprime un mensaje en la salida de errores de la consola
                        "Error reproduciendo video: "
                                + reproductor.getError() // Informacion del error
                );
            });
        }

        return reproductor; //Devuelve el reproductor con el video
    }

    /**
     * Este método administra la reproducción del video del menú.
     * Recibe la pantalla actual y utiliza instanceof para comprobar si pertenece a la clase PantallaMenuBase.
     * Si es un menú, consulta el estado del reproductor y lo reproduce cuando está preparado, pausado o detenido.
     * Si es otra pantalla, pausa el video
     *
     * @param pantalla pantalla que se acaba de mostrar
     */
    public void actualizar(PantallaBase pantalla) {

        if (pantalla instanceof PantallaMenuBase) {

            MediaPlayer reproductorActual = obtenerReproductor();

            MediaPlayer.Status estado = reproductorActual.getStatus();

            if (estado == MediaPlayer.Status.READY
                    || estado == MediaPlayer.Status.PAUSED
                    || estado == MediaPlayer.Status.STOPPED) {

                reproductorActual.play();
            }

        } else if (reproductor != null) {

            reproductor.pause();
        }
    }

    /**
     * Detiene y libera los recursos del reproductor.
     */
    public void liberar() {

        if (reproductor != null) {

            reproductor.stop();
            reproductor.dispose();

            reproductor = null;
        }
    }
}
