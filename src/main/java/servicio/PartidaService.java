package servicio;

import model.*;

import java.util.ArrayList;
import java.util.TreeMap;

/** Construye el estado inicial de una partida sin mezclarlo con la navegación. */
public class PartidaService {

    public Entrenador crearEntrenadorInicial() {
        Entrenador entrenador = new Entrenador("Pepe", new TreeMap<>(), new ArrayList<>(), "20");

        agregarPokemonInicial(entrenador, "Pikachu", TipoPokemon.ELECTRICO);
        agregarPokemonInicial(entrenador, "Charmander", TipoPokemon.FUEGO);


        entrenador.setPokemonActivo(entrenador.getEquipo().get(0));
        entrenador.getInventario().agregarObjeto(new Pocion("Pocion", "Cura", 2, 2, 50));
        entrenador.getInventario().agregarObjeto(new Pocion("Pocion", "Cura", 2, 2, 50));
        entrenador.getInventario().agregarObjeto(new Pocion("Pocion", "Cura", 2, 2, 50));
        entrenador.getInventario().agregarObjeto(new Pocion("Pocion", "Cura", 2, 2, 50));

        entrenador.getInventario().agregarObjeto(new Pokebola("Pokebola", "Captura pokemones", false));


        return entrenador;
    }

    private void agregarPokemonInicial(Entrenador entrenador, String nombre, TipoPokemon tipo) {
        Pokemon pokemon = new Pokemon(nombre, 50.0, 50.0, 12.0, 5.0, 10.0,
                tipo, 1, 0, 12, 5, 10);
        entrenador.aniadir(pokemon);
    }

    public Jugador crearJugadorInicial(Entrenador entrenador) {
        return new Jugador(290, 0, 3, entrenador);
    }
}
