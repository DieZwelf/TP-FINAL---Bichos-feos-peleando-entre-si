
package DAO;

import conexion.Conexion;
import model.*;
import java.sql.*;
import java.util.*;

/**
 * Coordina las operaciones de persistencia de una partida.
 * Delega el almacenamiento de inventario, Pokémon y equipo
 * en sus respectivos DAO.
 */
public class GuardarPartidaDAO {

    private static final int MAX_PARTIDAS = 3;

    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final PokemonDAO pokemonDAO = new PokemonDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO(pokemonDAO);

    /**
     * Guarda o actualiza una partida completa.
     */
    public void guardarJugador(Jugador jugador) {

        try (Connection conn = Conexion.crearConexion()) {

            conn.setAutoCommit(false);

            try {

                if (jugador.getId() == 0 &&
                        contarPartidas(conn) >= MAX_PARTIDAS) {

                    throw new SQLException(
                            "Ya existen tres partidas guardadas."
                    );
                }

                Inventario inventario = jugador.getInventario();
                int inventarioId;

                if (jugador.getId() == 0) {

                    inventarioId = inventarioDAO.insertarInventario(
                            inventario, conn
                    );

                    inventario.setId(inventarioId);

                    String sql = "INSERT INTO jugadores " +
                            "(x, y, tipo, rango, inventario_id, pokemon_activo_id) " +
                            "VALUES (?, ?, ?, ?, ?, NULL)";

                    try (PreparedStatement ps = conn.prepareStatement(
                            sql, Statement.RETURN_GENERATED_KEYS)) {

                        ps.setDouble(1, jugador.getX());
                        ps.setDouble(2, jugador.getY());
                        ps.setString(3, jugador.getTipo().name());
                        ps.setString(4, jugador.getRango());
                        ps.setInt(5, inventarioId);

                        ps.executeUpdate();

                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) {
                                jugador.setId(rs.getInt(1));
                            } else {
                                throw new SQLException(
                                        "No se pudo crear el jugador."
                                );
                            }
                        }
                    }

                } else {

                    inventarioId = inventario.getId();

                    String sql = "UPDATE jugadores " +
                            "SET x = ?, y = ?, tipo = ?, rango = ? " +
                            "WHERE id = ?";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setDouble(1, jugador.getX());
                        ps.setDouble(2, jugador.getY());
                        ps.setString(3, jugador.getTipo().name());
                        ps.setString(4, jugador.getRango());
                        ps.setInt(5, jugador.getId());

                        ps.executeUpdate();
                    }
                }

                inventarioDAO.actualizarInventario(
                        inventario, inventarioId, conn
                );

                inventarioDAO.reemplazarItems(
                        inventario, inventarioId, conn
                );

                equipoDAO.guardarPokemonesCapturados(
                        jugador, conn
                );

                equipoDAO.guardarEquipo(jugador, conn);

                Pokemon activo = jugador.getPokemonActivo();

                if (activo != null) {
                    pokemonDAO.guardarPokemon(
                            activo, jugador.getId(), conn
                    );
                }

                String sqlActivo =
                        "UPDATE jugadores SET pokemon_activo_id = ? WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sqlActivo)) {

                    if (activo != null) {
                        ps.setInt(1, activo.getId());
                    } else {
                        ps.setNull(1, Types.INTEGER);
                    }

                    ps.setInt(2, jugador.getId());
                    ps.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al guardar partida: " + e.getMessage()
            );
        }
    }

    /**
     * Comprueba si hay espacio para una nueva partida.
     */
    public boolean hayLugarDisponible() {

        String sql = "SELECT COUNT(*) FROM jugadores";

        try (Connection conn = Conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) < MAX_PARTIDAS;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al consultar cantidad de partidas: "
                            + e.getMessage()
            );
        }

        return false;
    }

    private int contarPartidas(Connection conn) throws SQLException {

        String sql = "SELECT COUNT(*) FROM jugadores";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Elimina una partida y sus datos relacionados.
     */
    public boolean eliminarPartida(int jugadorId) {

        String sqlInventario =
                "SELECT inventario_id FROM jugadores WHERE id = ?";

        try (Connection conn = Conexion.crearConexion()) {

            conn.setAutoCommit(false);

            try {

                int inventarioId;

                try (PreparedStatement ps =
                             conn.prepareStatement(sqlInventario)) {

                    ps.setInt(1, jugadorId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "No existe el jugador con ID: " + jugadorId
                            );
                        }

                        inventarioId = rs.getInt("inventario_id");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE jugadores SET pokemon_activo_id = NULL WHERE id = ?")) {

                    ps.setInt(1, jugadorId);
                    ps.executeUpdate();
                }

                eliminarPorJugador(
                        conn,
                        "DELETE FROM jugador_equipo WHERE jugador_id = ?",
                        jugadorId
                );

                eliminarPorJugador(
                        conn,
                        "DELETE FROM jugador_pokemones_capturados WHERE jugador_id = ?",
                        jugadorId
                );

                eliminarPorJugador(
                        conn,
                        "DELETE FROM pokemones WHERE jugador_id = ?",
                        jugadorId
                );

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM jugadores WHERE id = ?")) {

                    ps.setInt(1, jugadorId);

                    int filasEliminadas = ps.executeUpdate();

                    if (filasEliminadas == 0) {
                        throw new SQLException(
                                "No se eliminó ningún jugador con ID: " + jugadorId
                        );
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM inventario_items WHERE inventario_id = ?")) {

                    ps.setInt(1, inventarioId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM inventarios WHERE id = ?")) {

                    ps.setInt(1, inventarioId);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al eliminar partida: " + e.getMessage()
            );
            return false;
        }
    }

    private void eliminarPorJugador(
            Connection conn,
            String sql,
            int jugadorId
    ) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jugadorId);
            ps.executeUpdate();
        }
    }

    /**
     * Devuelve las partidas disponibles para cargar.
     */
    public Map<Integer, String> obtenerPartidasDisponibles() {

        Map<Integer, String> partidas = new LinkedHashMap<>();

        String sql =
                "SELECT id, tipo, rango FROM jugadores ORDER BY id DESC LIMIT 3";

        try (Connection conn = Conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                String rango = rs.getString("rango");

                partidas.put(
                        id,
                        "Partida #" + id + " | " + rango + " | " + tipo
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al consultar partidas: " + e.getMessage()
            );
        }

        return partidas;
    }

    /**
     * Recupera un jugador y todos los datos asociados a su partida.
     */
    public Jugador recuperarJugador(int jugadorId) {

        String sql = "SELECT * FROM jugadores WHERE id = ?";

        try (Connection conn = Conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                double x = rs.getDouble("x");
                double y = rs.getDouble("y");

                TipoJugador tipo =
                        TipoJugador.valueOf(rs.getString("tipo"));

                String rango = rs.getString("rango");
                int inventarioId = rs.getInt("inventario_id");

                int pokemonActivoId =
                        rs.getInt("pokemon_activo_id");

                boolean tienePokemonActivo = !rs.wasNull();

                Inventario inventario =
                        inventarioDAO.recuperarInventario(
                                inventarioId, conn
                        );

                LinkedHashSet<String> capturados =
                        equipoDAO.recuperarPokemonesCapturados(
                                jugadorId, conn
                        );

                Map<Integer, Pokemon> pokemones = new HashMap<>();

                List<Pokemon> equipo =
                        equipoDAO.recuperarEquipo(
                                jugadorId, conn, pokemones
                        );

                Pokemon activo = null;

                if (tienePokemonActivo) {
                    activo = pokemonDAO.recuperarPokemon(
                            pokemonActivoId,
                            jugadorId,
                            conn,
                            pokemones
                    );
                }

                return new Jugador(
                        jugadorId,
                        x,
                        y,
                        tipo,
                        capturados,
                        equipo,
                        rango,
                        activo,
                        inventario
                );
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al recuperar jugador: " + e.getMessage()
            );
            return null;
        }
    }
}
