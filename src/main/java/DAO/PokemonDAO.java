
package DAO;

import model.*;
import java.sql.*;
import java.util.Map;

/**
 * Gestiona la persistencia de los Pokémon de cada jugador.
 */
public class PokemonDAO {

    /**
     * Guarda un Pokémon nuevo o actualiza uno existente.
     */
    public void guardarPokemon(
            Pokemon p,
            int jugadorId,
            Connection conn
    ) throws SQLException {

        if (p.getId() == 0) {
            insertarPokemon(p, jugadorId, conn);
            return;
        }

        String sql = "UPDATE pokemones SET " +
                "nombre = ?, vida_maxima = ?, vida = ?, danio = ?, " +
                "defensa = ?, tipo = ?, nivel = ?, experiencia = ?, " +
                "danio_base = ?, defensa_base = ?, en_equipo = ? " +
                "WHERE id = ? AND jugador_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getVidaMaxima());
            ps.setDouble(3, p.getVida());
            ps.setDouble(4, p.getDanio());
            ps.setDouble(5, p.getDefensa());
            ps.setString(6, p.getTipo().name());
            ps.setInt(7, p.getNivel());
            ps.setInt(8, p.getExperiencia());
            ps.setInt(9, (int) p.getDanioBase());
            ps.setInt(10, (int) p.getDefensaBase());
            ps.setBoolean(11, p.isEnEquipo());
            ps.setInt(12, p.getId());
            ps.setInt(13, jugadorId);

            ps.executeUpdate();
        }
    }

    /**
     * Inserta un Pokémon nuevo y asigna el ID generado.
     */
    private void insertarPokemon(
            Pokemon p,
            int jugadorId,
            Connection conn
    ) throws SQLException {

        String sql = "INSERT INTO pokemones " +
                "(jugador_id, nombre, vida_maxima, vida, danio, defensa, " +
                "tipo, nivel, experiencia, danio_base, defensa_base, en_equipo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, jugadorId);
            ps.setString(2, p.getNombre());
            ps.setDouble(3, p.getVidaMaxima());
            ps.setDouble(4, p.getVida());
            ps.setDouble(5, p.getDanio());
            ps.setDouble(6, p.getDefensa());
            ps.setString(7, p.getTipo().name());
            ps.setInt(8, p.getNivel());
            ps.setInt(9, p.getExperiencia());
            ps.setInt(10, (int) p.getDanioBase());
            ps.setInt(11, (int) p.getDefensaBase());
            ps.setBoolean(12, p.isEnEquipo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    p.setId(rs.getInt(1));
                } else {
                    throw new SQLException(
                            "No se pudo obtener el ID generado para el Pokémon: "
                                    + p.getNombre()
                    );
                }
            }
        }
    }

    /**
     * Recupera un Pokémon. Si ya fue recuperado, reutiliza el objeto.
     */
    public Pokemon recuperarPokemon(
            int pokemonId,
            int jugadorId,
            Connection conn,
            Map<Integer, Pokemon> pokemones
    ) throws SQLException {

        if (pokemones.containsKey(pokemonId)) {
            return pokemones.get(pokemonId);
        }

        String sql =
                "SELECT * FROM pokemones WHERE id = ? AND jugador_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pokemonId);
            ps.setInt(2, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                Pokemon pokemon = new Pokemon(
                        rs.getString("nombre"),
                        rs.getDouble("vida_maxima"),
                        rs.getDouble("vida"),
                        rs.getDouble("danio"),
                        rs.getDouble("defensa"),
                        TipoPokemon.valueOf(rs.getString("tipo")),
                        rs.getInt("nivel"),
                        rs.getInt("experiencia"),
                        rs.getInt("danio_base"),
                        rs.getInt("defensa_base")
                );

                pokemon.setEnEquipo(rs.getBoolean("en_equipo"));

                pokemones.put(pokemonId, pokemon);

                return pokemon;
            }
        }
    }
}
