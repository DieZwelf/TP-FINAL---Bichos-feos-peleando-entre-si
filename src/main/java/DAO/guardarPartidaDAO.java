package DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import model.Jugador;
import conexion.Conexion;

public class guardarPartidaDAO {

    public static void guardarPartida(Jugador juagor){
        try(Connection con = Conexion.crearConexion()){
    int i;
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
