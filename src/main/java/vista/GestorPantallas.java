
package vista;

import DAO.guardarPartidaDAO;
import controlador.CombateViewModel;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.*;
import servicio.PartidaService;
import vista.base.PantallaBase;
import vista.base.PantallaMenuBase;
import vista.combate.PantallaCombate;
import vista.combate.PantallaResultados;
import vista.equipo.PantallaEquipo;
import vista.equipo.PantallaPokemonesCapturados;
import vista.inventario.PantallaInventario;
import vista.juego.CargadorMapaTiled;
import vista.juego.VistaJuego;
import vista.menu.PantallaCargarPartida;
import vista.menu.PantallaInicio;
import vista.menu.PantallaLogin;
import vista.menu.PantallaPartidaNueva;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class GestorPantallas {

    private final Stage stage;
    private final PartidaService partidaService = new PartidaService();

    private PantallaBase pantallaActual;
    private PantallaBase pantallaAnteriorEquipo;
    private PantallaBase pantallaAnteriorInventario;
    private PantallaBase pantallaAnteriorCapturados;
    private PantallaBase pantallaMapaAnterior;

    private Jugador jugador;
    private boolean combateActivo;
    private VistaJuego vistaJuego;

    private TipoJugador tipo = TipoJugador.USUARIO;
    private boolean modoAdministrador = false;

    private guardarPartidaDAO partidaDAO;
    private MediaPlayer reproductorMenu;

    public GestorPantallas(Stage stage) {

        this.stage = stage;

        configurarVentana();

        this.partidaDAO = new guardarPartidaDAO();
    }

    // =====================================================
    // CARGAR PARTIDA
    // =====================================================

    public void cargarPartida(int jugadorId) {

        jugador = partidaDAO.recuperarJugador(jugadorId);

        if (jugador == null) {
            return;
        }

        // Posición inicial igual que en Nueva partida.
        jugador.setX(254);
        jugador.setY(177);

        vistaJuego = new VistaJuego(jugador, this);
        vistaJuego.mostrar(stage);
    }

    // =====================================================
    // GUARDAR PARTIDA
    // =====================================================

    public void guardarPartida(Jugador jugador) {

        partidaDAO.guardarJugador(jugador);
    }

    // =====================================================
    // ELIMINAR PARTIDA
    // =====================================================

    public boolean eliminarPartida(int jugadorId) {

        return partidaDAO.eliminarPartida(jugadorId);
    }

    // =====================================================
    // CONFIGURAR VENTANA
    // =====================================================

    private void configurarVentana() {

        stage.setTitle("Bichos feos");

        URL icono =
                getClass().getResource("/images/pantallaMenu/icono.png");

        if (icono != null) {
            stage.getIcons().setAll(new Image(icono.toExternalForm()));
        }

        stage.setWidth(1400);
        stage.setHeight(800);
        stage.setResizable(true);
    }

    // =====================================================
    // MOSTRAR PANTALLA
    // =====================================================

    public void mostrarPantalla(
            PantallaBase pantalla,
            Scene escena
    ) {

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

        actualizarVideoMenu(pantalla);

        pantalla.alMostrar();
    }

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

        actualizarVideoMenu(pantalla);

        pantalla.alMostrar();
    }

    // =====================================================
    // GETTERS Y SETTERS
    // =====================================================

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

    // =====================================================
    // NAVEGACIÓN DE MENÚS
    // =====================================================

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

    // =====================================================
    // EQUIPO
    // =====================================================

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

    // =====================================================
    // INVENTARIO
    // =====================================================

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

    // =====================================================
    // POKÉMON CAPTURADOS
    // =====================================================

    public void mostrarPokemonesCapturados() {

        if (jugador == null) {
            return;
        }

        pantallaAnteriorCapturados = pantallaActual;

        new PantallaPokemonesCapturados(
                this,
                jugador
        ).mostrar(stage);
    }

    public void volverDesdePokemonesCapturados() {

        restaurarPantalla(pantallaAnteriorCapturados);

        pantallaAnteriorCapturados = null;
    }

    // =====================================================
    // INICIAR PARTIDA
    // =====================================================

    public void iniciarPartida() {

        if (!partidaDAO.hayLugarDisponible()) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("LÍMITE DE PARTIDAS");
            alerta.setHeaderText("NO HAY ESPACIO DISPONIBLE");

            alerta.setContentText(
                    "Ya existen tres aventuras guardadas. " +
                            "Eliminá una partida para comenzar otra."
            );

            alerta.showAndWait();

            return;
        }

        // Crear una partida nueva.
        jugador = partidaService.crearJugadorInicial(this.tipo);

        // Posición inicial: solamente para partidas nuevas.
        jugador.setX(254);
        jugador.setY(177);

        vistaJuego = new VistaJuego(jugador, this);

        vistaJuego.mostrar(stage);
    }

    // =====================================================
    // COMBATE
    // =====================================================

    public void iniciarCombate(
            Jugador entrenadorJugador,
            Pokemon pokemon
    ) {

        if (combateActivo || entrenadorJugador == null) {
            return;
        }

        Pokemon activo = entrenadorJugador.getPokemonActivo();

        if (activo == null || pokemon == null) {
            return;
        }

        Pokemon pokemonSalvaje = pokemon;

        combateActivo = true;
        pantallaMapaAnterior = pantallaActual;

        if (vistaJuego != null) {

            vistaJuego.animarEncuentro(() ->
                    mostrarPantallaCombate(
                            entrenadorJugador,
                            pokemonSalvaje
                    )
            );

        } else {

            mostrarPantallaCombate(
                    entrenadorJugador,
                    pokemonSalvaje
            );
        }
    }

    private void mostrarPantallaCombate(
            Jugador entrenadorJugador,
            Pokemon pokemonSalvaje
    ) {

        try {

            CombateViewModel viewModel =
                    new CombateViewModel(
                            entrenadorJugador,
                            pokemonSalvaje
                    );

            PantallaCombate pantallaCombate =
                    new PantallaCombate(viewModel, this);

            viewModel.setOnTerminarCombateCallback(
                    pantallaCombate::mostrarResultados
            );

            pantallaCombate.mostrar(stage);

        } catch (RuntimeException e) {

            combateActivo = false;

            if (vistaJuego != null) {
                vistaJuego.alMostrar();
            }

            throw e;
        }
    }

    public void regresarAlMapa() {

        combateActivo = false;

        restaurarPantalla(pantallaMapaAnterior);

        pantallaMapaAnterior = null;
    }

    // =====================================================
    // SALIR Y PANTALLA COMPLETA
    // =====================================================

    public void salir() {

        if (pantallaActual != null) {
            pantallaActual.alOcultar();
        }

        liberarVideoMenu();

        stage.close();
    }

    public void alternarPantallaCompleta() {

        stage.setFullScreen(!stage.isFullScreen());
    }

    // =====================================================
    // CAMBIAR MAPA
    // =====================================================

    public void cambiarMapa(
            CargadorMapaTiled mapa,
            double x,
            double y
    ) {

        if (vistaJuego == null) {
            return;
        }

        vistaJuego.cambiarMapa(mapa, x, y);
    }

    // =====================================================
    // RESULTADOS
    // =====================================================

    public void mostrarResultados(CombateViewModel viewModel) {

        new PantallaResultados(viewModel, this).mostrar(stage);
    }

    // =====================================================
    // VIDEO DEL MENÚ
    // =====================================================

    public MediaPlayer obtenerReproductorMenu() {

        if (reproductorMenu == null) {

            URL archivoVideo =
                    getClass().getResource("/videos/fondo.mp4");

            if (archivoVideo == null) {

                throw new IllegalStateException(
                        "No se encontró /videos/fondo.mp4"
                );
            }

            Media media = new Media(archivoVideo.toExternalForm());

            reproductorMenu = new MediaPlayer(media);

            reproductorMenu.setCycleCount(MediaPlayer.INDEFINITE);

            reproductorMenu.setOnReady(() -> {

                if (pantallaActual instanceof PantallaMenuBase) {
                    reproductorMenu.play();
                }
            });

            reproductorMenu.setOnError(() -> {

                System.err.println(
                        "Error reproduciendo video: "
                                + reproductorMenu.getError()
                );
            });
        }

        return reproductorMenu;
    }

    private void actualizarVideoMenu(PantallaBase pantalla) {

        if (pantalla instanceof PantallaMenuBase) {

            MediaPlayer reproductor = obtenerReproductorMenu();

            if (reproductor.getStatus() == MediaPlayer.Status.READY
                    || reproductor.getStatus() == MediaPlayer.Status.PAUSED
                    || reproductor.getStatus() == MediaPlayer.Status.STOPPED) {

                reproductor.play();
            }

        } else if (reproductorMenu != null) {

            reproductorMenu.pause();
        }
    }

    private void liberarVideoMenu() {

        if (reproductorMenu != null) {

            reproductorMenu.stop();
            reproductorMenu.dispose();

            reproductorMenu = null;
        }
    }
}
