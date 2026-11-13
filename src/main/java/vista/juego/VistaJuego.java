
package vista.juego;

import controlador.JuegoController;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import model.*;
import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;
import vista.juego.animaciones.AnimacionEncuentro;
import vista.juego.mapa.CargadorMapaTiled;
import vista.juego.mapa.GestorCambioMapa;
import vista.juego.mapa.MapaCasa;
import vista.juego.mapa.VistaMapa;

/**
 * La clase VISTAJUEGO representa la pantalla principal de exploración.
 * Coordina el mapa, el jugador, la cámara, los controles y los
 * componentes auxiliares de pausa, administración y cambios de mapa.
 * Tiene un constructor que recibe al jugador y al gestor de pantallas.
 * Otros métodos: crearContenido(), configurarEscena(), crearTransicionInicial(),
 * alMostrar(), alOcultar(), crearPanelJugador(), detenerGameLoop(),
 * reanudarGameLoop(), generarEncuentro(), animarEncuentro(),
 * mostrarErrorAdmin(), actualizarCamara(), limitarCamara(),
 * configurarTeclas(), cambiarMapa() y verificarTransicionMapa().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class VistaJuego extends PantallaBase {

    private static final double ZOOM = 1.4;

    private final Jugador jugador;
    private final VistaJugador vistaJugador;

    private VistaMapa vistaMapa;

    private final Pane mundo = new Pane();
    private final Affine transformacionCamara = new Affine();
    private final Rectangle recorte = new Rectangle();

    private StackPane root;
    private Scene escena;

    private JuegoController controlador;

    private MenuPausa menuPausa;
    private PanelAdministrador panelAdministrador;
    private GestorCambioMapa gestorCambioMapa;

    private boolean encuentroEnCurso;

    private Rectangle transicionInicial;

    /**
     * Construye la pantalla de exploración.
     * Inicializa el mapa inicial y la representación visual del jugador.
     *
     * @param jugador jugador de la partida
     * @param pantallas gestor de navegación entre pantallas
     */
    public VistaJuego(Jugador jugador, GestorPantallas pantallas) {
        super(pantallas);

        this.jugador = jugador;

        MapaCasa mapaInicial = new MapaCasa();

        this.vistaMapa = new VistaMapa(mapaInicial);
        this.vistaJugador = new VistaJugador(jugador);
    }

    /**
     * Construye los componentes visuales de la pantalla.
     * Inicializa el mundo, la cámara, los paneles auxiliares
     * y la transición inicial.
     *
     * @return contenedor principal
     */
    @Override
    protected Parent crearContenido() {

        root = new StackPane();
        root.getStyleClass().add("fondo-juego");
        root.setFocusTraversable(true);

        StackPane.setAlignment(mundo, Pos.TOP_LEFT);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);

        mundo.setPrefSize(
                vistaMapa.getPrefWidth(),
                vistaMapa.getPrefHeight()
        );

        mundo.getTransforms().setAll(transformacionCamara);

        root.getChildren().add(mundo);
        root.setClip(recorte);

        // Panel de acceso al menú de pausa.
        VBox panelJugador = crearPanelJugador();

        root.getChildren().add(panelJugador);

        StackPane.setAlignment(panelJugador, Pos.TOP_LEFT);
        StackPane.setMargin(panelJugador, new Insets(18));

        // Panel de herramientas del administrador.
        if (pantallas.isModoAdministrador()) {

            panelAdministrador = new PanelAdministrador(
                    jugador,
                    this::generarEncuentro,
                    this::cambiarMapa
            );

            root.getChildren().add(panelAdministrador.getVista());

            StackPane.setAlignment(
                    panelAdministrador.getVista(),
                    Pos.TOP_RIGHT
            );

            StackPane.setMargin(
                    panelAdministrador.getVista(),
                    new Insets(18)
            );
        }

        // Menú de pausa.
        menuPausa = new MenuPausa(
                jugador,
                pantallas,
                this::detenerGameLoop,
                this::reanudarGameLoop
        );

        root.getChildren().add(menuPausa.getVista());

        // Actualización del recorte y la cámara al cambiar el tamaño.
        root.widthProperty().addListener((obs, antes, ahora) -> {
            recorte.setWidth(ahora.doubleValue());
            actualizarCamara();
        });

        root.heightProperty().addListener((obs, antes, ahora) -> {
            recorte.setHeight(ahora.doubleValue());
            actualizarCamara();
        });

        crearTransicionInicial();

        return root;
    }

    /**
     * Configura el controlador del juego y los eventos de teclado.
     * También inicializa el gestor encargado de los cambios de mapa.
     *
     * @param escena escena JavaFX
     */
    @Override
    protected void configurarEscena(Scene escena) {

        agregarCSS(escena, "/images/pantallaMenu/estilo.css");

        this.escena = escena;

        controlador = new JuegoController(
                jugador,
                vistaJugador,
                this,
                vistaMapa.getColisiones()
        );

        controlador.setOnEncuentroGenerado(this::generarEncuentro);

        // Gestor encargado de las transiciones entre mapas.
        gestorCambioMapa = new GestorCambioMapa(
                root,
                mundo,
                jugador,
                vistaJugador,
                vistaMapa,
                this::actualizarCamara,
                nuevaVista -> {
                    vistaMapa = nuevaVista;
                    controlador.setColisiones(nuevaVista.getColisiones());
                }
        );

        configurarTeclas(escena);
    }

    /**
     * Crea la capa negra inicial que se desvanece al entrar al juego.
     * Ajusta sus dimensiones al tamaño del contenedor principal.
     */
    private void crearTransicionInicial() {

        transicionInicial = new Rectangle();

        transicionInicial.setFill(Color.BLACK);
        transicionInicial.setMouseTransparent(true);
        transicionInicial.setOpacity(1);

        transicionInicial.widthProperty().bind(root.widthProperty());
        transicionInicial.heightProperty().bind(root.heightProperty());

        root.getChildren().add(transicionInicial);
    }

    /**
     * Ejecuta las acciones correspondientes cuando se muestra esta pantalla.
     * Reanuda el ciclo del juego, actualiza la cámara y reproduce
     * la transición visual inicial cuando corresponde.
     */
    @Override
    public void alMostrar() {
        encuentroEnCurso = false;

        if (menuPausa == null || !menuPausa.estaAbierto()) {
            reanudarGameLoop();
        }

        actualizarCamara();
        root.requestFocus();

        if (transicionInicial != null) {

            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(700),
                    transicionInicial
            );

            aparecer.setFromValue(1);
            aparecer.setToValue(0);

            aparecer.setOnFinished(evento -> {

                root.getChildren().remove(transicionInicial);

                transicionInicial.widthProperty().unbind();
                transicionInicial.heightProperty().unbind();

                transicionInicial = null;
                root.requestFocus();
            });

            aparecer.play();
        }
    }

    /**
     * Detiene el juego cuando se abandona esta pantalla.
     */
    @Override
    public void alOcultar() {
        detenerGameLoop();
    }

    /**
     * Crea el botón que permite abrir el menú de pausa.
     * Configura su apariencia y su acción correspondiente.
     *
     * @return panel del jugador
     */
    private VBox crearPanelJugador() {

        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);

        Button menu = new Button("MENÚ");
        menu.getStyleClass().add("boton-retro");

        menu.setOnAction(e -> menuPausa.alternar());

        panel.getChildren().add(menu);

        panel.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        return panel;
    }

    /**
     * Detiene el ciclo de actualización del juego
     * mediante el controlador correspondiente.
     */
    private void detenerGameLoop() {

        if (controlador != null) {
            controlador.detenerGameLoop();
        }
    }

    /**
     * Reanuda el ciclo de actualización del juego
     * mediante el controlador correspondiente.
     */
    private void reanudarGameLoop() {

        if (controlador != null) {
            controlador.reanudarGameLoop();
        }
    }

    /**
     * Genera un encuentro con un Pokémon salvaje.
     * Comprueba que el mapa permita encuentros, que no exista
     * otro encuentro en curso y que el jugador tenga un Pokémon activo.
     */
    private void generarEncuentro() {

        if (vistaMapa.getCargador() instanceof MapaCasa) {
            mostrarErrorAdmin(
                    "No se pueden generar encuentros en este mapa."
            );
            return;
        }

        if (encuentroEnCurso) {
            return;
        }

        Pokemon pokemonActivo = jugador.getPokemonActivo();

        if (pokemonActivo == null) {
            mostrarErrorAdmin("Necesitás un Pokémon activo.");
            return;
        }

        double probabilidad =
                CalculadoraCaptura.calcularProbabilidad(pokemonActivo);

        System.out.println(
                "Vida: " + pokemonActivo.getVida()
                        + "/" + pokemonActivo.getVidaMaxima()
        );

        System.out.println(
                "Probabilidad: " + (probabilidad * 100) + "%"
        );

        Pokemon salvaje =
                GeneradorPokemonSalvaje.generarPokemonSegunNivel(
                        pokemonActivo.getNivel()
                );

        encuentroEnCurso = true;

        pantallas.iniciarCombate(jugador, salvaje);
    }

    /**
     * Ejecuta la animación visual previa a un encuentro.
     * Detiene el ciclo del juego durante la animación
     * y ejecuta la acción indicada al finalizar.
     *
     * @param alTerminar acción que se ejecuta al finalizar
     */
    public void animarEncuentro(Runnable alTerminar) {

        AnimacionEncuentro.reproducir(
                root,
                this::detenerGameLoop,
                alTerminar
        );
    }

    /**
     * Muestra un error en el panel de administración.
     * Si el panel no está disponible, imprime el mensaje por consola.
     *
     * @param texto mensaje de error
     */
    private void mostrarErrorAdmin(String texto) {

        if (panelAdministrador != null) {
            panelAdministrador.mostrarErrorDesdeJuego(texto);
        } else {
            System.out.println(texto);
        }
    }

    /**
     * Actualiza la cámara para mantener al jugador centrado.
     * Tiene en cuenta el tamaño de la ventana, las dimensiones del mapa
     * y el nivel de zoom establecido.
     */
    public void actualizarCamara() {

        if (escena == null || root == null || vistaMapa == null) {
            return;
        }

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();

        if (anchoVentana <= 0 || altoVentana <= 0) {
            return;
        }

        double anchoEscalado = vistaMapa.getPrefWidth() * ZOOM;
        double altoEscalado = vistaMapa.getPrefHeight() * ZOOM;

        double centroX = jugador.getX() + 24;
        double centroY = jugador.getY() + 24;

        double x = anchoVentana / 2 - centroX * ZOOM;
        double y = altoVentana / 2 - centroY * ZOOM;

        x = limitarCamara(x, anchoEscalado, anchoVentana);
        y = limitarCamara(y, altoEscalado, altoVentana);

        transformacionCamara.setToTransform(
                ZOOM, 0, x,
                0, ZOOM, y
        );
    }

    /**
     * Limita el desplazamiento de la cámara para evitar mostrar
     * el exterior del mapa.
     *
     * @param posicion posición calculada de la cámara
     * @param tamanoMapa tamaño escalado del mapa
     * @param tamanoVentana tamaño de la ventana
     * @return posición limitada
     */
    private double limitarCamara(
            double posicion,
            double tamanoMapa,
            double tamanoVentana
    ) {

        if (tamanoMapa >= tamanoVentana) {

            return Math.max(
                    tamanoVentana - tamanoMapa,
                    Math.min(0, posicion)
            );
        }

        return (tamanoVentana - tamanoMapa) / 2;
    }

    /**
     * Configura los controles de teclado.
     * Permite abrir el menú de pausa, controlar al jugador
     * y acceder a las pantallas de equipo, inventario y Pokémon capturados.
     *
     * @param escena escena donde se registran los eventos
     */
    private void configurarTeclas(Scene escena) {

        escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {

            if (evento.getCode() == KeyCode.ESCAPE) {
                menuPausa.alternar();
                evento.consume();
                return;
            }

            if (menuPausa.estaAbierto()) {
                evento.consume();
                return;
            }

            controlador.manejarTeclaPresionada(evento);

            if (evento.getCode() == KeyCode.TAB) {

                pantallas.mostrarEquipo();
                evento.consume();

            } else if (evento.getCode() == KeyCode.E) {

                pantallas.mostrarInventario();
                evento.consume();

            } else if (evento.getCode() == KeyCode.C) {

                pantallas.mostrarPokemonesCapturados();
                evento.consume();
            }
        });

        escena.addEventFilter(
                KeyEvent.KEY_RELEASED,
                evento -> {

                    if (!menuPausa.estaAbierto()) {
                        controlador.manejarTeclaLiberada(evento);
                    }
                }
        );
    }

    /**
     * Solicita un cambio de mapa al gestor correspondiente.
     * Detiene el ciclo del juego mientras se realiza la transición
     * y lo reanuda cuando finaliza.
     *
     * @param nuevoMapa mapa de destino
     * @param nuevaX posición horizontal de aparición
     * @param nuevaY posición vertical de aparición
     */
    public void cambiarMapa(CargadorMapaTiled nuevoMapa, double nuevaX, double nuevaY) {

        if (gestorCambioMapa == null ||
                gestorCambioMapa.estaCambiando()) {
            return;
        }

        detenerGameLoop();

        gestorCambioMapa.cambiarMapa(nuevoMapa, nuevaX, nuevaY, this::reanudarGameLoop);
    }

    /**
     * Comprueba si el jugador ingresó en una zona de transición.
     * Delega la comprobación al gestor de cambios de mapa.
     * Lo invoca el controlador durante la exploración.
     */
    public void verificarTransicionMapa() {

        if (gestorCambioMapa != null) {
            gestorCambioMapa.verificarTransicionMapa();
        }
    }
}