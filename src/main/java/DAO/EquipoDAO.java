
package DAO;

import model.*;
import java.sql.*;
import java.util.*;

/**
 * Gestiona el equipo y los Pokémon capturados de un jugador.
 */
public class EquipoDAO {

    private final IPokemonDAO pokemonDAO;

    public EquipoDAO(IPokemonDAO pokemonDAO) {
        this.pokemonDAO = pokemonDAO;
    }

    /**
     * Reemplaza los Pokémon capturados guardados por los actuales.
     */
    public void guardarPokemonesCapturados(
            Jugador jugador,
            Connection conn
    ) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM jugador_pokemones_capturados WHERE jugador_id = ?")) {

            ps.setInt(1, jugador.getId());
            ps.executeUpdate();
        }

        if (jugador.getPokemonesCapturados() != null) {

            String sqlCapturados =
                    "INSERT INTO jugador_pokemones_capturados " +
                            "(jugador_id, nombre_pokemon, orden) VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                         conn.prepareStatement(sqlCapturados)) {

                int orden = 0;

                for (String nombre : jugador.getPokemonesCapturados()) {

                    ps.setInt(1, jugador.getId());
                    ps.setString(2, nombre);
                    ps.setInt(3, orden++);
                    ps.addBatch();
                }

                ps.executeBatch();
            }
        }
    }

    /**
     * Reemplaza las relaciones del equipo por las actuales.
     */
    public void guardarEquipo(
            Jugador jugador,
            Connection conn
    ) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM jugador_equipo WHERE jugador_id = ?")) {

            ps.setInt(1, jugador.getId());
            ps.executeUpdate();
        }

        List<Pokemon> equipo = jugador.getEquipoCompleto();

        if (equipo != null) {

            String sqlEquipo =
                    "INSERT INTO jugador_equipo " +
                            "(jugador_id, slot, pokemon_id) VALUES (?, ?, ?)";

            try (PreparedStatement ps =
                         conn.prepareStatement(sqlEquipo)) {

                for (int slot = 0; slot < equipo.size(); slot++) {

                    Pokemon p = equipo.get(slot);

                    if (p == null) {
                        continue;
                    }

                    pokemonDAO.guardarPokemon(
                            p, jugador.getId(), conn
                    );

                    ps.setInt(1, jugador.getId());
                    ps.setInt(2, slot);
                    ps.setInt(3, p.getId());
                    ps.addBatch();
                }

                ps.executeBatch();
            }
        }
    }

    /**
     * Recupera los nombres de los Pokémon capturados.
     */
    public LinkedHashSet<String> recuperarPokemonesCapturados(
            int jugadorId,
            Connection conn
    ) throws SQLException {

        LinkedHashSet<String> capturados = new LinkedHashSet<>();

        String sql =
                "SELECT nombre_pokemon FROM jugador_pokemones_capturados " +
                        "WHERE jugador_id = ? ORDER BY orden";

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
     * Recupera el equipo conservando las posiciones originales.
     */
    public List<Pokemon> recuperarEquipo(
            int jugadorId,
            Connection conn,
            Map<Integer, Pokemon> pokemones
    ) throws SQLException {

        List<Pokemon> equipo = new ArrayList<>();

        String sql =
                "SELECT slot, pokemon_id FROM jugador_equipo " +
                        "WHERE jugador_id = ? ORDER BY slot";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int slot = rs.getInt("slot");
                    int pokemonId = rs.getInt("pokemon_id");

                    Pokemon pokemon = pokemonDAO.recuperarPokemon(
                            pokemonId,
                            jugadorId,
                            conn,
                            pokemones
                    );

                    while (equipo.size() <= slot) {
                        equipo.add(null);
                    }

                    equipo.set(slot, pokemon);
                }
            }
        }

        return equipo;
    }
}
