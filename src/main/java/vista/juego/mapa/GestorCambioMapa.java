
package vista.juego.mapa;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Jugador;
import vista.juego.VistaJugador;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Administra los cambios entre mapas durante la exploración.
 *
 * Ejecuta la transición visual, reemplaza la vista del mapa,
 * actualiza la posición del jugador y notifica los cambios
 * necesarios al controlador del juego.
 */
public class GestorCambioMapa {

    private final StackPane root;
    private final Pane mundo;
    private final Jugador jugador;
    private final VistaJugador vistaJugador;

    private final Runnable actualizarCamara;
    private final Consumer<VistaMapa> actualizarControlador; // Esta línea declara un atributo que almacena una acción que recibe un objeto VistaMapa y no devuelve ningún resultado.
    /*
    ¿Por qué se utiliza Consumer y no un método normal?
    Porque permite que GestorCambioMapa reciba una acción desde otra clase, sin necesitar conocer exactamente cómo está implementada.

    Es decir, el gestor sabe que tiene que avisar al controlador sobre la nueva vista, pero no necesita saber qué instrucciones utiliza el controlador para actualizarse.
     */

    private VistaMapa vistaMapa;
    private List<TransicionMapa> transiciones = new ArrayList<>();

    private boolean cambiando;

    /**
     * Construye el gestor de cambios de mapa.
     *
     * @param root pantalla principal
     * @param mundo contenedor de los elementos del mapa
     * @param jugador jugador que se desplaza entre mapas
     * @param vistaJugador representación visual del jugador
     * @param vistaMapa vista inicial del mapa
     * @param actualizarCamara acción para actualizar la cámara
     * @param actualizarControlador acción para actualizar el controlador
     */
    public GestorCambioMapa(
            StackPane root,
            Pane mundo,
            Jugador jugador,
            VistaJugador vistaJugador,
            VistaMapa vistaMapa,
            Runnable actualizarCamara,
            Consumer<VistaMapa> actualizarControlador
    ) {
        this.root = root;
        this.mundo = mundo;
        this.jugador = jugador;
        this.vistaJugador = vistaJugador;
        this.vistaMapa = vistaMapa;
        this.actualizarCamara = actualizarCamara;
        this.actualizarControlador = actualizarControlador;

        actualizarTransiciones(vistaMapa.getCargador());
    }

    /**
     * Inicia el cambio de mapa sin una acción adicional al finalizar.
     *
     * @param nuevoMapa mapa al que se quiere acceder
     * @param nuevaX coordenada horizontal de aparición
     * @param nuevaY coordenada vertical de aparición
     */
    public void cambiarMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY
    ) {
        cambiarMapa(nuevoMapa, nuevaX, nuevaY, null);
    }

    /**
     * Inicia el cambio de mapa con una transición de pantalla.
     *
     * @param nuevoMapa mapa al que se quiere acceder
     * @param nuevaX coordenada horizontal de aparición
     * @param nuevaY coordenada vertical de aparición
     * @param alTerminar acción que se ejecuta al finalizar la transición
     */
    public void cambiarMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY,
            Runnable alTerminar
    ) {

        if (cambiando || nuevoMapa == null) {
            return;
        }

        cambiando = true;

        Rectangle pantallaNegra = new Rectangle();
        pantallaNegra.setFill(Color.BLACK);
        pantallaNegra.setMouseTransparent(true);
        pantallaNegra.setOpacity(0);

        pantallaNegra.widthProperty().bind(root.widthProperty());
        pantallaNegra.heightProperty().bind(root.heightProperty());

        root.getChildren().add(pantallaNegra);

        FadeTransition oscurecer = new FadeTransition(
                Duration.millis(250),
                pantallaNegra
        );

        oscurecer.setFromValue(0);
        oscurecer.setToValue(1);

        oscurecer.setOnFinished(evento -> {

            aplicarCambioMapa(nuevoMapa, nuevaX, nuevaY);

            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(250),
                    pantallaNegra
            );

            aparecer.setFromValue(1);
            aparecer.setToValue(0);

            aparecer.setOnFinished(fin -> {

                root.getChildren().remove(pantallaNegra);

                pantallaNegra.widthProperty().unbind();
                pantallaNegra.heightProperty().unbind();

                cambiando = false;

                if (alTerminar != null) {
                    alTerminar.run();
                }
            });

            aparecer.play();
        });

        oscurecer.play();
    }

    /**
     * Reemplaza el mapa actual y actualiza los elementos asociados.
     *
     * @param nuevoMapa nuevo mapa
     * @param nuevaX posición horizontal del jugador
     * @param nuevaY posición vertical del jugador
     */
    private void aplicarCambioMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY
    ) {

        vistaMapa = new VistaMapa(nuevoMapa);

        jugador.setX(nuevaX);
        jugador.setY(nuevaY);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);

        mundo.setPrefSize(
                vistaMapa.getPrefWidth(),
                vistaMapa.getPrefHeight()
        );

        actualizarTransiciones(nuevoMapa);

        actualizarControlador.accept(vistaMapa);
        actualizarCamara.run();
    }

    /**
     * Actualiza las transiciones disponibles en el mapa actual.
     *
     * @param mapa mapa cuyas transiciones se consultan
     */
    private void actualizarTransiciones(CargadorMapaTiled mapa) {
        transiciones = mapa.obtenerTransiciones();
    }

    /**
     * Comprueba si el jugador ingresó en alguna zona de transición.
     * Si encuentra una, inicia el cambio al mapa correspondiente.
     */
    public void verificarTransicionMapa() {

        if (cambiando) {
            return;
        }

        for (TransicionMapa transicion : transiciones) {

            if (transicion.jugadorEntro(
                    jugador.getX(),
                    jugador.getY()
            )) {

                cambiarMapa(
                        transicion.getMapaDestino(),
                        transicion.getSpawnX(),
                        transicion.getSpawnY()
                );

                return;
            }
        }
    }

    /**
     * Devuelve la vista del mapa que está activa.
     *
     * @return vista del mapa actual
     */
    public VistaMapa getVistaMapa() {
        return vistaMapa;
    }

    /**
     * Indica si se está realizando un cambio de mapa.
     *
     * @return true si hay una transición en curso
     */
    public boolean estaCambiando() {
        return cambiando;
    }
}
