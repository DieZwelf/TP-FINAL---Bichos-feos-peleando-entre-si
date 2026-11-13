
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

            URL archivoVideo = getClass().getResource(
                    "/videos/fondo.mp4"
            );

            if (archivoVideo == null) {
                throw new IllegalStateException(
                        "No se encontró /videos/fondo.mp4"
                );
            }

            Media media = new Media(archivoVideo.toExternalForm());

            reproductor = new MediaPlayer(media);

            reproductor.setCycleCount(MediaPlayer.INDEFINITE);

            reproductor.setOnReady(() -> {

                if (gestorPantallas.getPantallaActualInterna()
                        instanceof PantallaMenuBase) {
                    reproductor.play();
                }
            });

            reproductor.setOnError(() -> {

                System.err.println(
                        "Error reproduciendo video: "
                                + reproductor.getError()
                );
            });
        }

        return reproductor;
    }

    /**
     * Reproduce el video en los menús y lo pausa en las demás pantallas.
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
