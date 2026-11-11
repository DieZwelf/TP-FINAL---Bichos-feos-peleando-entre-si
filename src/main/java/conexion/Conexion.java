package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://172.16.1.64:3306/bichos_feos";
    private static final String USUARIO = "bdd1";
    private static final String PASSWORD = "bdd1";

    public static final Connection crearConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
