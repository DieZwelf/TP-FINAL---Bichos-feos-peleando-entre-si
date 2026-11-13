package servicio;

import model.*;
import DAO.guardarPartidaDAO;
import java.util.ArrayList;
import java.util.TreeMap;

/** Construye el estado inicial de una partida sin mezclarlo con la navegación. */
public class PartidaService {

    public Jugador crearJugadorInicial(TipoJugador tipo) {
        Jugador jugador = new Jugador(290, 0, tipo);


        agregarPokemonInicial(jugador, "Pikachu", TipoPokemon.ELECTRICO);
        agregarPokemonInicial(jugador, "Charmander", TipoPokemon.FUEGO);


        jugador.setPokemonActivo(jugador.getEquipo().get(0));
        jugador.getInventario().agregarObjeto(new Pocion(50));
        jugador.getInventario().agregarObjeto(new Pocion(50));
        jugador.getInventario().agregarObjeto(new Pocion(50));
        jugador.getInventario().agregarObjeto(new Pocion(50));

        jugador.getInventario().agregarObjeto(new Pokebola("Pokebola", "Captura pokemones", false));
        guardarPartidaDAO g = new guardarPartidaDAO();
        g.guardarJugador(jugador);
        return jugador;
    }

    private void agregarPokemonInicial(Jugador jugador, String nombre, TipoPokemon tipo) {
        Pokemon pokemon = new Pokemon(nombre, 50.0, 50.0, 12.0, 5.0,
                tipo, 1, 0, 12, 5);
        jugador.aniadir(pokemon);
    }

}
