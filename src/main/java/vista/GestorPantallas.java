package vista;

import controlador.CombateViewModel;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import model.*;
import vista.base.PantallaBase;
import vista.juego.CargadorMapaTiled;
import vista.menu.PantallaLogin;
import vista.menu.PantallaInicio;
import vista.menu.PantallaPartidaNueva;
import vista.menu.PantallaCargarPartida;
import vista.equipo.PantallaEquipo;
import vista.inventario.PantallaInventario;
import vista.combate.PantallaCombate;
import vista.juego.VistaJuego;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import servicio.PartidaService;
import vista.combate.PantallaResultados;

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
    private TipoJugador tipo = TipoJugador.USUARIO;



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
        jugador = partidaService.crearJugadorInicial(this.tipo);

        vistaJuego = new VistaJuego(jugador, this);

        vistaJuego.mostrar(stage);
    }

    public void iniciarCombate(Jugador entrenadorJugador, Pokemon pokemon) {

        if (combateActivo || entrenadorJugador == null) return;

        Pokemon activo = entrenadorJugador.getPokemonActivo();

        if (activo == null || pokemon == null) return;

        combateActivo = true;
        pantallaMapaAnterior = pantallaActual;

        // La animación de encuentro ya se ejecutó desde VistaJuego.
        mostrarPantallaCombate(entrenadorJugador, pokemon);
    }

    private void mostrarPantallaCombate(
            Jugador entrenadorJugador,
            Pokemon pokemonSalvaje
    ) {

        try {

            CombateViewModel viewModel =
                    new CombateViewModel(entrenadorJugador, pokemonSalvaje);

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

        if (pantallaMapaAnterior == null) {
            combateActivo = false;
            mostrarInicio();
            return;
        }

        Scene escenaCombate = stage.getScene();
        Scene escenaMapa = pantallaMapaAnterior.getEscena();

        if (escenaCombate == null || escenaMapa == null) {
            combateActivo = false;
            restaurarPantalla(pantallaMapaAnterior);
            pantallaMapaAnterior = null;
            return;
        }

        Parent contenidoCombate = escenaCombate.getRoot();
        Parent contenidoMapa = escenaMapa.getRoot();

        Paint fondoCombate = escenaCombate.getFill();
        Paint fondoMapa = escenaMapa.getFill();

        // Fondo negro para ocultar el cambio de pantalla.
        escenaCombate.setFill(Color.BLACK);

        FadeTransition oscurecer = new FadeTransition(
                Duration.millis(250),
                contenidoCombate
        );

        oscurecer.setFromValue(1);
        oscurecer.setToValue(0);

        oscurecer.setOnFinished(evento -> {

            // Preparamos el mapa completamente oscuro.
            escenaMapa.setFill(Color.BLACK);
            contenidoMapa.setOpacity(0);

            combateActivo = false;

            restaurarPantalla(pantallaMapaAnterior);

            pantallaMapaAnterior = null;

            // Aparece el mapa progresivamente.
            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(300),
                    contenidoMapa
            );

            aparecer.setFromValue(0);
            aparecer.setToValue(1);

            aparecer.setOnFinished(fin -> {
                contenidoMapa.setOpacity(1);
                escenaMapa.setFill(fondoMapa);
                escenaCombate.setFill(fondoCombate);
            });

            aparecer.play();
        });

        oscurecer.play();
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
    public void mostrarResultados(CombateViewModel viewModel) {
        new PantallaResultados(viewModel, this).mostrar(stage);
    }
}
