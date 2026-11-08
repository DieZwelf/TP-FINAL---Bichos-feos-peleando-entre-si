package vista;

import controlador.CombateViewModel;
import javafx.scene.input.KeyCode;
import model.Jugador;
import vista.base.PantallaBase;
import vista.juego.MapaBosque;
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
import model.Entrenador;
import model.Pokemon;
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

    private Entrenador entrenador;
    private boolean combateActivo;

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

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
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
        if (entrenador == null) return;
        pantallaAnteriorEquipo = pantallaActual;
        new PantallaEquipo(this, entrenador).mostrar(stage);
    }

    public void volverDesdeEquipo() {
        restaurarPantalla(pantallaAnteriorEquipo);
        pantallaAnteriorEquipo = null;
    }

    public void mostrarInventario() {
        if (entrenador == null) return;
        pantallaAnteriorInventario = pantallaActual;
        new PantallaInventario(this, entrenador).mostrar(stage);
    }

    public void volverDesdeInventario() {
        restaurarPantalla(pantallaAnteriorInventario);
        pantallaAnteriorInventario = null;
    }

    public void iniciarPartida() {
        entrenador = partidaService.crearEntrenadorInicial();
        var jugador = partidaService.crearJugadorInicial(entrenador);
        VistaJuego nuevaPantallaMapa = new VistaJuego(jugador, this);
        nuevaPantallaMapa.mostrar(stage);
    }

    public void iniciarCombate(Entrenador entrenadorJugador, Pokemon pokemonSalvaje) {
        if (combateActivo || entrenadorJugador == null || pokemonSalvaje == null) return;

        combateActivo = true;
        pantallaMapaAnterior = pantallaActual;

        try {
            CombateViewModel viewModel = new CombateViewModel(entrenadorJugador, pokemonSalvaje);
            viewModel.setOnTerminarCombateCallback(this::regresarAlMapa);
            new PantallaCombate(viewModel, this).mostrar(stage);
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

    public void cambiarMapaBosque (){
        entrenador = partidaService.crearEntrenadorInicial();
        var jugador = partidaService.crearJugadorInicial(entrenador);
        VistaJuego nuevaPantallaMapa = new VistaJuego(jugador, this);
    }
}
