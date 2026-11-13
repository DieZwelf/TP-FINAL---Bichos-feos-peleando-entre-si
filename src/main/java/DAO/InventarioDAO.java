
package DAO;

import model.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona la persistencia de los inventarios y sus ítems.
 */
public class InventarioDAO {

    /**
     * Actualiza la capacidad del inventario.
     */
    public void actualizarInventario(
            Inventario inventario,
            int inventarioId,
            Connection conn
    ) throws SQLException {

        String sql =
                "UPDATE inventarios SET capacidad_maxima = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, inventario.getCapacidad());
            ps.setInt(2, inventarioId);
            ps.executeUpdate();
        }
    }

    /**
     * Elimina los ítems anteriores e inserta los actuales.
     */
    public void reemplazarItems(
            Inventario inventario,
            int inventarioId,
            Connection conn
    ) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM inventario_items WHERE inventario_id = ?")) {

            ps.setInt(1, inventarioId);
            ps.executeUpdate();
        }

        insertarItemsInventario(inventario, inventarioId, conn);
    }

    /**
     * Inserta un inventario nuevo y devuelve su identificador.
     */
    public int insertarInventario(
            Inventario inv,
            Connection conn
    ) throws SQLException {

        String sql =
                "INSERT INTO inventarios (capacidad_maxima) VALUES (?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            int capacidad = (inv != null) ? inv.getCapacidad() : 50;

            ps.setInt(1, capacidad);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new SQLException(
                        "No se pudo obtener el ID generado para el inventario."
                );
            }
        }
    }

    /**
     * Inserta todos los ítems de un inventario.
     */
    private void insertarItemsInventario(
            Inventario inv,
            int inventarioId,
            Connection conn
    ) throws SQLException {

        if (inv.getItems() == null || inv.getItems().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO inventario_items " +
                "(inventario_id, tipo_item, nombre, descripcion, cantidad, curacion, ocupada) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<Item, Integer> entry :
                    inv.getItems().entrySet()) {

                Item item = entry.getKey();
                int cantidad = entry.getValue();

                ps.setInt(1, inventarioId);
                ps.setString(3, item.getNombre());
                ps.setString(4, item.getDescripcion());
                ps.setInt(5, cantidad);

                if (item instanceof Pocion pocion) {

                    ps.setString(2, "POCION");
                    ps.setInt(6, (int) pocion.getCuracion());
                    ps.setNull(7, Types.BOOLEAN);

                } else if (item instanceof Pokebola pokebola) {

                    ps.setString(2, "POKEBOLA");
                    ps.setNull(6, Types.INTEGER);
                    ps.setBoolean(7, pokebola.isOcupada());

                } else {

                    throw new SQLException(
                            "Tipo de ítem desconocido: "
                                    + item.getClass().getName()
                    );
                }

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    /**
     * Recupera un inventario y sus ítems.
     */
    public Inventario recuperarInventario(
            int inventarioId,
            Connection conn
    ) throws SQLException {

        String sql =
                "SELECT capacidad_maxima FROM inventarios WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, inventarioId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                HashMap<Item, Integer> items = new HashMap<>();

                Inventario inventario = new Inventario(items);
                inventario.setId(inventarioId);

                recuperarItemsInventario(
                        inventario,
                        inventarioId,
                        conn
                );

                return inventario;
            }
        }
    }

    /**
     * Recupera los objetos almacenados en un inventario.
     */
    private void recuperarItemsInventario(
            Inventario inventario,
            int inventarioId,
            Connection conn
    ) throws SQLException {

        String sql =
                "SELECT * FROM inventario_items WHERE inventario_id = ?";

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
                                rs.getBoolean("ocupada")
                        );

                    } else {

                        throw new SQLException(
                                "Tipo de objeto desconocido: " + tipo
                        );
                    }

                    inventario.getItems().put(item, cantidad);
                }
            }
        }
    }
}
