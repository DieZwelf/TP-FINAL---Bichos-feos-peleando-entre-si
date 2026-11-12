package vista;

import controlador.CombateViewModel;
import javafx.scene.input.KeyCode;
import model.*;
import vista.base.PantallaBase;
import vista.juego.CargadorMapaTiled;
import vista.menu.PantallaLogin;
import vista.menu.PantallaInicio;
import vista.menu.PantallaPartidaNueva;
import vista.menu.PantallaCargarPartida;
import vista.combate.PantallaEquipo;
import vista.combate.PantallaInventario;
import vista.combate.PantallaCombate;
import vista.juego.VistaJuego;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import servicio.PartidaService;

import java.net.URL;

/** Centraliza la navegación y conserva las pantallas a las que se puede volver. */
public class GestorPantallas {

    private final Stage stage;
    private final PartidaService partidaService = new PartidaService();

    private PantallaBase pantallaActual;
    private PantallaBase pantallaAnteriorEquipo;
    private PantallaBase pantallaAnteriorInventario;
    private PantallaBase pantallaMapaAnterior;

    private Jugador jugador;
    private boolean combateActivo;
    private VistaJuego vistaJuego;
    private TipoJugador tipo;

    public GestorPantallas(Stage stage) {
        this.stage = stage;
        configurarVentana();
    }



    private void configurarVentana() {
        stage.setTitle("Bichos feos");

        URL icono = getClass().getResource("/images/pantallaMenu/icono.png");
        if (icono != null) {
            stage.getIcons().setAll(new Image(icono.toExternalForm()));
        }

        stage.setWidth(1400);
        stage.setHeight(800);
        stage.setResizable(true);
    }

    /** Método único para presentar una pantalla nueva. */
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


        pantalla.alMostrar();
    }

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

    public void mostrarEquipo() {
        if (jugador == null) return;
        pantallaAnteriorEquipo = pantallaActual;
        new PantallaEquipo(this, jugador).mostrar(stage);
    }

    public void volverDesdeEquipo() {
        restaurarPantalla(pantallaAnteriorEquipo);
        pantallaAnteriorEquipo = null;
    }

    // Inventario desde el mapa
    public void mostrarInventario() {
        if (jugador == null) return;

        pantallaAnteriorInventario = pantallaActual;

        new PantallaInventario(this, jugador, null).mostrar(stage);
    }

    public void mostrarInventario(CombateViewModel viewModel) {
        if (jugador == null || viewModel == null) return;

        pantallaAnteriorInventario = pantallaActual;

        new PantallaInventario(this, jugador,viewModel).mostrar(stage);
    }

    public void volverDesdeInventario() {
        restaurarPantalla(pantallaAnteriorInventario);
        pantallaAnteriorInventario = null;
    }

    public void iniciarPartida() {
        jugador = partidaService.crearJugadorInicial(TipoJugador.USUARIO);

        vistaJuego = new VistaJuego(jugador, this);

        vistaJuego.mostrar(stage);
    }

    public void iniciarCombate(Jugador entrenadorJugador, Pokemon pokemon) {
        if (combateActivo || entrenadorJugador == null) return;

        Pokemon activo = entrenadorJugador.getPokemonActivo();

        if (activo == null) return;

        Pokemon pokemonSalvaje = GeneradorPokemonSalvaje.generarPokemonSegunNivel(activo.getNivel());

        combateActivo = true;
        pantallaMapaAnterior = pantallaActual;

        try {
            CombateViewModel viewModel = new CombateViewModel(entrenadorJugador, pokemonSalvaje);

            PantallaCombate pantallaCombate = new PantallaCombate(viewModel, this);

            viewModel.setOnTerminarCombateCallback(pantallaCombate::mostrarResultados);

            pantallaCombate.mostrar(stage);

        } catch (RuntimeException e) {
            combateActivo = false;
            throw e;
        }

    }

    public void regresarAlMapa() {
        combateActivo = false;
        restaurarPantalla(pantallaMapaAnterior);
        pantallaMapaAnterior = null;
    }

    public void salir() {
        if (pantallaActual != null) {
            pantallaActual.alOcultar();
        }
        stage.close();
    }

    public void alternarPantallaCompleta() {
        stage.setFullScreen(!stage.isFullScreen());
    }

    public void cambiarMapa(CargadorMapaTiled mapa, double x, double y) {
        if (vistaJuego == null) return;

        vistaJuego.cambiarMapa(mapa, x, y);
    }
}
