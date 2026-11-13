
package DAO;

import model.Pokemon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Interfaz que define las operaciones de persistencia
 * necesarias para guardar y recuperar Pokémon.
 */
public interface IPokemonDAO {

    void guardarPokemon(
            Pokemon pokemon,
            int jugadorId,
            Connection conn
    ) throws SQLException;

    Pokemon recuperarPokemon(
            int pokemonId,
            int jugadorId,
            Connection conn,
            Map<Integer, Pokemon> pokemones
    ) throws SQLException;
}
