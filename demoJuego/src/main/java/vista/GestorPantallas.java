
/**
 * La clase GestorPantallas centraliza la navegación entre las
 * distintas pantallas del juego.
 *
 * Su objetivo es separar la lógica de navegación de la construcción
 * de las interfaces gráficas, permitiendo cambiar de pantalla
 * desde un único lugar y facilitando el mantenimiento y la
 * organización del código.
 *
 * Además, administra la ventana principal (Stage) que se utiliza
 * para mostrar las diferentes pantallas del juego.
 */

package vista;

import controlador.CombateViewModel;
import controlador.InventarioViewModel;
import model.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GestorPantallas {

    private final Stage stage;
    private Scene escenaCombate;
    private Scene escenaMapaAnterior;
    private PantallaCombate pantallaCombate;
    private CombateViewModel combateViewModel;
    private boolean combateActivo = false;
    private Entrenador entrenador;
    private Scene escenaAnteriorEquipo;

    public GestorPantallas(Stage stage) {
        this.stage = stage;
    }

    //Metodo de configuracion del Stage
    public void mostrarEscena(Scene escena) {
        stage.setTitle("Bichos feos");

        URL iconoURL = Objects.requireNonNull(getClass().getResource("/images/pantallaMenu/icono.png"));
        Image icono = new Image(iconoURL.toExternalForm());

        stage.getIcons().setAll(icono);
        stage.setScene(escena);
        stage.setFullScreen(true);
        stage.show();
    }

    //Mostrar pantalla login
    public void mostrarLogin() {
        new PantallaLogin().mostrar(stage);
    }

    // Mostrar menú principal
    public void mostrarInicio() {
        new PantallaInicio(this).mostrar(stage);
    }

    // Ir a la pantalla de nueva partida
    public void mostrarNuevaPartida() {
        new PantallaPartidaNueva(this).mostrar(stage);
    }

    public void mostrarEquipo() {

        escenaAnteriorEquipo = stage.getScene();

        PantallaEquipo pantallaEquipo = new PantallaEquipo(this, entrenador);

        pantallaEquipo.mostrar(stage);
    }

    public void mostrarInventario (){

        PantallaInventario pantallaInventario = new PantallaInventario(this);

        InventarioViewModel inventarioViewModel = new InventarioViewModel(entrenador.getInventario(), pantallaInventario);

        pantallaInventario.mostrar(stage);

    }

    public void volverDesdeEquipo() {

        if (escenaAnteriorEquipo != null) {
            stage.setScene(escenaAnteriorEquipo);
            stage.setFullScreen(true);
            stage.show();
        }
    }

    public void volverAlCombate() {
        stage.setScene(escenaCombate);
        stage.setFullScreen(true);
        stage.show();
    }

    // Crear los objetos necesarios e iniciar el juego
    public void iniciarPartida() {
        Mapa mapa = new Mapa(10, 10);

        TreeMap<Integer, Pokemon> treeMap = new TreeMap<>();

        Pokemon pokemonInicial = new Pokemon("Pikachu", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.ELECTRICO, 1, 0, 12, 5, 10);

        Pokemon pokemonInicial2 = new Pokemon("Pepe", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.TIERRA, 1, 0, 12, 5, 10);

        Pokemon pokemonInicial3 = new Pokemon("Juan", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.TIERRA, 1, 0, 12, 5, 10);

        Pokemon pokemonInicial4 = new Pokemon("Jose", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.TIERRA, 1, 0, 12, 5, 10);

        Pokemon pokemonInicial5 = new Pokemon("Roberto", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.TIERRA, 1, 0, 12, 5, 10);

        Pokemon pokemonInicial6 = new Pokemon("Fernando", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.TIERRA, 1, 0, 12, 5, 10);


        List<Pokemon> pokemons = new ArrayList<>();
        Entrenador entrenador = new Entrenador("Pepe", treeMap, pokemons, "20" );
        this.entrenador = entrenador;

        entrenador.aniadir(pokemonInicial);
        entrenador.aniadir(pokemonInicial2);
        entrenador.aniadir(pokemonInicial3);
        entrenador.aniadir(pokemonInicial4);
        entrenador.aniadir(pokemonInicial5);
        entrenador.aniadir(pokemonInicial6);


        entrenador.setPokemonActivo(pokemonInicial);

        Jugador jugador = new Jugador(290, 0, 3,entrenador);

        Pocion item = new Pocion("Pocion", "Cura", 2, 2, 2.5);
        jugador.getEntrenador().getInventario().agregarObjeto(item);
        new VistaJuego(mapa, jugador, this).mostrar(stage);
    }

    public void iniciarCombate(
            Entrenador entrenadorJugador,
            Pokemon pokemonSalvaje) {

        // Evitar combates simultáneos
        if (combateActivo) {
            System.out.println("Ya hay un combate activo.");
            return;
        }

        // Bloquear inmediatamente
        combateActivo = true;

        try {
            escenaMapaAnterior = stage.getScene();

            combateViewModel = new CombateViewModel(
                    entrenadorJugador,
                    pokemonSalvaje
            );

            combateViewModel.setOnTerminarCombateCallback(
                    this::regresarAlMapa
            );

            pantallaCombate = new PantallaCombate(
                    combateViewModel,
                    this
            );

            Scene escena = new Scene(pantallaCombate, 1000, 800);

            stage.setScene(escena);
            stage.setFullScreen(true);
            stage.show();

        } catch (Exception e) {

            combateActivo = false;

            e.printStackTrace();
        }
    }

    public void regresarAlMapa() {
        if (escenaMapaAnterior != null) {
            stage.setScene(escenaMapaAnterior);
            stage.setFullScreen(true);
        }
    }

    public void mostrarCargarPartida (){
        new PantallaCargarPartida(this).mostrar(stage);
    }

    // Cerrar el juego
    public void salir() {
        stage.close();
    }
}
