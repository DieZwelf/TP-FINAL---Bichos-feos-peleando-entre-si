package vista.menu;

import vista.GestorPantallas;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import vista.base.PantallaBase;

/** Pantalla provisional: la carga desde archivos todavía no está implementada. */
public class PantallaCargarPartida extends PantallaBase {

    public PantallaCargarPartida(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected Parent crearContenido() {
        Label titulo = new Label("CARGAR PARTIDA");
        Label aviso = new Label("Todavía no hay partidas guardadas para cargar.");
        Button volver = new Button("VOLVER");
        volver.setOnAction(e -> pantallas.mostrarInicio());

        VBox root = new VBox(20, titulo, aviso, volver);
        root.setAlignment(Pos.CENTER);
        return root;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
