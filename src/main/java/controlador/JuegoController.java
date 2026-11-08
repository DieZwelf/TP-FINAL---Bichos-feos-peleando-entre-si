package controlador;

import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import model.Jugador;
import vista.juego.VistaJuego;
import vista.juego.VistaJugador;

import java.util.List;

/** Controla entradas, movimiento, colisiones, animación y encuentros aleatorios. */
public class JuegoController {

    private static final long INTERVALO_ANIMACION = 120_000_000L;
    private static final long INTERVALO_IDLE = 500_000_000L;
    private static final long INTERVALO_VERIFICACION = 3_000_000_000L;
    private static final double PROBABILIDAD_ENCUENTRO = 0.85;

    private final Jugador jugador;
    private final VistaJugador vista;
    private final VistaJuego vistaJuego;
    private List<Shape> colisiones;

    private boolean arriba;
    private boolean abajo;
    private boolean izquierda;
    private boolean derecha;

    private long ultimoTiempoIdle;
    private long ultimoTiempoAnimacion;
    private long ultimoTiempoEncuentro;
    private Runnable onEncuentroGenerado;
    private AnimationTimer gameLoop;

    public JuegoController(Jugador jugador, VistaJugador vista,
                           VistaJuego vistaJuego, List<Shape> colisiones) {
        this.jugador = jugador;
        this.vista = vista;
        this.vistaJuego = vistaJuego;
        this.colisiones = colisiones;
    }

    public void setOnEncuentroGenerado(Runnable callback) {
        this.onEncuentroGenerado = callback;
    }

    public void reanudarGameLoop() {
        if (gameLoop == null) {
            crearGameLoop();
        }
        gameLoop.start();
    }

    public void detenerGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        limpiarTeclas();
        jugador.setEnMovimiento(false);
    }

    private void crearGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                actualizarLogica(now);
                vista.renderizar(jugador);
                vistaJuego.actualizarCamara();
            }
        };
    }

    private void actualizarLogica(long ahora) {
        double xAnterior = jugador.getX();
        double yAnterior = jugador.getY();
        boolean seMovio = moverJugador();

        if (hayColision(jugador.getX(), jugador.getY())) {
            jugador.setX(xAnterior);
            jugador.setY(yAnterior);
            seMovio = false;
        }

        jugador.setEnMovimiento(seMovio);
        if (seMovio) {
            actualizarAnimacionCaminata(ahora);
            verificarEncuentro(ahora);
        } else {
            actualizarAnimacionQuieto(ahora);
            ultimoTiempoEncuentro = 0;
        }
    }

    private boolean moverJugador() {
        if (arriba) {
            jugador.moverArriba();
        } else if (abajo) {
            jugador.moverAbajo();
        } else if (izquierda) {
            jugador.moverIzquierda();
        } else if (derecha) {
            jugador.moverDerecha();
        } else {
            return false;
        }
        return true;
    }

    private void actualizarAnimacionCaminata(long ahora) {
        if (ahora - ultimoTiempoAnimacion >= INTERVALO_ANIMACION) {
            jugador.setFrameActual(jugador.getFrameActual() + 1);
            ultimoTiempoAnimacion = ahora;
        }
        ultimoTiempoIdle = ahora;
    }

    private void actualizarAnimacionQuieto(long ahora) {
        if (ultimoTiempoIdle == 0) ultimoTiempoIdle = ahora;
        if (ahora - ultimoTiempoIdle >= INTERVALO_IDLE) {
            jugador.setFrameActual(jugador.getFrameActual() + 1);
            ultimoTiempoIdle = ahora;
        }
    }

    private void verificarEncuentro(long ahora) {
        if (ultimoTiempoEncuentro == 0) {
            ultimoTiempoEncuentro = ahora;
            return;
        }

        if (ahora - ultimoTiempoEncuentro >= INTERVALO_VERIFICACION) {
            ultimoTiempoEncuentro = ahora;
            if (Math.random() < PROBABILIDAD_ENCUENTRO && onEncuentroGenerado != null) {
                onEncuentroGenerado.run();
            }
        }
    }

    public void manejarTeclaPresionada(KeyEvent evento) {
        cambiarEstadoTecla(evento.getCode(), true);
    }

    public void manejarTeclaLiberada(KeyEvent evento) {
        cambiarEstadoTecla(evento.getCode(), false);
    }

    private void cambiarEstadoTecla(KeyCode codigo, boolean presionada) {
        switch (codigo) {
            case W, UP -> arriba = presionada;
            case S, DOWN -> abajo = presionada;
            case A, LEFT -> izquierda = presionada;
            case D, RIGHT -> derecha = presionada;
            default -> { }
        }
    }

    private void limpiarTeclas() {
        arriba = false;
        abajo = false;
        izquierda = false;
        derecha = false;
    }

    private boolean hayColision(double nuevoX, double nuevoY) {
        double anchoJugador = 24;
        double altoJugador = 16;
        double desplazamientoX = (48 - anchoJugador) / 2;
        double desplazamientoY = 48 - altoJugador;

        Rectangle jugadorProximo = new Rectangle(nuevoX + desplazamientoX, nuevoY + desplazamientoY,
                anchoJugador, altoJugador);

        for (Shape obstaculo : colisiones) {
            Shape interseccion = Shape.intersect(jugadorProximo, obstaculo);
            Bounds limites = interseccion.getBoundsInLocal();
            if (limites.getWidth() > 0 && limites.getHeight() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<Shape> getColisiones() {
        return colisiones;
    }

    public void setColisiones(List<Shape> colisiones) {
        this.colisiones = colisiones;
    }
}
