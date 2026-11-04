
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
import model.*;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class GestorPantallas {

    private final Stage stage;
    private Scene escenaMapaAnterior;

    public GestorPantallas(Stage stage) {
        this.stage = stage;
    }

    //Metodo de configuracion del Stage
    public void mostrarEscena(Scene escena) {
        stage.setTitle("Bichos feos");
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

    // Crear los objetos necesarios e iniciar el juego
    public void iniciarPartida() {
        Mapa mapa = new Mapa(10, 10);

        Entrenador entrenador = new Entrenador();

        Pokemon pokemonInicial = new Pokemon("Picachu", 50.0, 50.0, 12.0, 5.0, 10.0,
                TipoPokemon.ELECTRICO, 1, 0, 12, 5, 10);

        entrenador.getEquipo().add(pokemonInicial);

        Jugador jugador = new Jugador(290, 0, 3,entrenador);

        new VistaJuego(mapa, jugador, this).mostrar(stage);
    }

    public void iniciarCombate(Entrenador entrenadorJugador, Pokemon pokemonSalvaje) {
        System.out.println("DEBUG: Entrando a GestorPantallas.iniciarCombate()");

        this.escenaMapaAnterior = stage.getScene();

        try {
            CombateViewModel viewModel = new CombateViewModel(entrenadorJugador, pokemonSalvaje);
            viewModel.setOnTerminarCombateCallback(this::regresarAlMapa);

            PantallaCombate pantallaCombate = new PantallaCombate(viewModel);
            System.out.println("DEBUG: PantallaCombate creada con éxito.");

            Scene escenaCombate = new Scene(pantallaCombate, 800, 600);
            stage.setScene(escenaCombate);
            stage.setFullScreen(true);
            stage.show();
            System.out.println("DEBUG: Escena de combate aplicada al Stage.");

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO AL INICIAR COMBATE:");
            e.printStackTrace(); // Esto te dirá exactamente si falló el CSS, una imagen o un null
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
