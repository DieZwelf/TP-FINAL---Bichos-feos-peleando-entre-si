package vista;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PantallaCargarPartida implements IPantalla{

    private final GestorPantallas pantallas;

    public PantallaCargarPartida (GestorPantallas pantallas) {
        this.pantallas = pantallas;
    }

    @Override
    public void mostrar(Stage stage) {
        Pane root = new Pane();
        Scene escena = new Scene(root, 900, 650);

        pantallas.mostrarEscena(escena);
    }
}
