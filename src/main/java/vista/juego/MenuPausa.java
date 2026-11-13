
package vista.juego;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Jugador;
import vista.gestor.GestorPantallas;

/**
 * Gestiona el menú de pausa de la partida.
 *
 * Permite reanudar el juego, guardar la partida y volver
 * al menú principal después de confirmar la salida.
 */
public class MenuPausa {

    private final Jugador jugador;
    private final GestorPantallas pantallas;
    private final Runnable detenerJuego;
    private final Runnable reanudarJuego;

    private final StackPane vista;
    private final Label mensaje;
    private final Button confirmarSalida;
    private final Button cancelarSalida;

    private boolean abierto;

    private Timeline temporizadorMensaje;
    private FadeTransition animacionMensaje;

    /**
     * Construye el menú de pausa y configura sus acciones.
     *
     *  jugador jugador cuya partida se administra
     *  pantallas gestor de navegación
     *  detenerJuego acción para detener el ciclo del juego
     *  reanudarJuego acción para reanudar el ciclo del juego
     */
    public MenuPausa(
            Jugador jugador,
            GestorPantallas pantallas,
            Runnable detenerJuego,
            Runnable reanudarJuego
    ) {
        this.jugador = jugador;
        this.pantallas = pantallas;
        this.detenerJuego = detenerJuego;
        this.reanudarJuego = reanudarJuego;

        this.mensaje = new Label();
        this.confirmarSalida = new Button("SÍ, SALIR");
        this.cancelarSalida = new Button("CANCELAR");

        this.vista = crearVista();
    }

    /**
     * Construye visualmente el menú de pausa.
     *
     * @return panel del menú
     */
    private StackPane crearVista() {

        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("fondo-menu-pausa");
        overlay.setPickOnBounds(true);

        VBox contenido = new VBox(20);
        contenido.getStyleClass().add("contenido-menu-pausa");
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(35));
        contenido.setMaxWidth(420);

        Label titulo = new Label("MENÚ DE PAUSA");
        titulo.getStyleClass().add("titulo-menu-pausa");

        Button continuar = crearBoton("VOLVER AL JUEGO");
        Button guardar = crearBoton("GUARDAR PARTIDA");
        Button volverMenu = crearBoton("VOLVER AL MENÚ");

        mensaje.setWrapText(true);
        mensaje.setMaxWidth(350);
        mensaje.setMinHeight(35);
        mensaje.setAlignment(Pos.CENTER);
        mensaje.getStyleClass().add("mensaje-menu-pausa");

        configurarBoton(confirmarSalida);
        configurarBoton(cancelarSalida);

        continuar.setOnAction(e -> cerrar());
        guardar.setOnAction(e -> guardarPartida());
        volverMenu.setOnAction(e -> confirmarVolverAlMenu());

        confirmarSalida.setOnAction(e -> {
            detenerAnimaciones();
            pantallas.mostrarInicio();
        });

        cancelarSalida.setOnAction(e -> {
            detenerAnimaciones();
            mensaje.setText("");
            mensaje.setOpacity(1);
            ocultarConfirmacion();
        });

        ocultarConfirmacion();

        contenido.getChildren().addAll(
                titulo,
                continuar,
                guardar,
                volverMenu,
                mensaje,
                confirmarSalida,
                cancelarSalida
        );

        overlay.getChildren().add(contenido);
        overlay.setVisible(false);
        overlay.setManaged(false);

        return overlay;
    }

    /**
     * Crea un botón con el estilo general del juego.
     *
     * @param texto texto del botón
     * @return botón configurado
     */
    private Button crearBoton(String texto) {
        Button boton = new Button(texto);
        configurarBoton(boton);
        return boton;
    }

    /**
     * Aplica las propiedades comunes a los botones.
     *
     * @param boton botón que se configura
     */
    private void configurarBoton(Button boton) {
        boton.getStyleClass().add("boton-retro");
        boton.setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * Alterna entre abrir y cerrar el menú.
     */
    public void alternar() {
        if (abierto) {
            cerrar();
        } else {
            abrir();
        }
    }

    /**
     * Abre el menú y detiene el juego.
     */
    public void abrir() {

        if (abierto) {
            return;
        }

        abierto = true;
        detenerJuego.run();

        vista.setManaged(true);
        vista.setVisible(true);
    }

    /**
     * Cierra el menú y reanuda el juego.
     */
    public void cerrar() {

        if (!abierto) {
            return;
        }

        abierto = false;

        detenerAnimaciones();
        mensaje.setText("");
        mensaje.setOpacity(1);

        ocultarConfirmacion();

        vista.setVisible(false);
        vista.setManaged(false);

        reanudarJuego.run();
    }

    /**
     * Guarda la partida actual.
     */
    private void guardarPartida() {

        detenerAnimaciones();

        try {
            pantallas.guardarPartida(jugador);
            mostrarMensaje("¡PARTIDA GUARDADA CORRECTAMENTE!", false);

        } catch (Exception ex) {
            mostrarMensaje("ERROR AL GUARDAR LA PARTIDA.", true);
            ex.printStackTrace();
        }
    }

    /**
     * Solicita confirmación antes de abandonar la partida.
     */
    private void confirmarVolverAlMenu() {

        detenerAnimaciones();

        mensaje.setText("¿QUERÉS ABANDONAR LA PARTIDA?");
        mensaje.getStyleClass().remove("mensaje-error");
        mensaje.getStyleClass().remove("mensaje-exito");
        mensaje.getStyleClass().add("mensaje-advertencia");

        mensaje.setOpacity(0);

        animacionMensaje = new FadeTransition(
                Duration.millis(300),
                mensaje
        );

        animacionMensaje.setFromValue(0);
        animacionMensaje.setToValue(1);
        animacionMensaje.play();

        confirmarSalida.setVisible(true);
        confirmarSalida.setManaged(true);

        cancelarSalida.setVisible(true);
        cancelarSalida.setManaged(true);
    }

    /**
     * Muestra un mensaje temporal de éxito o error.
     *
     * @param texto contenido del mensaje
     * @param error indica si se trata de un error
     */
    private void mostrarMensaje(String texto, boolean error) {

        detenerAnimaciones();

        mensaje.setText(texto);

        mensaje.getStyleClass().remove("mensaje-error");
        mensaje.getStyleClass().remove("mensaje-exito");
        mensaje.getStyleClass().remove("mensaje-advertencia");

        mensaje.getStyleClass().add(
                error ? "mensaje-error" : "mensaje-exito"
        );

        mensaje.setOpacity(0);

        animacionMensaje = new FadeTransition(
                Duration.millis(250),
                mensaje
        );

        animacionMensaje.setFromValue(0);
        animacionMensaje.setToValue(1);
        animacionMensaje.play();

        temporizadorMensaje = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {

                    animacionMensaje = new FadeTransition(
                            Duration.millis(350),
                            mensaje
                    );

                    animacionMensaje.setFromValue(1);
                    animacionMensaje.setToValue(0);

                    animacionMensaje.setOnFinished(evento -> {
                        mensaje.setText("");
                        mensaje.setOpacity(1);
                    });

                    animacionMensaje.play();
                })
        );

        temporizadorMensaje.play();
    }

    /**
     * Detiene las animaciones y temporizadores activos.
     */
    private void detenerAnimaciones() {

        if (temporizadorMensaje != null) {
            temporizadorMensaje.stop();
            temporizadorMensaje = null;
        }

        if (animacionMensaje != null) {
            animacionMensaje.stop();
            animacionMensaje = null;
        }
    }

    /**
     * Oculta los botones de confirmación de salida.
     */
    private void ocultarConfirmacion() {
        confirmarSalida.setVisible(false);
        confirmarSalida.setManaged(false);

        cancelarSalida.setVisible(false);
        cancelarSalida.setManaged(false);
    }

    /**
     * Devuelve la vista del menú para incorporarla a la pantalla.
     *
     * @return panel de pausa
     */
    public StackPane getVista() {
        return vista;
    }

    /**
     * Indica si el menú está abierto.
     *
     * @return true si está abierto
     */
    public boolean estaAbierto() {
        return abierto;
    }
}
