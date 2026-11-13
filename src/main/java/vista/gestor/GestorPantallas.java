
package vista.gestor;

import controlador.CombateViewModel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.*;
import vista.base.PantallaBase;
import vista.equipo.PantallaEquipo;
import vista.equipo.PantallaPokemonesCapturados;
import vista.inventario.PantallaInventario;
import vista.juego.VistaJuego;
import vista.juego.mapa.CargadorMapaTiled;
import vista.menu.PantallaCargarPartida;
import vista.menu.PantallaInicio;
import vista.menu.PantallaLogin;
import vista.menu.PantallaPartidaNueva;
import vista.combate.PantallaResultados;

/**
 * Coordina la navegación entre las pantallas del juego.
 *
 * Delega la administración de ventanas, partidas, combates y video
 * a gestores especializados.
 */
public class GestorPantallas {

    private final Stage stage;

    private final GestorVentana gestorVentana;
    private final GestorVideoMenu gestorVideoMenu;
    private final GestorPartidas gestorPartidas;
    private final GestorCombate gestorCombate;

    private PantallaBase pantallaActual;
    private PantallaBase pantallaAnteriorEquipo;
    private PantallaBase pantallaAnteriorInventario;
    private PantallaBase pantallaAnteriorCapturados;
    private PantallaBase pantallaMapaAnterior;

    private Jugador jugador;
    private VistaJuego vistaJuego;

    private TipoJugador tipo = TipoJugador.USUARIO;
    private boolean modoAdministrador;

    /**
     * Inicializa el gestor principal y sus gestores auxiliares.
     *
     * @param stage ventana principal
     */
    public GestorPantallas(Stage stage) {
        this.stage = stage;

        gestorVentana = new GestorVentana(stage);
        gestorVideoMenu = new GestorVideoMenu(this);
        gestorPartidas = new GestorPartidas(this);
        gestorCombate = new GestorCombate(this);

        gestorVentana.configurarVentana();
    }

    // -----------------------------------------------------
    // MOSTRAR Y RESTAURAR PANTALLAS
    // -----------------------------------------------------

    /**
     * Muestra una pantalla y actualiza su ciclo de vida.
     */
    public void mostrarPantalla(PantallaBase pantalla, Scene escena) {

        if (pantallaActual != null && pantallaActual != pantalla) {
            pantallaActual.alOcultar();
        }

        pantallaActual = pantalla;

        stage.setScene(escena);

        escena.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.F11) {
                alternarPantallaCompleta();
            }
        });

        stage.show();

        gestorVideoMenu.actualizar(pantalla);

        pantalla.alMostrar();
    }

    /**
     * Restaura una pantalla creada anteriormente.
     */
    private void restaurarPantalla(PantallaBase pantalla) {

        if (pantalla == null || pantalla.getEscena() == null) {
            mostrarInicio();
            return;
        }

        if (pantallaActual != null && pantallaActual != pantalla) {
            pantallaActual.alOcultar();
        }

        pantallaActual = pantalla;

        stage.setScene(pantalla.getEscena());
        stage.show();

        gestorVideoMenu.actualizar(pantalla);

        pantalla.alMostrar();
    }

    // -----------------------------------------------------
    // NAVEGACIÓN DE MENÚS
    // -----------------------------------------------------

    public void mostrarLogin() {
        new PantallaLogin(this).mostrar(stage);
    }

    public void mostrarInicio() {
        new PantallaInicio(this).mostrar(stage);
    }

    public void mostrarNuevaPartida() {
        new PantallaPartidaNueva(this).mostrar(stage);
    }

    public void mostrarCargarPartida() {
        new PantallaCargarPartida(this).mostrar(stage);
    }

    // -----------------------------------------------------
    // EQUIPO
    // -----------------------------------------------------

    public void mostrarEquipo() {

        if (jugador == null) {
            return;
        }

        pantallaAnteriorEquipo = pantallaActual;

        new PantallaEquipo(this, jugador).mostrar(stage);
    }

    public void volverDesdeEquipo() {
        restaurarPantalla(pantallaAnteriorEquipo);
        pantallaAnteriorEquipo = null;
    }

    // -----------------------------------------------------
    // INVENTARIO
    // -----------------------------------------------------

    public void mostrarInventario() {

        if (jugador == null) {
            return;
        }

        pantallaAnteriorInventario = pantallaActual;

        new PantallaInventario(this, jugador, null).mostrar(stage);
    }

    public void mostrarInventario(CombateViewModel viewModel) {

        if (jugador == null || viewModel == null) {
            return;
        }

        pantallaAnteriorInventario = pantallaActual;

        new PantallaInventario(this, jugador, viewModel).mostrar(stage);
    }

    public void volverDesdeInventario() {
        restaurarPantalla(pantallaAnteriorInventario);
        pantallaAnteriorInventario = null;
    }

    // -----------------------------------------------------
    // POKÉMON CAPTURADOS
    // -----------------------------------------------------

    public void mostrarPokemonesCapturados() {

        if (jugador == null) {
            return;
        }

        pantallaAnteriorCapturados = pantallaActual;

        new PantallaPokemonesCapturados(this, jugador).mostrar(stage);
    }

    public void volverDesdePokemonesCapturados() {
        restaurarPantalla(pantallaAnteriorCapturados);
        pantallaAnteriorCapturados = null;
    }

    // -----------------------------------------------------
    // PARTIDAS
    // -----------------------------------------------------

    public void cargarPartida(int jugadorId) {
        gestorPartidas.cargarPartida(jugadorId);
    }

    public void guardarPartida(Jugador jugador) {
        gestorPartidas.guardarPartida(jugador);
    }

    public boolean eliminarPartida(int jugadorId) {
        return gestorPartidas.eliminarPartida(jugadorId);
    }

    public void iniciarPartida() {
        gestorPartidas.iniciarPartida();
    }

    /**
     * Recibe un jugador y abre su mapa.
     * Método interno utilizado por GestorPartidas.
     */
    void abrirMapa(Jugador jugador) {

        this.jugador = jugador;

        vistaJuego = new VistaJuego(jugador, this);
        vistaJuego.mostrar(stage);
    }

    // -----------------------------------------------------
    // COMBATE
    // -----------------------------------------------------

    public void iniciarCombate(Jugador entrenadorJugador, Pokemon pokemon) {
        gestorCombate.iniciarCombate(entrenadorJugador, pokemon);
    }

    public void regresarAlMapa() {
        gestorCombate.regresarAlMapa();
    }

    public void mostrarResultados(CombateViewModel viewModel) {
        new PantallaResultados(viewModel, this).mostrar(stage);
    }

    /**
     * Abre la pantalla de combate.
     * Método interno utilizado por GestorCombate.
     */
    void abrirPantallaCombate(
            Jugador entrenadorJugador,
            Pokemon pokemonSalvaje
    ) {
        gestorCombate.mostrarPantallaCombate(
                entrenadorJugador,
                pokemonSalvaje
        );
    }

    // -----------------------------------------------------
    // CAMBIAR MAPA
    // -----------------------------------------------------

    public void cambiarMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY
    ) {

        if (vistaJuego == null) {
            return;
        }

        vistaJuego.cambiarMapa(nuevoMapa, nuevaX, nuevaY);
    }

    // -----------------------------------------------------
    // VENTANA Y SALIDA
    // -----------------------------------------------------

    public void salir() {

        if (pantallaActual != null) {
            pantallaActual.alOcultar();
        }

        gestorVideoMenu.liberar();
        gestorVentana.cerrar();
    }

    public void alternarPantallaCompleta() {
        gestorVentana.alternarPantallaCompleta();
    }

    // -----------------------------------------------------
    // VIDEO DEL MENÚ
    // -----------------------------------------------------

    public javafx.scene.media.MediaPlayer obtenerReproductorMenu() {
        return gestorVideoMenu.obtenerReproductor();
    }

    // -----------------------------------------------------
    // GETTERS Y SETTERS
    // -----------------------------------------------------

    public TipoJugador getTipo() {
        return tipo;
    }

    public void setTipo(TipoJugador tipo) {
        this.tipo = tipo;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setEntrenador(Jugador jugador) {
        this.jugador = jugador;
    }

    public boolean isModoAdministrador() {
        return modoAdministrador;
    }

    public void setModoAdministrador(boolean modoAdministrador) {
        this.modoAdministrador = modoAdministrador;
    }

    // -----------------------------------------------------
    // ACCESOS INTERNOS PARA LOS GESTORES
    // -----------------------------------------------------

    PantallaBase getPantallaActualInterna() {
        return pantallaActual;
    }

    Stage getStageInterno() {
        return stage;
    }

    VistaJuego getVistaJuegoInterna() {
        return vistaJuego;
    }

    void setPantallaMapaAnterior(PantallaBase pantalla) {
        this.pantallaMapaAnterior = pantalla;
    }

    PantallaBase getPantallaMapaAnterior() {
        return pantallaMapaAnterior;
    }

    void limpiarPantallaMapaAnterior() {
        pantallaMapaAnterior = null;
    }
    void restaurarMapaDesdeCombate() {
        restaurarPantalla(pantallaMapaAnterior);
        pantallaMapaAnterior = null;
    }
}
