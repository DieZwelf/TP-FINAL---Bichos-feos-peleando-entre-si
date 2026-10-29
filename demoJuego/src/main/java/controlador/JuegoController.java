
package controlador;

import model.Jugador;
import vista.VistaJugador;
import vista.VistaJuego;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class JuegoController {

    private final Jugador jugador;
    private final VistaJugador vista;
    private final VistaJuego vistaJuego;
    private long ultimoTiempoIdle = 0;

    private boolean arriba, abajo, izquierda, derecha;
    private long ultimoTiempoAnimacion = 0;

    private static final long INTERVALO_ANIMACION = 120_000_000L;
    private final List<Shape> colisiones;

    public JuegoController(
            Jugador jugador,
            VistaJugador vista,
            VistaJuego vistaJuego,
            Scene escena,
            List<Shape> colisiones) {

        this.jugador = jugador;
        this.vista = vista;
        this.vistaJuego = vistaJuego;
        this.colisiones = colisiones;

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
        double xAnterior = jugador.getX();
        double yAnterior = jugador.getY();

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

        if (hayColision(jugador.getX(), jugador.getY())) {
            jugador.setX(xAnterior);
            jugador.setY(yAnterior);
            seMovio = false;
        }

        jugador.setEnMovimiento(seMovio);

        if (seMovio) {
            if (now - ultimoTiempoAnimacion >= INTERVALO_ANIMACION) {
                jugador.setFrameActual(jugador.getFrameActual() + 1);
                ultimoTiempoAnimacion = now;
            }

            // Reinicia el reloj del idle al caminar.
            ultimoTiempoIdle = now;

        } else {
            // El idle alterna entre sus dos imágenes.
            if (ultimoTiempoIdle == 0) {
                ultimoTiempoIdle = now;
            }

            if (now - ultimoTiempoIdle >= 500_000_000L) {
                jugador.setFrameActual(jugador.getFrameActual() + 1);
                ultimoTiempoIdle = now;
            }
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


    private boolean hayColision(double nuevoX, double nuevoY) {

        // Zona de colisión del jugador: solamente los pies.
        double anchoJugador = 24;
        double altoJugador = 16;

        double desplazamientoX = (48 - anchoJugador) / 2;
        double desplazamientoY = 48 - altoJugador;

        Rectangle jugadorProximo = new Rectangle(
                nuevoX + desplazamientoX,
                nuevoY + desplazamientoY,
                anchoJugador,
                altoJugador
        );

        for (Shape obstaculo : colisiones) {

            Shape interseccion = Shape.intersect(
                    jugadorProximo,
                    obstaculo
            );

            Bounds limites = interseccion.getBoundsInLocal();

            if (limites.getWidth() > 0
                    && limites.getHeight() > 0) {
                return true;
            }
        }

        return false;
    }


}
