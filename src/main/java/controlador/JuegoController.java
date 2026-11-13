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

/**
 * La clase 'JuegoController' controla las entradas del jugador, el movimiento, las colisiones,
 * las animaciones y la generación de encuentros aleatorios durante la partida.
 * Métodos: setOnEncuentroGenerado(), reanudarGameLoop(), detenerGameLoop(), crearGameLoop(),
 * actualizarLogica(), moverJugador(), actualizarAnimacionCaminata(), actualizarAnimacionQuieto(),
 * verificarEncuentro(), manejarTeclaPresionada(), manejarTeclaLiberada(), cambiarEstadoTecla(),
 * limpiarTeclas(), hayColision(), getColisiones(), setColisiones().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class JuegoController {

    private static final long INTERVALO_ANIMACION = 120_000_000L;
    private static final long INTERVALO_IDLE = 500_000_000L;
    private static final long INTERVALO_VERIFICACION = 3_000_000_000L;
    private static final double PROBABILIDAD_ENCUENTRO = 0.99;

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

    /**
     * Construye el controlador del juego y establece las referencias al jugador y a las vistas.
     *
     * @param jugador jugador cuyo movimiento será controlado
     * @param vista vista encargada de representar al jugador
     * @param vistaJuego vista principal del juego
     * @param colisiones lista de figuras que representan los obstáculos del mapa
     */
    public JuegoController(Jugador jugador, VistaJugador vista,
                           VistaJuego vistaJuego, List<Shape> colisiones) {
        this.jugador = jugador;
        this.vista = vista;
        this.vistaJuego = vistaJuego;
        this.colisiones = colisiones;
    }

    /**
     * Establece la función que se ejecutará cuando se genere un encuentro.
     *
     * @param callback función que se ejecutará al producirse un encuentro
     */
    public void setOnEncuentroGenerado(Runnable callback) {
        this.onEncuentroGenerado = callback;
    }

    /**
     * Reanuda el ciclo principal del juego.
     * Si todavía no existe, primero crea el GameLoop.
     */
    public void reanudarGameLoop() {
        if (gameLoop == null) {
            crearGameLoop();
        }
        gameLoop.start();
    }

    /**
     * Detiene el ciclo principal del juego, limpia las teclas presionadas
     * y establece que el jugador ya no se encuentra en movimiento.
     */
    public void detenerGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        limpiarTeclas();
        jugador.setEnMovimiento(false);
    }

    /**
     * Crea el ciclo principal del juego mediante un AnimationTimer.
     * En cada actualización ejecuta la lógica, renderiza al jugador y actualiza la cámara.
     */
    private void crearGameLoop() {
        gameLoop = new AnimationTimer() {
            /**
             * Ejecuta las actualizaciones correspondientes a cada ciclo del AnimationTimer.
             *
             * @param now instante actual proporcionado por el AnimationTimer
             */
            @Override
            public void handle(long now) {
                actualizarLogica(now);
                vista.renderizar(jugador);
                vistaJuego.actualizarCamara();
            }
        };
    }

    /**
     * Actualiza la lógica general del juego.
     * Controla el movimiento, las colisiones, las animaciones y la generación de encuentros.
     *
     * @param ahora instante actual utilizado para controlar los intervalos de tiempo
     */
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

        if (seMovio) {
            vistaJuego.verificarTransicionMapa();
        }

        if (seMovio) {
            System.out.println(
                    "Jugador X: " + jugador.getX() +
                            " | Jugador Y: " + jugador.getY()
            );
        }

    }

    /**
     * Mueve al jugador según la dirección cuya tecla se encuentre presionada.
     *
     * @return true si se ejecutó un movimiento; false si no hay ninguna dirección activa
     */
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

    /**
     * Actualiza la animación de caminata cuando transcurre el intervalo establecido.
     *
     * @param ahora instante actual del ciclo de juego
     */
    private void actualizarAnimacionCaminata(long ahora) {
        if (ahora - ultimoTiempoAnimacion >= INTERVALO_ANIMACION) {
            jugador.setFrameActual(jugador.getFrameActual() + 1);
            ultimoTiempoAnimacion = ahora;
        }
        ultimoTiempoIdle = ahora;
    }

    /**
     * Actualiza la animación del jugador cuando permanece quieto.
     *
     * @param ahora instante actual del ciclo de juego
     */
    private void actualizarAnimacionQuieto(long ahora) {
        if (ultimoTiempoIdle == 0) ultimoTiempoIdle = ahora;
        if (ahora - ultimoTiempoIdle >= INTERVALO_IDLE) {
            jugador.setFrameActual(jugador.getFrameActual() + 1);
            ultimoTiempoIdle = ahora;
        }
    }

    /**
     * Verifica si transcurrió el intervalo necesario para generar un encuentro aleatorio.
     * Si se cumple la probabilidad establecida y existe un callback, lo ejecuta.
     *
     * @param ahora instante actual del ciclo de juego
     */
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

    /**
     * Procesa una tecla presionada por el usuario y actualiza su estado.
     *
     * @param evento evento de teclado recibido
     */
    public void manejarTeclaPresionada(KeyEvent evento) {
        cambiarEstadoTecla(evento.getCode(), true);
    }

    /**
     * Procesa una tecla liberada por el usuario y actualiza su estado.
     *
     * @param evento evento de teclado recibido
     */
    public void manejarTeclaLiberada(KeyEvent evento) {
        cambiarEstadoTecla(evento.getCode(), false);
    }

    /**
     * Actualiza el estado de la dirección correspondiente a una tecla.
     *
     * @param codigo código de la tecla presionada o liberada
     * @param presionada indica si la tecla se encuentra presionada
     */
    private void cambiarEstadoTecla(KeyCode codigo, boolean presionada) {
        switch (codigo) {
            case W, UP -> arriba = presionada;
            case S, DOWN -> abajo = presionada;
            case A, LEFT -> izquierda = presionada;
            case D, RIGHT -> derecha = presionada;
            default -> { }
        }
    }

    /**
     * Restablece el estado de todas las teclas de movimiento.
     */
    private void limpiarTeclas() {
        arriba = false;
        abajo = false;
        izquierda = false;
        derecha = false;
    }

    /**
     * Comprueba si la posición propuesta para el jugador intersecta algún obstáculo.
     *
     * @param nuevoX coordenada horizontal propuesta para el jugador
     * @param nuevoY coordenada vertical propuesta para el jugador
     * @return true si existe una colisión; false en caso contrario
     */
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

    /**
     * Obtiene la lista de figuras utilizadas para detectar colisiones.
     *
     * @return lista de obstáculos del mapa
     */
    public List<Shape> getColisiones() {
        return colisiones;
    }

    /**
     * Establece la lista de figuras utilizadas para detectar colisiones.
     *
     * @param colisiones nueva lista de obstáculos del mapa
     */
    public void setColisiones(List<Shape> colisiones) {
        this.colisiones = colisiones;
    }
}