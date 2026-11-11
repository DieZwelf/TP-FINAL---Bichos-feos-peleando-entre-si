package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
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

        try(Connection con = Conexion.crearConexion()){
            con.setAutoCommit(false);


        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
