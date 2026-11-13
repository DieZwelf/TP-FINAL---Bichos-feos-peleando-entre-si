package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mysql://localhost:3306/bichos_feos";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "1234";

    public static final Connection crearConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
