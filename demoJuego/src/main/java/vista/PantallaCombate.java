package vista;

import controlador.CombateViewModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.AccionJugador;

import java.net.URL;
import java.util.Objects;

public class PantallaCombate extends StackPane implements IPantalla {

    private final CombateViewModel viewModel;

    private ProgressBar barraHpJugador;
    private ProgressBar barraHpEnemigo;
    private Label lblNombreJugador;
    private Label lblNivelJugador;
    private Label lblMensajeActual;

    private Button btnAtacar;
    private Button btnMochila;
    private Button btnHuir;
    private Button btnEquipo;
    private GestorPantallas pantallas;

    public PantallaCombate(CombateViewModel viewModel, GestorPantallas pantallas) {
        this.viewModel = viewModel;
        this.pantallas = pantallas;

        inicializarComponentes();
        organizarLayout();
        aplicarBindingsYListeners();
        cargarEstilosCSS();
    }

    @Override
    public void mostrar(Stage stage) {
        Scene scene = new Scene(this, 1000, 800);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

    }

    private void inicializarComponentes() {
        this.getStyleClass().add("pantalla-combate-root");

        barraHpJugador = new ProgressBar(1.0);
        barraHpEnemigo = new ProgressBar(1.0);

        barraHpJugador.getStyleClass().add("barra-hp");
        barraHpEnemigo.getStyleClass().add("barra-hp");
        barraHpJugador.setPrefWidth(180);
        barraHpEnemigo.setPrefWidth(180);

        lblNombreJugador = new Label();
        lblNivelJugador = new Label();
        lblNombreJugador.getStyleClass().add("texto-retro");
        lblNivelJugador.getStyleClass().add("texto-retro");

        lblMensajeActual = new Label();
        lblMensajeActual.getStyleClass().add("texto-retro");

        // Botones con estilo retro
        btnAtacar = new Button("LUCHA");
        btnMochila = new Button("MOCHILA");
        btnHuir = new Button("HUIR");
        btnEquipo = new Button("POKEMONES");

        btnAtacar.getStyleClass().add("boton-retro");
        btnMochila.getStyleClass().add("boton-retro");
        btnHuir.getStyleClass().add("boton-retro");
        btnEquipo.getStyleClass().add("boton-retro");

        btnAtacar.setPrefSize(320, 80);
        btnMochila.setPrefSize(320, 80);
        btnHuir.setPrefSize(320, 80);
        btnEquipo.setPrefSize(320, 80);

        // Acciones
        btnAtacar.setOnAction(e -> viewModel.ejecutarAccion(AccionJugador.ATACAR));
        btnHuir.setOnAction(e -> {viewModel.ejecutarAccion(AccionJugador.HUIR); pantallas.regresarAlMapa();});
        btnEquipo.setOnAction( e -> {viewModel.ejecutarAccion(AccionJugador.ELEGIR_POKEMON); pantallas.mostrarEquipo();});

    }

    private void organizarLayout() {
        // 1. HUD del Pokémon Enemigo (Arriba a la derecha)
        VBox hudEnemigo = new VBox(5, new Label("Enemigo"), barraHpEnemigo);
        hudEnemigo.getStyleClass().add("caja-hud");
        StackPane.setAlignment(hudEnemigo, Pos.TOP_RIGHT);
        StackPane.setMargin(hudEnemigo, new Insets(40, 50, 0, 0));

        // 2. MINI-HUD del Pokémon del Jugador (Abajo a la izquierda, sobre el panel de texto)
        HBox datosSuperioresHud = new HBox(20, lblNombreJugador, lblNivelJugador);
        datosSuperioresHud.setAlignment(Pos.CENTER_LEFT);

        VBox miniHudJugador = new VBox(5, datosSuperioresHud, new Label("PS:"), barraHpJugador);
        miniHudJugador.getStyleClass().add("caja-hud");
        miniHudJugador.setMaxWidth(230);

        // 3. Caja de diálogo central para los logs de combate
        StackPane cajaDialogo = new StackPane(lblMensajeActual);
        cajaDialogo.getStyleClass().add("caja-dialogo-rpg");
        cajaDialogo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cajaDialogo, Priority.ALWAYS);

        // 4. Menú de botones a la derecha
        GridPane panelBotonesMenu = new GridPane();
        panelBotonesMenu.setHgap(8);
        panelBotonesMenu.setVgap(8);
        panelBotonesMenu.setAlignment(Pos.CENTER);
        panelBotonesMenu.add(btnAtacar, 0, 0);
        panelBotonesMenu.add(btnMochila, 1, 0);
        panelBotonesMenu.add(btnHuir, 0, 1);
        panelBotonesMenu.add(btnEquipo,1 , 1);

        // Contenedor horizontal inferior unificado (Mini-HUD izq + Caja de texto centro + Botones der)
        HBox contenedorInferiorPrincipal = new HBox(15, miniHudJugador, cajaDialogo, panelBotonesMenu);
        contenedorInferiorPrincipal.setAlignment(Pos.CENTER);
        contenedorInferiorPrincipal.setPadding(new Insets(15));
        contenedorInferiorPrincipal.setMaxHeight(140);

        StackPane.setAlignment(contenedorInferiorPrincipal, Pos.BOTTOM_CENTER);
        StackPane.setMargin(contenedorInferiorPrincipal, new Insets(0, 20, 20, 20));

        URL fondoCombate = Objects.requireNonNull(getClass().getResource("/images/mapa/escenario_prueba.jpg"));

        Image imagen = new Image(fondoCombate.toExternalForm());

        ImageView vistaFondo = new ImageView(imagen);

        vistaFondo.setPreserveRatio(false);
        vistaFondo.fitWidthProperty().bind(this.widthProperty());
        vistaFondo.fitHeightProperty().bind(this.heightProperty());

        this.getChildren().addAll(vistaFondo,hudEnemigo, contenedorInferiorPrincipal);
    }

    private void aplicarBindingsYListeners() {
        // Bindings de barras de vida y textos del mini-HUD
        barraHpJugador.progressProperty().bind(viewModel.hpJugadorProperty());
        barraHpEnemigo.progressProperty().bind(viewModel.hpEnemigoProperty());
        lblNombreJugador.textProperty().bind(viewModel.nombreJugadorProperty());
        lblNivelJugador.textProperty().bind(viewModel.nivelJugadorProperty());

        // Listener para los logs de texto
        viewModel.getLogsCombate().addListener((ListChangeListener<String>) cambio -> {
            while (cambio.next()) {
                if (cambio.wasAdded() && !cambio.getAddedSubList().isEmpty()) {
                    String ultimoMensaje = cambio.getAddedSubList().get(cambio.getAddedSubList().size() - 1);
                    Platform.runLater(() -> lblMensajeActual.setText(ultimoMensaje));
                }
            }
        });

        if (!viewModel.getLogsCombate().isEmpty()) {
            lblMensajeActual.setText(viewModel.getLogsCombate().get(0));
        }
    }

    private void cargarEstilosCSS() {
        var cssUrl = getClass().getResource("/images/estilos/estilo.css");
        if (cssUrl != null) {
            this.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            var cssAlt = getClass().getResource("/estilo.css");
            if (cssAlt != null) {
                this.getStylesheets().add(cssAlt.toExternalForm());
            } else {
                System.err.println("ADVERTENCIA: No se encontró el archivo estilo.css en /styles/ o en la raíz.");
            }
        }
    }
}