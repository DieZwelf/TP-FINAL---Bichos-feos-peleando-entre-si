
package com.example.demojuego.controlador;

import com.example.demojuego.model.Jugador;
import com.example.demojuego.vista.VistaJugador;
import com.example.demojuego.vista.VistaJuego;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

public class JuegoController {

    private final Jugador jugador;
    private final VistaJugador vista;
    private final VistaJuego vistaJuego;

    private boolean arriba, abajo, izquierda, derecha;
    private long ultimoTiempoAnimacion = 0;

    private static final long INTERVALO_ANIMACION = 120_000_000L;

    public JuegoController(
            Jugador jugador,
            VistaJugador vista,
            VistaJuego vistaJuego,
            Scene escena) {

        this.jugador = jugador;
        this.vista = vista;
        this.vistaJuego = vistaJuego;

        escena.setOnKeyPressed(this::onKeyPressed);
        escena.setOnKeyReleased(this::onKeyReleased);
    }

    public void iniciarGameLoop() {
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarLogica(now);
                vista.renderizar(jugador);
                vistaJuego.actualizarCamara();
            }
        };

        loop.start();
    }

    private void actualizarLogica(long now) {
        boolean seMovio = false;

        if (arriba) {
            jugador.moverArriba();
            seMovio = true;
        } else if (abajo) {
            jugador.moverAbajo();
            seMovio = true;
        } else if (izquierda) {
            jugador.moverIzquierda();
            seMovio = true;
        } else if (derecha) {
            jugador.moverDerecha();
            seMovio = true;
        }

        jugador.setEnMovimiento(seMovio);

        if (seMovio) {
            if (now - ultimoTiempoAnimacion >= INTERVALO_ANIMACION) {
                jugador.setFrameActual(jugador.getFrameActual() + 1);
                ultimoTiempoAnimacion = now;
            }
        } else {
            jugador.setFrameActual(0);
        }
    }

    private void onKeyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case W, UP -> arriba = true;
            case S, DOWN -> abajo = true;
            case A, LEFT -> izquierda = true;
            case D, RIGHT -> derecha = true;
        }
    }

    private void onKeyReleased(KeyEvent e) {
        switch (e.getCode()) {
            case W, UP -> arriba = false;
            case S, DOWN -> abajo = false;
            case A, LEFT -> izquierda = false;
            case D, RIGHT -> derecha = false;
        }
    }
}
