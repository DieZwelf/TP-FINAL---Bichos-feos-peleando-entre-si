
package vista.combate.animaciones;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AnimacionResultado {

    // Animación utilizada durante el combate.
    public void reproducir(
            Scene escena,
            boolean victoria,
            Runnable alTerminar
    ) {
        Label titulo = new Label(
                victoria ? "¡VICTORIA!" : "¡DERROTA!"
        );

        titulo.setStyle(
                "-fx-font-size: 58px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        (victoria ? "#FFD700" : "#FF5555") + ";"
        );

        reproducirAnimacion(
                escena,
                titulo,
                victoria,
                alTerminar,
                true
        );
    }

    // Animación del título que ya existe en PantallaResultados.
    public void reproducir(
            Scene escena,
            Label titulo,
            boolean victoria,
            Runnable alTerminar
    ) {
        reproducirAnimacion(
                escena,
                titulo,
                victoria,
                alTerminar,
                false
        );
    }

    private void reproducirAnimacion(
            Scene escena,
            Label titulo,
            boolean victoria,
            Runnable alTerminar,
            boolean agregarTituloAlOverlay
    ) {
        StackPane root = (StackPane) escena.getRoot();

        StackPane overlay = new StackPane();
        overlay.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        String colorFondo = victoria
                ? "rgba(25, 20, 5, 0.94)"
                : "rgba(25, 5, 10, 0.95)";

        String colorBrillo = victoria
                ? "#FFF2A6"
                : "#FF9999";

        overlay.setStyle(
                "-fx-background-color: " + colorFondo + ";"
        );

        Label subtitulo = new Label(
                victoria
                        ? "HAS GANADO EL COMBATE"
                        : "HAS SIDO DERROTADO"
        );

        subtitulo.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-opacity: 0.85;"
        );

        titulo.setStyle(
                titulo.getStyle() +
                        "-fx-effect: dropshadow(gaussian, " +
                        colorBrillo + ", 24, 0.8, 0, 0);"
        );

        VBox contenido = new VBox(18);
        contenido.setAlignment(Pos.CENTER);

        if (agregarTituloAlOverlay) {
            contenido.getChildren().add(titulo);
        } else {
            // El título ya está en la pantalla de resultados.
            // No lo agregamos a otro contenedor.
            contenido.getChildren().add(subtitulo);
        }

        if (agregarTituloAlOverlay) {
            contenido.getChildren().add(subtitulo);
        }

        overlay.getChildren().add(contenido);
        overlay.setOpacity(0);

        titulo.setScaleX(0.65);
        titulo.setScaleY(0.65);

        root.getChildren().add(overlay);

        FadeTransition entrada = new FadeTransition(
                Duration.millis(300),
                overlay
        );
        entrada.setFromValue(0);
        entrada.setToValue(1);

        ScaleTransition impacto = new ScaleTransition(
                Duration.millis(450),
                titulo
        );
        impacto.setFromX(0.65);
        impacto.setFromY(0.65);
        impacto.setToX(1.08);
        impacto.setToY(1.08);
        impacto.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition ajuste = new ScaleTransition(
                Duration.millis(180),
                titulo
        );
        ajuste.setToX(1);
        ajuste.setToY(1);
        ajuste.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition pausa = new PauseTransition(
                Duration.millis(1100)
        );

        FadeTransition salida = new FadeTransition(
                Duration.millis(300),
                overlay
        );
        salida.setFromValue(1);
        salida.setToValue(0);

        SequentialTransition secuencia = new SequentialTransition(
                new ParallelTransition(entrada, impacto),
                ajuste,
                pausa,
                salida
        );

        secuencia.setOnFinished(evento -> {
            root.getChildren().remove(overlay);
            titulo.setScaleX(1);
            titulo.setScaleY(1);

            if (alTerminar != null) {
                alTerminar.run();
            }
        });

        secuencia.play();
    }
}
