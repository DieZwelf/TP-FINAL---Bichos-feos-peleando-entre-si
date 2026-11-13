package DAO;
import java.sql.*;
import java.util.List;
import java.util.Map;

import model.*;
import conexion.Conexion;

public class guardarPartidaDAO {



        public void guardarJugador(Jugador jugador) {


            try(Connection conn = Conexion.crearConexion()) {
                // 1. Insertar Inventario y obtener su ID
                int inventarioId = insertarInventario(jugador.getInventario(), conn);
                if (jugador.getInventario() != null) {
                    jugador.getInventario().setId(inventarioId);
                    insertarItemsInventario(jugador.getInventario(), inventarioId, conn);
                }

                // 2. Insertar Jugador (inicialmente con pokemon_activo_id = NULL) y obtener su ID
                String sqlJugador = "INSERT INTO jugadores (x, y, tipo, rango, inventario_id, pokemon_activo_id) VALUES (?, ?, ?, ?, ?, NULL)";
                try (PreparedStatement ps = conn.prepareStatement(sqlJugador, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDouble(1, jugador.getX());
                    ps.setDouble(2, jugador.getY());
                    ps.setString(3, jugador.getTipo().name()); // "USUARIO" o "ADMINISTRADOR"
                    ps.setString(4, jugador.getRango());
                    ps.setInt(5, inventarioId);
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            jugador.setId(rs.getInt(1)); // Asignamos el ID generado al objeto Jugador
                        } else {
                            throw new SQLException("No se pudo obtener el ID generado para el jugador.");
                        }
                    }
                }

                // 3. Insertar Pokemones capturados y asignarles su ID
                if (jugador.getPokemonesCapturados() != null && !jugador.getPokemonesCapturados().isEmpty()) {
                    String sqlCapturados = "INSERT INTO jugador_pokemones_capturados (jugador_id, nombre_pokemon, orden) VALUES (?, ?, ?)";
                    try (PreparedStatement psCap = conn.prepareStatement(sqlCapturados)) {
                        int orden = 0;
                        for (String nombrePokemon : jugador.getPokemonesCapturados()) {
                            psCap.setInt(1, jugador.getId());
                            psCap.setString(2, nombrePokemon);
                            psCap.setInt(3, orden++);
                            psCap.addBatch();
                        }
                        psCap.executeBatch();
                    }
                }

                // 4. Insertar Equipo (List<Pokemon>) y asignar IDs si alguno no estaba en capturados
                if (jugador.getEquipo() != null) {
                    List<Pokemon> equipo = jugador.getEquipo();
                    String sqlEquipo = "INSERT INTO jugador_equipo (jugador_id, slot, pokemon_id) VALUES (?, ?, ?)";
                    try (PreparedStatement psEquipo = conn.prepareStatement(sqlEquipo)) {
                        for (int slot = 0; slot < equipo.size(); slot++) {
                            Pokemon p = equipo.get(slot);
                            if (p != null) {
                                if (p.getId() == 0) {
                                    insertarPokemon(p, jugador.getId(), conn);
                                }
                                psEquipo.setInt(1, jugador.getId());
                                psEquipo.setInt(2, slot);
                                psEquipo.setInt(3, p.getId());
                                psEquipo.addBatch();
                            }
                        }
                        psEquipo.executeBatch();
                    }
                }

                // 5. Actualizar pokemon_activo_id en la tabla jugadores
                Pokemon activo = jugador.getPokemonActivo();
                if (activo != null) {
                    if (activo.getId() == 0) {
                        insertarPokemon(activo, jugador.getId(), conn);
                    }
                    String sqlUpdateActivo = "UPDATE jugadores SET pokemon_activo_id = ? WHERE id = ?";
                    try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateActivo)) {
                        psUpdate.setInt(1, activo.getId());
                        psUpdate.setInt(2, jugador.getId());
                        psUpdate.executeUpdate();
                    }
                }


            } catch (SQLException e) {
                // Si algo falla, deshacemos todos los cambios en la BD
                System.out.println("Error de SQL: " + e.getMessage());
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



}

