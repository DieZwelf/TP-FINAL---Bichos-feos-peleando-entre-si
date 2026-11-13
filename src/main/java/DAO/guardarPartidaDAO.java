package DAO;
import java.sql.*;
import java.util.*;

import model.*;
import conexion.Conexion;

public class guardarPartidaDAO {

    public void guardarJugador(Jugador jugador) {

        try (Connection conn = Conexion.crearConexion()) {

            conn.setAutoCommit(false);

            try {
                Inventario inventario = jugador.getInventario();

                int inventarioId;

                if (jugador.getId() == 0) {

                    // partida nueva: crear inventario
                    inventarioId = insertarInventario(inventario, conn);
                    inventario.setId(inventarioId);

                    // Crear jugador
                    String sql = " INSERT INTO jugadores (x, y, tipo, rango, inventario_id, pokemon_activo_id) VALUES (?, ?, ?, ?, ?, NULL) ";

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
                                throw new SQLException("No se pudo crear el jugador.");
                            }
                        }
                    }

                } else {

                    // PARTIDA EXISTENTE: actualizar jugador
                    inventarioId = inventario.getId();

                    String sql = " UPDATE jugadores SET x = ?, y = ?, tipo = ?, rango = ? WHERE id = ? ";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setDouble(1, jugador.getX());
                        ps.setDouble(2, jugador.getY());
                        ps.setString(3, jugador.getTipo().name());
                        ps.setString(4, jugador.getRango());
                        ps.setInt(5, jugador.getId());

                        ps.executeUpdate();
                    }
                }

                // Actualizar inventario
                String sqlInventario = "UPDATE inventarios SET capacidad_maxima = ? WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sqlInventario)) {
                    ps.setInt(1, inventario.getCapacidad());
                    ps.setInt(2, inventarioId);
                    ps.executeUpdate();
                }

                // Reemplazar los ítems guardados
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inventario_items WHERE inventario_id = ?")) {
                    ps.setInt(1, inventarioId);
                    ps.executeUpdate();
                }

                insertarItemsInventario(inventario, inventarioId, conn);

                // Reemplazar Pokémon capturados.
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM jugador_pokemones_capturados WHERE jugador_id = ?")) {
                    ps.setInt(1, jugador.getId());
                    ps.executeUpdate();
                }

                if (jugador.getPokemonesCapturados() != null) {
                    String sqlCapturados = " INSERT INTO jugador_pokemones_capturados (jugador_id, nombre_pokemon, orden) VALUES (?, ?, ?) ";

                    try (PreparedStatement ps = conn.prepareStatement(sqlCapturados)) {
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

                // Reemplazar equipo
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM jugador_equipo WHERE jugador_id = ?")) {
                    ps.setInt(1, jugador.getId());
                    ps.executeUpdate();
                }

                List<Pokemon> equipo = jugador.getEquipo();

                if (equipo != null) {
                    String sqlEquipo = "INSERT INTO jugador_equipo (jugador_id, slot, pokemon_id) VALUES (?, ?, ?) ";

                    try (PreparedStatement ps = conn.prepareStatement(sqlEquipo)) {

                        for (int slot = 0; slot < equipo.size(); slot++) {
                            Pokemon p = equipo.get(slot);

                            if (p == null) continue;

                            guardarPokemon(p, jugador.getId(), conn);

                            ps.setInt(1, jugador.getId());
                            ps.setInt(2, slot);
                            ps.setInt(3, p.getId());
                            ps.addBatch();
                        }

                        ps.executeBatch();
                    }
                }

                // Actualizar Pokémon activo
                Pokemon activo = jugador.getPokemonActivo();

                if (activo != null) {
                    guardarPokemon(activo, jugador.getId(), conn);
                }

                String sqlActivo = "UPDATE jugadores SET pokemon_activo_id = ? WHERE id = ?";

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
            System.out.println("Error al guardar partida: " + e.getMessage());
        }
    }

    private void guardarPokemon(Pokemon p, int jugadorId, Connection conn) throws SQLException {

        if (p.getId() == 0) {
            insertarPokemon(p, jugadorId, conn);
            return;
        }

        String sql = " UPDATE pokemones SET nombre = ?, vida_maxima = ?, vida = ?, danio = ?, defensa = ?, tipo = ?, nivel = ?, experiencia = ?, danio_base = ?, defensa_base = ? WHERE id = ? AND jugador_id = ? ";

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
            ps.setInt(11, p.getId());
            ps.setInt(12, jugadorId);

            ps.executeUpdate();
        }
    }

        private int insertarInventario(Inventario inv, Connection conn) throws SQLException {
            String sql = "INSERT INTO inventarios (capacidad_maxima) VALUES (?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                int capacidad = (inv != null) ? inv.getCapacidad() : 50;
                ps.setInt(1, capacidad);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                    throw new SQLException("No se pudo obtener el ID generado para el inventario.");
                }
            }
        }

    private void insertarItemsInventario(Inventario inv, int inventarioId, Connection conn) throws SQLException {
        if (inv.getItems() == null || inv.getItems().isEmpty()) return;

        String sql = "INSERT INTO inventario_items (inventario_id, tipo_item, nombre, descripcion, cantidad, curacion, ocupada) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<Item, Integer> entry : inv.getItems().entrySet()) {
                Item item = entry.getKey();
                int cantidad = entry.getValue();

                ps.setInt(1, inventarioId);
                ps.setString(3, item.getNombre());
                ps.setString(4, item.getDescripcion());
                ps.setInt(5, cantidad);

                if (item instanceof Pocion pocion) {
                    ps.setString(2, "POCION");
                    ps.setInt(6, (int)pocion.getCuracion());
                    ps.setNull(7, Types.BOOLEAN);
                } else if (item instanceof Pokebola pokebola) {
                    ps.setString(2, "POKEBOLA");
                    ps.setNull(6, Types.INTEGER);
                    ps.setBoolean(7, pokebola.isOcupada());
                } else {
                    throw new SQLException("Tipo de ítem desconocido: " + item.getClass().getName());
                }

                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertarPokemon(Pokemon p, int jugadorId, Connection conn) throws SQLException {
            String sql = "INSERT INTO pokemones (jugador_id, nombre, vida_maxima, vida, danio, defensa, tipo, nivel, experiencia, danio_base, defensa_base) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, jugadorId);
                ps.setString(2, p.getNombre());
                ps.setDouble(3, p.getVidaMaxima());
                ps.setDouble(4, p.getVida());
                ps.setDouble(5, p.getDanio());
                ps.setDouble(6, p.getDefensa());
                ps.setString(7, p.getTipo().name());
                ps.setInt(8, p.getNivel());
                ps.setInt(9, p.getExperiencia());
                ps.setInt(10, (int)p.getDanioBase());
                ps.setInt(11, (int)p.getDefensaBase());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1)); // Asignamos el ID generado al objeto Pokemon
                    } else {
                        throw new SQLException("No se pudo obtener el ID generado para el Pokémon: " + p.getNombre());
                    }
                }
            }
        }

    public Map<Integer, String> obtenerPartidasDisponibles() {
        Map<Integer, String> partidas = new LinkedHashMap<>();

        String sql = "SELECT id, tipo, rango FROM jugadores ORDER BY id DESC LIMIT 3";

        try (Connection conn = Conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo");
                String rango = rs.getString("rango");

                partidas.put(id, "Partida #" + id + " | " + rango + " | " + tipo);
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar partidas: " + e.getMessage());
        }

        return partidas;
    }

    public Jugador recuperarJugador(int jugadorId) {

        String sql = " SELECT * FROM jugadores WHERE id = ?";

        try (Connection conn = Conexion.crearConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                double x = rs.getDouble("x");
                double y = rs.getDouble("y");

                TipoJugador tipo = TipoJugador.valueOf(rs.getString("tipo"));

                String rango = rs.getString("rango");
                int inventarioId = rs.getInt("inventario_id");

                int pokemonActivoId = rs.getInt("pokemon_activo_id");
                boolean tienePokemonActivo = !rs.wasNull();

                // Recuperar inventario.
                Inventario inventario = recuperarInventario(inventarioId, conn);

                // Recuperar Pokémon capturados.
                LinkedHashSet<String> capturados = recuperarPokemonesCapturados(jugadorId, conn);

                // Recuperar equipo.
                Map<Integer, Pokemon> pokemones = new HashMap<>();

                List<Pokemon> equipo = recuperarEquipo(jugadorId, conn, pokemones);

                // Recuperar Pokémon activo.
                Pokemon activo = null;

                if (tienePokemonActivo) {
                    activo = recuperarPokemon(pokemonActivoId, jugadorId, conn, pokemones);
                }

                return new Jugador(jugadorId, x, y, tipo, capturados, equipo, rango, null, inventario);
            }

        } catch (SQLException e) {
            System.out.println("Error al recuperar jugador: " + e.getMessage());
            return null;
        }
    }

    private Inventario recuperarInventario(int inventarioId, Connection conn) throws SQLException {

        String sql = "SELECT capacidad_maxima FROM inventarios WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, inventarioId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                HashMap<Item, Integer> items = new HashMap<>();

                Inventario inventario = new Inventario(items);
                inventario.setId(inventarioId);

                recuperarItemsInventario(inventario, inventarioId, conn);

                return inventario;
            }
        }
    }

    private void recuperarItemsInventario(Inventario inventario, int inventarioId, Connection conn) throws SQLException {

        String sql = " SELECT * FROM inventario_items WHERE inventario_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, inventarioId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String tipo = rs.getString("tipo_item");
                    int cantidad = rs.getInt("cantidad");

                    Item item;

                    if (tipo.equals("POCION")) {

                        item = new Pocion(rs.getDouble("curacion"));

                    } else if (tipo.equals("POKEBOLA")) {

                        item = new Pokebola(
                                rs.getString("nombre"),
                                rs.getString("descripcion"),
                                rs.getBoolean("ocupada"));

                    } else {
                        throw new SQLException("Tipo de objeto desconocido: " + tipo);
                    }

                    inventario.getItems().put(item, cantidad);
                }
            }
        }
    }

    private LinkedHashSet<String> recuperarPokemonesCapturados(int jugadorId, Connection conn) throws SQLException {

        LinkedHashSet<String> capturados = new LinkedHashSet<>();

        String sql = " SELECT nombre_pokemon FROM jugador_pokemones_capturados WHERE jugador_id = ? ORDER BY orden ";

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

    private List<Pokemon> recuperarEquipo(int jugadorId, Connection conn, Map<Integer, Pokemon> pokemones) throws SQLException {

        List<Pokemon> equipo = new ArrayList<>();

        String sql = " SELECT slot, pokemon_id FROM jugador_equipo WHERE jugador_id = ? ORDER BY slot";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jugadorId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    int slot = rs.getInt("slot");
                    int pokemonId = rs.getInt("pokemon_id");

                    Pokemon pokemon = recuperarPokemon(pokemonId, jugadorId, conn, pokemones);

                    // Mantiene la posición original del Pokémon.
                    while (equipo.size() <= slot) {
                        equipo.add(null);
                    }

                    equipo.set(slot, pokemon);
                }
            }
        }

        return equipo;
    }

    private Pokemon recuperarPokemon(int pokemonId, int jugadorId, Connection conn, Map<Integer, Pokemon> pokemones) throws SQLException {

        // Si ya fue recuperado, reutiliza el mismo objeto.
        if (pokemones.containsKey(pokemonId)) {
            return pokemones.get(pokemonId);
        }

        String sql = " SELECT * FROM pokemones WHERE id = ? AND jugador_id = ?";

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

                pokemon.setId(pokemonId);

                pokemones.put(pokemonId, pokemon);

                return pokemon;
            }
        }
    }
}

