package vista;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class PantallaInventario implements IPantalla {

    private GestorPantallas pantallas;
    private ListView<String> listaVista;

    public PantallaInventario(GestorPantallas pantallas) {
        this.pantallas = pantallas;
        listaVista = new ListView<>();
    }

    @Override
    public void mostrar(Stage stage) {

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));

        Label titulo = new Label("INVENTARIO");
        titulo.setFont(new Font(28));

        listaVista.setPrefSize(400, 350);

        // OBJETOS DE PRUEBA
        actualizarInventario(List.of(
                "Poción de vida x3",
                "Poción de energía x2",
                "Pokeball x5",
                "Super poción x1",
                "Antídoto x4"
        ));

        Button volver = new Button("Volver");
        volver.setOnAction(e -> pantallas.regresarAlMapa());

        root.getChildren().addAll(
                titulo,
                listaVista,
                volver
        );

        Scene escena = new Scene(root, 900, 650);

        pantallas.mostrarEscena(escena);
    }

    public ListView<String> getListaObjetos() {
        return listaVista;
    }

    public void actualizarInventario(List<String> objetos) {
        listaVista.getItems().clear();
        listaVista.getItems().addAll(objetos);
    }
}
