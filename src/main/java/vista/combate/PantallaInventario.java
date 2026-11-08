package vista.combate;

import controlador.InventarioViewModel;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.Entrenador;
import model.Item;

import java.util.ArrayList;
import java.util.List;

public class PantallaInventario extends PantallaBase {

    private final Entrenador entrenador;
    private final InventarioViewModel viewModel;
    private ListView<String> listaVista;

    public PantallaInventario(GestorPantallas pantallas, Entrenador entrenador) {
        super(pantallas);
        this.entrenador = entrenador;
        this.viewModel = new InventarioViewModel(entrenador.getInventario(), this);
    }

    @Override
    protected Parent crearContenido() {
        listaVista = new ListView<>();
        listaVista.setPrefSize(400, 350);

        Label titulo = new Label("INVENTARIO");
        titulo.setFont(new Font(28));
        Button usar = new Button("Usar objeto");
        Button volver = new Button("Volver");

        usar.setOnAction(e -> usarObjetoSeleccionado());
        volver.setOnAction(e -> pantallas.volverDesdeInventario());

        VBox root = new VBox(15, titulo, listaVista, usar, volver);
        root.setPadding(new Insets(30));
        viewModel.actualizarVista();
        return root;
    }

    private void usarObjetoSeleccionado() {
        int indice = listaVista.getSelectionModel().getSelectedIndex();
        List<Item> items = viewModel.obtenerItemsOrdenados();
        if (indice < 0 || indice >= items.size()) return;

        List<String> logs = new ArrayList<>();
        boolean usado = viewModel.usarItem(items.get(indice), entrenador, logs);
        logs.forEach(System.out::println);
        if (!usado && logs.isEmpty()) {
            System.out.println("No se pudo utilizar el objeto.");
        }
    }

    public void actualizarInventario(List<String> objetos) {
        listaVista.getItems().setAll(objetos);
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
