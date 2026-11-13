package vista.menu;

import DAO.guardarPartidaDAO;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class PantallaCargarPartida extends PantallaBase {

    private final guardarPartidaDAO partidaDAO = new guardarPartidaDAO();

    public PantallaCargarPartida(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected Parent crearContenido() {

        Label titulo = new Label("CARGAR PARTIDA");

        VBox listaPartidas = new VBox(15);
        listaPartidas.setAlignment(Pos.CENTER);

        Map<Integer, String> partidas = partidaDAO.obtenerPartidasDisponibles();

        if (partidas.isEmpty()) {

            Label aviso = new Label("Todavía no hay partidas guardadas para cargar.");

            listaPartidas.getChildren().add(aviso);

        } else {

            for (Map.Entry<Integer, String> partida : partidas.entrySet()) {

                int id = partida.getKey();

                Label informacion = new Label(partida.getValue());

                Button cargar = new Button("CARGAR");

                cargar.setOnAction(e -> pantallas.cargarPartida(id));

                HBox fila = new HBox(15, informacion, cargar);
                fila.setAlignment(Pos.CENTER);

                listaPartidas.getChildren().add(fila);
            }
        }

        Button volver = new Button("VOLVER");
        volver.setOnAction(e -> pantallas.mostrarInicio());

        VBox root = new VBox(20, titulo, listaPartidas, volver);
        root.setAlignment(Pos.CENTER);

        return root;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}