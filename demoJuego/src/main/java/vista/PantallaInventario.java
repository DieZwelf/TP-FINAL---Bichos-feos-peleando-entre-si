package vista;

import controlador.InventarioViewModel;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Entrenador;
import model.Item;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.scene.image.Image;

public class PantallaInventario implements IPantalla {

    private final GestorPantallas pantallas;
    private final InventarioViewModel viewModel;
    private ListView<String> listaVista;

    public PantallaInventario(GestorPantallas pantallas, Entrenador entrenador) {
        this.pantallas = pantallas;
        this.viewModel = new InventarioViewModel(entrenador.getInventario(), this);
        this.listaVista = new ListView<>();
    }

    @Override
    public void mostrar(Stage stage) {

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));

        Label titulo = new Label("INVENTARIO");
        titulo.setFont(new Font(28));

        listaVista.setPrefSize(400, 350);

        // Carga los datos reales desde el ViewModel
        viewModel.actualizarVista();

        Button volver = new Button("Volver");
        volver.setOnAction(e -> pantallas.volverDesdeInventario());

        Button usar = new Button("Usar objeto");
        usar.setOnAction(e -> {
            int indiceSeleccionado = listaVista.getSelectionModel().getSelectedIndex();

            if (indiceSeleccionado >= 0) {
                // Obtiene la lista ordenada de ítems reales usando el metodo que agregue en el ViewModel
                Item itemSeleccionado = viewModel.obtenerItemsOrdenados().get(indiceSeleccionado);

                List<String> logs = new ArrayList<>();

                // Le pasa el ítem real a usarItem(...)
                boolean usado = viewModel.usarItem(itemSeleccionado, pantallas.getEntrenador(), logs);

                if (usado) {
                    System.out.println("¡Ítem usado con éxito!");
                    for (String log : logs) {
                        System.out.println(log);
                    }
                } else {
                    System.out.println("No se pudo usar el ítem: " + logs);
                }
            }
        });

        root.getChildren().addAll(titulo, listaVista, usar, volver);

        Scene escena = new Scene(root, 900, 650);

        URL cssUrl = getClass().getResource("/images/pantallaMenu/estilo.css");
        if (cssUrl != null) {
            escena.getStylesheets().add(cssUrl.toExternalForm());
        }

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