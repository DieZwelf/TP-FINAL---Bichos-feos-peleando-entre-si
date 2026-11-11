package vista.combate;

import controlador.CombateViewModel;
import controlador.InventarioViewModel;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Entrenador;
import model.Item;

import java.util.ArrayList;
import java.util.List;

public class PantallaInventario extends PantallaBase {

    private final Entrenador entrenador;
    private final InventarioViewModel viewModel;
    private VBox listaItemsContainer;
    private Label lblMensajePantalla; // Etiqueta integrada para mostrar los avisos

    public PantallaInventario(GestorPantallas pantallas, Entrenador entrenador, CombateViewModel combateViewModel) {
        super(pantallas);
        this.entrenador = entrenador;
        this.viewModel = new InventarioViewModel(entrenador.getInventario(), this);
        if (combateViewModel != null) {
            this.viewModel.setCallbackUsarPokebola(combateViewModel::intentarUsarPokebola);
        }
    }

    @Override
    protected Parent crearContenido() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("pantalla-combate-root");

        // Título superior estilo retro
        Label titulo = new Label("INVENTARIO");
        titulo.getStyleClass().add("texto-retro");
        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(20, 0, 10, 0));

        // Etiqueta para mostrar los mensajes de aviso directamente en la pantalla
        lblMensajePantalla = new Label("");
        lblMensajePantalla.getStyleClass().add("texto-retro");
        lblMensajePantalla.setStyle("-fx-text-fill: #ffde00;"); // Color destacado para el aviso
        lblMensajePantalla.setMinHeight(30);

        VBox topContainer = new VBox(10, titulo, lblMensajePantalla);
        topContainer.setAlignment(Pos.CENTER);
        root.setTop(topContainer);

        // Contenedor central de tarjetas de objetos con ScrollPane
        listaItemsContainer = new VBox(15);
        listaItemsContainer.setPadding(new Insets(15));
        listaItemsContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(listaItemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );
        root.setCenter(scrollPane);

        // Botón inferior VOLVER
        Button volver = new Button("VOLVER");
        volver.getStyleClass().add("boton-retro");
        volver.setOnAction(e -> pantallas.volverDesdeInventario());

        HBox inferior = new HBox(volver);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(20));
        root.setBottom(inferior);

        // Cargamos los ítems iniciales
        viewModel.actualizarVista();

        return root;
    }

    public void actualizarInventario(List<String> objetos) {
        listaItemsContainer.getChildren().clear();
        List<Item> itemsReales = viewModel.obtenerItemsOrdenados();

        if (objetos.isEmpty() || itemsReales.isEmpty()) {
            Label vacio = new Label("No hay objetos en el inventario.");
            vacio.getStyleClass().add("texto-retro");
            listaItemsContainer.getChildren().add(vacio);
            return;
        }

        for (int i = 0; i < itemsReales.size(); i++) {
            Item item = itemsReales.get(i);
            String descripcionTexto = (i < objetos.size()) ? objetos.get(i) : item.getNombre();
            listaItemsContainer.getChildren().add(crearTarjetaItem(item, descripcionTexto));
        }
    }

    private HBox crearTarjetaItem(Item item, String descripcionTexto) {
        Label lblDescripcion = new Label(descripcionTexto);
        lblDescripcion.getStyleClass().add("texto-retro");
        lblDescripcion.setWrapText(true);

        VBox informacion = new VBox(5, lblDescripcion);
        informacion.setAlignment(Pos.CENTER_LEFT);

        Button usar = new Button("USAR");
        usar.getStyleClass().add("boton-equipo");

        // Al hacer clic, usamos el objeto y volcamos el texto en la etiqueta de pantalla
        usar.setOnAction(e -> usarObjeto(item));

        HBox tarjeta = new HBox(25, informacion, usar);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(650);
        tarjeta.getStyleClass().add("caja-hud");

        return tarjeta;
    }

    private void usarObjeto(Item item) {
        List<String> logs = new ArrayList<>();
        boolean usado = viewModel.usarItem(item, entrenador, logs);

        if (!logs.isEmpty()) {
            // Muestra el texto directamente en la etiqueta de la pantalla
            lblMensajePantalla.setText(logs.get(logs.size() - 1));
        } else {
            lblMensajePantalla.setText("");
        }

        viewModel.actualizarVista();
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}