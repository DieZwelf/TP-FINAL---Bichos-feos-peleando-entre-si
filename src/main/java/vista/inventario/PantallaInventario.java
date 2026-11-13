package vista.inventario;

import controlador.CombateViewModel;
import controlador.InventarioViewModel;
import excepciones.PokebolaFueraDeCombateException;
import model.Pokebola;
import vista.gestor.GestorPantallas;
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
import model.Jugador;
import model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa la pantalla del inventario del jugador.
 * Permite visualizar los objetos disponibles, utilizarlos
 * y mostrar mensajes relacionados con sus acciones.
 * También controla el uso de Pokébolas, restringiéndolo
 * a los momentos en los que existe un combate activo.
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class PantallaInventario extends PantallaBase {

    private final Jugador jugador;
    private final InventarioViewModel viewModel;
    private VBox listaItemsContainer;
    private Label lblMensajePantalla;
    private CombateViewModel combateViewModel;

    /**
     * Construye la pantalla del inventario.
     * Inicializa el ViewModel y configura el callback
     * para utilizar Pokébolas cuando existe un combate.
     *
     * @param pantallas gestor de navegación entre pantallas
     * @param jugador jugador propietario del inventario
     * @param combateViewModel ViewModel del combate activo, si existe
     */
    public PantallaInventario(
            GestorPantallas pantallas,
            Jugador jugador,
            CombateViewModel combateViewModel
    ) {
        super(pantallas);
        this.jugador = jugador;
        this.viewModel = new InventarioViewModel(jugador.getInventario(), this);

        if (combateViewModel != null) {
            this.viewModel.setCallbackUsarPokebola(combateViewModel::intentarUsarPokebola);
        }

        this.combateViewModel = combateViewModel;
    }

    /**
     * Construye los componentes visuales de la pantalla del inventario.
     * Organiza el título, los mensajes, la lista de objetos
     * y el botón para regresar.
     *
     * @return contenedor principal de la pantalla
     */
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
        lblMensajePantalla.setStyle("-fx-text-fill: #ffde00;");
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
        scrollPane.setStyle("-fx-background: transparent;" + "-fx-background-color: transparent;");
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

    /**
     * Actualiza visualmente los objetos que aparecen en el inventario.
     * Si no existen objetos, muestra un mensaje indicando que el inventario
     * se encuentra vacío. En caso contrario, genera una tarjeta por objeto.
     *
     * @param objetos lista de descripciones de los objetos del inventario
     */
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

    /**
     * Construye la tarjeta visual correspondiente a un objeto.
     * Muestra su descripción y agrega un botón para utilizarlo.
     *
     * @param item objeto representado en la tarjeta
     * @param descripcionTexto descripción que se mostrará
     * @return contenedor horizontal con la información y el botón
     */
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

    /**
     * Ejecuta el uso de un objeto del inventario.
     * Impide utilizar Pokébolas fuera de combate, registra los mensajes
     * producidos por el ViewModel y actualiza la vista del inventario.
     *
     * @param item objeto que el jugador intenta utilizar
     */
    private void usarObjeto(Item item) {
        try {
            if (item instanceof Pokebola && combateViewModel == null) {
                throw new PokebolaFueraDeCombateException("¡No podés utilizar una Pokébola fuera de combate!");
            }

            List<String> logs = new ArrayList<>();
            viewModel.usarItem(item, jugador, logs);

            if (!logs.isEmpty()) {
                lblMensajePantalla.setText(logs.get(logs.size() - 1));
            } else {
                lblMensajePantalla.setText("");
            }

            viewModel.actualizarVista();

        } catch (PokebolaFueraDeCombateException ex) {
            lblMensajePantalla.setText(ex.getMessage());
        }
    }

    /**
     * Configura los estilos CSS correspondientes a la pantalla.
     *
     * @param escena escena sobre la que se aplican los estilos
     */
    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}