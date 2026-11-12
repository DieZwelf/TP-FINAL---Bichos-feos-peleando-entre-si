package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.Entrenador;
import model.Jugador;
import conexion.Conexion;

public class guardarPartidaDAO {

    public static void guardarPartida(Jugador jugador){
        Entrenador entrenador = jugador.getEntrenador();

        String sqlEntrenador = "INSERT INTO entrenadores (id, nombre, rango, id_pokemon_activo) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), rango = VALUES(rango), id_pokemon_activo = VALUES(id_pokemon_activo)";

        String sqlPartida = "INSERT INTO partidas (x, y, tipo_jugador, id_entrenador) VALUES (?, ?, ?, ?)";

        String sqlLimpiarPokemones = "DELETE FROM pokemones WHERE id_entrenador = ?";
        String sqlPokemon = "INSERT INTO pokemones (id, id_entrenador, nombre, nivel, en_equipo) VALUES (?, ?, ?, ?, ?)";

        try(Connection con = Conexion.crearConexion()) {
            con.setAutoCommit(false);
            try {
                // 2. Guardar o actualizar el Entrenador (usando su atributo id ya generado)
                try (PreparedStatement psEnt = con.prepareStatement(sqlEntrenador)) {
                    psEnt.setInt(1, entrenador.getId());
                    psEnt.setString(2, entrenador.getNombre());
                    psEnt.setString(3, entrenador.getRango());

                    // Validamos por si aún no tiene un pokémon activo seleccionado
                    if (entrenador.getPokemonActivo() != null) {
                        psEnt.setInt(4, entrenador.getPokemonActivo().getId());
                    } else {
                        psEnt.setNull(4, java.sql.Types.INTEGER);
                    }
                    psEnt.executeUpdate();
                }
            }   catch (Exception e){
                System.out.println(e.getMessage());
            }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

