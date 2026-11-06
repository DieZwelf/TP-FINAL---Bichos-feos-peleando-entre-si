
package vista;

import javafx.scene.input.KeyEvent;
import model.GeneradorPokemonSalvaje;
import model.Mapa;
import model.Jugador;
import controlador.JuegoController;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.transform.Affine;
import javafx.scene.shape.Rectangle;
import model.Pokemon;
import javafx.scene.input.KeyCode;

public class VistaJuego extends Pane implements IPantalla {

    private final Jugador jugador;
    private final VistaJugador vistaJugador;
    private final VistaMapa vistaMapa;
    private final Pane mundo = new Pane();
    private static final double ZOOM = 1.4;
    private final Affine transformacionCamara = new Affine();
    private final Rectangle recorte = new Rectangle();

    private JuegoController controlador;
    private Scene escena;
    protected GestorPantallas pantallas;

    public VistaJuego(Mapa mapa, Jugador jugador, GestorPantallas pantallas) {
        this.jugador = jugador;
        this.pantallas = pantallas;
        vistaMapa = new VistaMapa(mapa);
        vistaJugador = new VistaJugador(jugador);

        mundo.getChildren().addAll(vistaMapa, vistaJugador);
        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());

        getChildren().add(mundo);

        mundo.getTransforms().setAll(transformacionCamara);

        setClip(recorte);

        widthProperty().addListener((obs, antes, ahora) -> {
            recorte.setWidth(ahora.doubleValue());
            actualizarCamara();
        });

        heightProperty().addListener((obs, antes, ahora) -> {
            recorte.setHeight(ahora.doubleValue());
            actualizarCamara();
        });
    }

    public VistaJugador getVistaJugador() {
        return vistaJugador;
    }

    @Override
    public void mostrar(Stage stage) {
        escena = new Scene(this, 1000, 800);

        configurarTeclas(escena);
// Primero mostrar la escena
        pantallas.mostrarEscena(escena);



// Después calcular la cámara
        actualizarCamara();

// Finalmente iniciar el bucle
        controlador = new JuegoController(jugador, vistaJugador, this, escena,vistaMapa.getColisiones());

        controlador.setOnEncuentroGenerado(() -> {
            // Obtiene el nivel del Pokémon activo del jugador
            Pokemon pokemonActivo = jugador.getEntrenador().getPokemonActivo();
            int nivelJugador = pokemonActivo.getNivel();

            // Pokémon salvaje acorde a ese nivel
            Pokemon pokemonSalvaje = GeneradorPokemonSalvaje.generarPokemonSegunNivel(nivelJugador);

            // Mensaje de prueba por consola
            System.out.println("==================================================");
            System.out.println(" ¡ENCUENTRO SALVAJE ACTIVADO!");
            System.out.println(" Tu Pokémon (" + pokemonActivo.getNombre() + " Nv." + nivelJugador + ")");
            System.out.println(" se enfrenta contra un salvaje:");
            System.out.println(" " + pokemonSalvaje.toString());
            System.out.println("==================================================");


                pantallas.iniciarCombate(jugador.getEntrenador(), pokemonSalvaje);

        });


        controlador.iniciarGameLoop();
    }

    public void actualizarCamara() {
        if (escena == null) return;

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();

        if (anchoVentana <= 0 || altoVentana <= 0) return;

        double anchoMapa = vistaMapa.getPrefWidth();
        double altoMapa = vistaMapa.getPrefHeight();

        double anchoEscalado = anchoMapa * ZOOM;
        double altoEscalado = altoMapa * ZOOM;

        // Centro del personaje
        double centroX = jugador.getX() + 24;
        double centroY = jugador.getY() + 24;

        // Posición necesaria para centrar al personaje
        double x = anchoVentana / 2 - centroX * ZOOM;
        double y = altoVentana / 2 - centroY * ZOOM;

        // Evitar que se vea el exterior del mapa
        if (anchoEscalado >= anchoVentana) {
            x = Math.max(anchoVentana - anchoEscalado, Math.min(0, x));
        } else {
            x = (anchoVentana - anchoEscalado) / 2;
        }

        if (altoEscalado >= altoVentana) {
            y = Math.max(altoVentana - altoEscalado, Math.min(0, y));
        } else {
            y = (altoVentana - altoEscalado) / 2;
        }

        // Escala desde el origen (0, 0), sin pivote automático
        transformacionCamara.setToTransform(ZOOM, 0, x, 0, ZOOM, y);
    }

    private void configurarTeclas(Scene escena) {

        escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {

            if (evento.getCode() == KeyCode.TAB) {

                pantallas.mostrarEquipo();

                evento.consume();
            }
            if (evento.getCode() == KeyCode.E) {

                pantallas.mostrarInventario();

                evento.consume();
            }
        });


    }

}
