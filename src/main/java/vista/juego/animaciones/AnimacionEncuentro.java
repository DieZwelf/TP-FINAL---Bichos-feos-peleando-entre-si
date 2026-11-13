
package vista.juego.animaciones;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Gestiona la animación visual que se reproduce al iniciar un encuentro.
 *
 * Realiza destellos blancos y muestra un cartel anunciando
 * la aparición de un Pokémon salvaje.
 */
public class AnimacionEncuentro {

    private AnimacionEncuentro() {
        // Clase de utilidad: no necesita instancias.
    }

    /**
     * Ejecuta la animación de encuentro.
     *
     * @param root pantalla sobre la que se muestra la animación
     * @param detenerJuego acción para detener el ciclo del juego
     * @param alTerminar acción que se ejecuta al finalizar la animación
     */
    public static void reproducir(
            StackPane root,
            Runnable detenerJuego,
            Runnable alTerminar
    ) {

        detenerJuego.run();

        double ancho = root.getWidth();
        double alto = root.getHeight();

        Pane capaTransicion = new Pane();
        capaTransicion.setPrefSize(ancho, alto);
        capaTransicion.setMouseTransparent(true);

        root.getChildren().add(capaTransicion);

        Rectangle flash = new Rectangle(ancho, alto, Color.WHITE);
        flash.setOpacity(0);

        capaTransicion.getChildren().add(flash);

        Timeline parpadeo = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(flash.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(50),
                        new KeyValue(flash.opacityProperty(), 0.95)
                ),
                new KeyFrame(
                        Duration.millis(110),
                        new KeyValue(flash.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(160),
                        new KeyValue(flash.opacityProperty(), 0.9)
                ),
                new KeyFrame(
                        Duration.millis(230),
                        new KeyValue(flash.opacityProperty(), 0)
                )
        );

        parpadeo.setOnFinished(evento -> {
            capaTransicion.getChildren().remove(flash);

            mostrarMensaje(
                    root,
                    capaTransicion,
                    ancho,
                    alto,
                    alTerminar
            );
        });

        parpadeo.play();
    }

    /**
     * Muestra el cartel de aparición del Pokémon salvaje.
     *
     * @param root pantalla principal
     * @param capaTransicion capa temporal de la animación
     * @param ancho ancho de la pantalla
     * @param alto alto de la pantalla
     * @param alTerminar acción posterior al cartel
     */
    private static void mostrarMensaje(
            StackPane root,
            Pane capaTransicion,
            double ancho,
            double alto,
            Runnable alTerminar
    ) {

        StackPane cartel = new StackPane();
        cartel.getStyleClass().add("cartel-encuentro");
        cartel.setMaxSize(580, 110);

        Label mensaje = new Label("¡Un Pokémon salvaje apareció!");
        mensaje.getStyleClass().add("texto-cartel-encuentro");

        cartel.getChildren().add(mensaje);

        StackPane contenedor = new StackPane(cartel);
        contenedor.setPrefSize(ancho, alto);

        StackPane.setAlignment(cartel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(cartel, new Insets(0, 0, 50, 0));

        capaTransicion.getChildren().setAll(contenedor);

        FadeTransition aparecer = new FadeTransition(
                Duration.millis(250),
                cartel
        );

        aparecer.setFromValue(0);
        aparecer.setToValue(1);

        aparecer.setOnFinished(evento -> {

            PauseTransition pausa = new PauseTransition(
                    Duration.millis(1200)
            );

            pausa.setOnFinished(fin -> {
                root.getChildren().remove(capaTransicion);
                alTerminar.run();
            });

            pausa.play();
        });

        aparecer.play();
    }
}
