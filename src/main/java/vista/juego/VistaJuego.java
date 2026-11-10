package vista.juego;

import controlador.JuegoController;
import model.CalculadoraCaptura;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import model.GeneradorPokemonSalvaje;
import model.Jugador;
import model.Pokemon;

import java.util.ArrayList;
import java.util.List;

/** Pantalla de exploración: compone el mapa y al jugador. */
public class VistaJuego extends PantallaBase {

    private static final double ZOOM = 1.4;

    private final Jugador jugador;
    private final VistaJugador vistaJugador;
    private VistaMapa vistaMapa;
    private final Pane mundo = new Pane();
    private final Affine transformacionCamara = new Affine();
    private final Rectangle recorte = new Rectangle();

    private Pane root;
    private Scene escena;
    private JuegoController controlador;
    private List<TransicionMapa> transiciones = new ArrayList<>();

    public VistaJuego(Jugador jugador, GestorPantallas pantallas) {
        super(pantallas);
        this.jugador = jugador;

        MapaPueblo mapaInicial = new MapaPueblo();

        this.vistaMapa = new VistaMapa(mapaInicial);
        this.vistaJugador = new VistaJugador(jugador);

        actualizarTransiciones(mapaInicial);
    }

    @Override
    protected Parent crearContenido() {
        root = new Pane();
        mundo.getChildren().setAll(vistaMapa, vistaJugador);
        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());
        mundo.getTransforms().setAll(transformacionCamara);
        root.getChildren().setAll(mundo);
        root.setClip(recorte);

        root.widthProperty().addListener((obs, antes, ahora) -> {
            recorte.setWidth(ahora.doubleValue());
            actualizarCamara();
        });
        root.heightProperty().addListener((obs, antes, ahora) -> {
            recorte.setHeight(ahora.doubleValue());
            actualizarCamara();
        });
        return root;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        this.escena = escena;
        controlador = new JuegoController(jugador, vistaJugador, this, vistaMapa.getColisiones());
        controlador.setOnEncuentroGenerado(this::generarEncuentro);
        configurarTeclas(escena);
    }

    @Override
    public void alMostrar() {
        if (controlador != null) {
            controlador.reanudarGameLoop();
        }
        actualizarCamara();
        root.requestFocus();
    }

    @Override
    public void alOcultar() {
        if (controlador != null) {
            controlador.detenerGameLoop();
        }
    }

    private void generarEncuentro() {
        Pokemon pokemonActivo = jugador.getEntrenador().getPokemonActivo();
        if (pokemonActivo == null) return;

        double probabilidad = CalculadoraCaptura.calcularProbabilidad(pokemonActivo);

        System.out.println("Vida: " + pokemonActivo.getVida() + "/" + pokemonActivo.getVidaMaxima());
        System.out.println("Probabilidad: " + (probabilidad * 100) + "%");

        int nivelJugador = pokemonActivo.getNivel();
        Pokemon salvaje = GeneradorPokemonSalvaje.generarPokemonSegunNivel(nivelJugador);
        pantallas.iniciarCombate(jugador.getEntrenador(), salvaje);
    }

    public void actualizarCamara() {
        if (escena == null || root == null) return;

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();
        if (anchoVentana <= 0 || altoVentana <= 0) return;

        double anchoEscalado = vistaMapa.getPrefWidth() * ZOOM;
        double altoEscalado = vistaMapa.getPrefHeight() * ZOOM;
        double centroX = jugador.getX() + 24;
        double centroY = jugador.getY() + 24;

        double x = anchoVentana / 2 - centroX * ZOOM;
        double y = altoVentana / 2 - centroY * ZOOM;

        x = limitarCamara(x, anchoEscalado, anchoVentana);
        y = limitarCamara(y, altoEscalado, altoVentana);

        transformacionCamara.setToTransform(ZOOM, 0, x, 0, ZOOM, y);
    }

    private double limitarCamara(double posicion, double tamanoMapa, double tamanoVentana) {
        if (tamanoMapa >= tamanoVentana) {
            return Math.max(tamanoVentana - tamanoMapa, Math.min(0, posicion));
        }
        return (tamanoVentana - tamanoMapa) / 2;
    }

    private void configurarTeclas(Scene escena) {
        escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            controlador.manejarTeclaPresionada(evento);
            if (evento.getCode() == KeyCode.TAB) {
                pantallas.mostrarEquipo();
                evento.consume();
            } else if (evento.getCode() == KeyCode.E) {
                pantallas.mostrarInventario();
                evento.consume();
            }
        });
        escena.addEventFilter(KeyEvent.KEY_RELEASED, controlador::manejarTeclaLiberada);
    }

    public void cambiarMapa(CargadorMapaTiled nuevoMapa, double nuevaX, double nuevaY) {

        VistaMapa nuevoVistaMapa = new VistaMapa(nuevoMapa);

        this.vistaMapa = nuevoVistaMapa;

        jugador.setX(nuevaX);
        jugador.setY(nuevaY);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);

        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());

        actualizarTransiciones(nuevoMapa);

        if (controlador != null) {
            controlador.setColisiones(vistaMapa.getColisiones());
        }

        actualizarCamara();
    }

    private void configurarTransiciones(CargadorMapaTiled mapa) {

        transiciones.clear();

        if (mapa instanceof MapaPueblo) {

            // Salida del pueblo hacia el bosque.
        transiciones.add(new TransicionMapa(450, 100, 80, 40, MapaBosque::new,100, 500));

        } else if (mapa instanceof MapaBosque) {
            // Regreso del bosque hacia el pueblo.
            transiciones.add(new TransicionMapa(100, 550, 80, 40, MapaPueblo::new,
                    450, 150));
        }
    }

    public void verificarTransicionMapa() {

        for (TransicionMapa transicion : transiciones) {

            if (transicion.jugadorEntro(jugador.getX(), jugador.getY())) {

                cambiarMapa(transicion.getMapaDestino(), transicion.getSpawnX(), transicion.getSpawnY());
                return;
            }
        }
    }

    private void actualizarTransiciones(CargadorMapaTiled mapa) {
        transiciones = mapa.obtenerTransiciones();
    }



}
