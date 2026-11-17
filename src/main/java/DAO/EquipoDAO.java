
package DAO;

import excepciones.PokemonInexistenteException;
import model.*;

import java.sql.*;
import java.util.*;

/**
 * Gestiona la persistencia del equipo y de los Pokémon capturados
 * de cada jugador.
 */
public class EquipoDAO {

    private final PokemonDAO pokemonDAO;

    /**
     * Constructor que recibe el DAO encargado de los Pokémon.
     */
    public EquipoDAO(PokemonDAO pokemonDAO) {
        this.pokemonDAO = pokemonDAO;
    }

    /**
     * Guarda los nombres de los Pokémon capturados por el jugador.
     */
    public void guardarPokemonesCapturados(
            Jugador jugador,
            Connection conn
    ) throws SQLException {

        String eliminar =
                "DELETE FROM jugador_pokemones_capturados WHERE jugador_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(eliminar)) {
            ps.setInt(1, jugador.getId());
            ps.executeUpdate();
        }

        String insertar =
                "INSERT INTO jugador_pokemones_capturados " +
                        "(jugador_id, nombre_pokemon, orden) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertar)) {

            int orden = 1;

            for (String nombre : jugador.getPokemonesCapturados()) {

                ps.setInt(1, jugador.getId());
                ps.setString(2, nombre);
                ps.setInt(3, orden++);

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    /**
     * Recupera los nombres de los Pokémon capturados por el jugador,
     * respetando el orden en el que fueron capturados.
     */
    public LinkedHashSet<String> recuperarPokemonesCapturados(
            int jugadorId,
            Connection conn
    ) throws SQLException {

        LinkedHashSet<String> capturados = new LinkedHashSet<>();

        String sql =
                "SELECT nombre_pokemon " +
                        "FROM jugador_pokemones_capturados " +
                        "WHERE jugador_id = ? ORDER BY orden ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    capturados.add(rs.getString("nombre_pokemon"));
                }
            }
        }

        return capturados;
    }

    /**
     * Guarda los Pokémon del equipo y sus posiciones.
     */
    public void guardarEquipo(
            Jugador jugador,
            Connection conn
    ) throws SQLException {

        List<Pokemon> equipo = jugador.getEquipoCompleto();

        String eliminar =
                "DELETE FROM jugador_equipo WHERE jugador_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(eliminar)) {
            ps.setInt(1, jugador.getId());
            ps.executeUpdate();
        }

        String insertar =
                "INSERT INTO jugador_equipo " +
                        "(jugador_id, slot, pokemon_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertar)) {

            for (int i = 0; i < equipo.size(); i++) {

                Pokemon pokemon = equipo.get(i);

                if (pokemon == null) {
                    continue;
                }

                pokemonDAO.guardarPokemon(
                        pokemon,
                        jugador.getId(),
                        conn
                );

                ps.setInt(1, jugador.getId());
                ps.setInt(2, i);
                ps.setInt(3, pokemon.getId());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    /**
     * Recupera el equipo del jugador respetando las posiciones
     * almacenadas en la tabla jugador_equipo.
     */
    public List<Pokemon> recuperarEquipo(
            int jugadorId,
            Connection conn,
            Map<Integer, Pokemon> pokemones
    ) throws SQLException, PokemonInexistenteException {

        List<Pokemon> equipo = new ArrayList<>();

        String sql =
                "SELECT pokemon_id, slot " +
                        "FROM jugador_equipo " +
                        "WHERE jugador_id = ? ORDER BY slot ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int pokemonId = rs.getInt("pokemon_id");
                    int slot = rs.getInt("slot");

                    Pokemon pokemon = pokemonDAO.recuperarPokemon(
                            pokemonId,
                            jugadorId,
                            conn,
                            pokemones
                    );

                    if (pokemon != null) {

                        while (equipo.size() <= slot) {
                            equipo.add(null);
                        }

                        equipo.set(slot, pokemon);
                    }
                }
            }
        }

        return equipo;
    }
}
