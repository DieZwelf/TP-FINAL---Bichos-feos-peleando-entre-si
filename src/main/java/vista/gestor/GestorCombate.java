
package vista.gestor;

import controlador.CombateViewModel;
import model.Jugador;
import model.Pokemon;
import vista.base.PantallaBase;
import vista.combate.PantallaCombate;

/**
 * Gestiona el inicio y la finalización de los combates.
 *
 * Coordina la creación del modelo de combate, la pantalla
 * correspondiente y el regreso al mapa.
 */
public class GestorCombate {

    private final GestorPantallas gestorPantallas;

    private boolean combateActivo;

    public GestorCombate(GestorPantallas gestorPantallas) {
        this.gestorPantallas = gestorPantallas;
    }

    /**
     * Inicia un combate contra un Pokémon salvaje.
     *
     * @param entrenadorJugador jugador que participa
     * @param pokemon Pokémon salvaje
     */
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

        combateActivo = true;

        PantallaBase pantallaAnterior =
                gestorPantallas.getPantallaActualInterna();

        gestorPantallas.setPantallaMapaAnterior(pantallaAnterior);

        if (gestorPantallas.getVistaJuegoInterna() != null) {

            gestorPantallas.getVistaJuegoInterna().animarEncuentro(() ->
                    mostrarPantallaCombate(
                            entrenadorJugador,
                            pokemon
                    )
            );

        } else {

            mostrarPantallaCombate(
                    entrenadorJugador,
                    pokemon
            );
        }
    }

    /**
     * Construye el ViewModel y muestra la pantalla de combate.
     */
    public void mostrarPantallaCombate(
            Jugador entrenadorJugador,
            Pokemon pokemonSalvaje
    ) {

        try {

            CombateViewModel viewModel = new CombateViewModel(
                    entrenadorJugador,
                    pokemonSalvaje
            );

            PantallaCombate pantallaCombate =
                    new PantallaCombate(viewModel, gestorPantallas);

            viewModel.setOnTerminarCombateCallback(
                    pantallaCombate::mostrarResultados
            );

            pantallaCombate.mostrar(
                    gestorPantallas.getStageInterno()
            );

        } catch (RuntimeException e) {

            combateActivo = false;

            if (gestorPantallas.getVistaJuegoInterna() != null) {
                gestorPantallas.getVistaJuegoInterna().alMostrar();
            }

            throw e;
        }
    }

    /**
     * Regresa al mapa después de finalizar un combate.
     */
    public void regresarAlMapa() {

        combateActivo = false;

        gestorPantallas.restaurarMapaDesdeCombate();
    }

    public boolean isCombateActivo() {
        return combateActivo;
    }
}
