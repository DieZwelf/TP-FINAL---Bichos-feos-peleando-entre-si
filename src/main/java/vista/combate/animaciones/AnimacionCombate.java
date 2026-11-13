
package vista.combate.animaciones;

import javafx.animation.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class AnimacionCombate {

    public void animarEntrada(
            ImageView pokemonSalvaje,
            ImageView pokemonActivo,
            BorderPane interfaz,
            Runnable alTerminar
    ) {
        pokemonSalvaje.setTranslateX(550);
        pokemonActivo.setTranslateX(-550);

        TranslateTransition entradaSalvaje =
                new TranslateTransition(Duration.millis(650), pokemonSalvaje);

        entradaSalvaje.setFromX(550);
        entradaSalvaje.setToX(0);
        entradaSalvaje.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition entradaJugador =
                new TranslateTransition(Duration.millis(650), pokemonActivo);

        entradaJugador.setFromX(-550);
        entradaJugador.setToX(0);
        entradaJugador.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition entradas = new ParallelTransition(
                entradaSalvaje,
                entradaJugador
        );

        ParallelTransition rebotes = new ParallelTransition(
                crearRebote(pokemonSalvaje),
                crearRebote(pokemonActivo)
        );

        SequentialTransition presentacion = new SequentialTransition(
                entradas,
                rebotes
        );

        presentacion.setOnFinished(evento -> {
            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(300),
                    interfaz
            );

            aparecer.setFromValue(0);
            aparecer.setToValue(1);

            aparecer.setOnFinished(fin -> {
                interfaz.setDisable(false);

                if (alTerminar != null) {
                    alTerminar.run();
                }
            });

            aparecer.play();
        });

        presentacion.play();
    }

    private TranslateTransition crearRebote(ImageView pokemon) {
        TranslateTransition rebote = new TranslateTransition(
                Duration.millis(180),
                pokemon
        );

        rebote.setFromY(0);
        rebote.setToY(-15);
        rebote.setCycleCount(2);
        rebote.setAutoReverse(true);

        return rebote;
    }

    public void animarGolpe(ImageView spriteAfectado) {
        if (spriteAfectado == null) {
            return;
        }

        TranslateTransition sacudida = new TranslateTransition(
                Duration.millis(50),
                spriteAfectado
        );

        sacudida.setFromX(0);
        sacudida.setByX(10);
        sacudida.setCycleCount(6);
        sacudida.setAutoReverse(true);

        sacudida.setOnFinished(evento ->
                spriteAfectado.setTranslateX(0)
        );

        sacudida.play();
    }
}
