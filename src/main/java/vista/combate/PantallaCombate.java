package vista.combate;

import controlador.CombateViewModel;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.AccionJugador;

import java.net.URL;

public class PantallaCombate extends PantallaBase {

    private final CombateViewModel viewModel;

    private ProgressBar barraHpJugador;
    private ProgressBar barraHpEnemigo;
    private Label lblNombreJugador;
    private Label lblNivelJugador;
    private Label lblMensajeActual;

    public PantallaCombate(CombateViewModel viewModel, GestorPantallas pantallas) {
        super(pantallas);
        this.viewModel = viewModel;
    }

    @Override
    protected Parent crearContenido() {
        StackPane root = new StackPane();
        root.getStyleClass().add("pantalla-combate-root");
        inicializarComponentes();
        root.getChildren().addAll(crearFondo(root), crearHudEnemigo(), crearPanelInferior());
        return root;
    }

    private void inicializarComponentes() {
        barraHpJugador = crearBarraVida();
        barraHpEnemigo = crearBarraVida();

        lblNombreJugador = crearLabel("texto-retro");
        lblNivelJugador = crearLabel("texto-retro");
        lblMensajeActual = crearLabel("texto-retro");
    }

    private ProgressBar crearBarraVida() {
        ProgressBar barra = new ProgressBar(1.0);
        barra.setPrefWidth(180);
        barra.getStyleClass().add("barra-hp");
        return barra;
    }

    private Label crearLabel(String estilo) {
        Label label = new Label();
        label.getStyleClass().add(estilo);
        return label;
    }

    private ImageView crearFondo(StackPane root) {
        URL recurso = getClass().getResource("/images/mapa/escenario_prueba.jpg");
        if (recurso == null) {
            throw new IllegalStateException("No se encontró /images/mapa/escenario_prueba.jpg");
        }
        ImageView fondo = new ImageView(new Image(recurso.toExternalForm()));
        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());
        return fondo;
    }

    private VBox crearHudEnemigo() {
        VBox hud = new VBox(5, new Label("Enemigo"), barraHpEnemigo);
        hud.getStyleClass().add("caja-hud");
        StackPane.setAlignment(hud, Pos.TOP_RIGHT);
        StackPane.setMargin(hud, new Insets(40, 50, 0, 0));
        return hud;
    }

    private HBox crearPanelInferior() {
        HBox datosJugador = new HBox(20, lblNombreJugador, lblNivelJugador);
        datosJugador.setAlignment(Pos.CENTER_LEFT);

        VBox miniHudJugador = new VBox(5, datosJugador, new Label("PS:"), barraHpJugador);
        miniHudJugador.getStyleClass().add("caja-hud");
        miniHudJugador.setMaxWidth(230);

        StackPane cajaDialogo = new StackPane(lblMensajeActual);
        cajaDialogo.getStyleClass().add("caja-dialogo-rpg");
        cajaDialogo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cajaDialogo, Priority.ALWAYS);

        GridPane botones = crearBotonera();
        HBox inferior = new HBox(15, miniHudJugador, cajaDialogo, botones);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(15));
        inferior.setMaxHeight(140);
        StackPane.setAlignment(inferior, Pos.BOTTOM_CENTER);
        StackPane.setMargin(inferior, new Insets(0, 20, 20, 20));
        return inferior;
    }

    private GridPane crearBotonera() {
        Button atacar = crearBoton("LUCHA");
        Button mochila = crearBoton("MOCHILA");
        Button huir = crearBoton("HUIR");
        Button equipo = crearBoton("POKEMONES");

        atacar.setOnAction(e -> viewModel.ejecutarAccion(AccionJugador.ATACAR));
        mochila.setOnAction(e -> pantallas.mostrarInventario());
        huir.setOnAction(e -> viewModel.ejecutarAccion(AccionJugador.HUIR));
        equipo.setOnAction(e -> pantallas.mostrarEquipo());

        GridPane panel = new GridPane();
        panel.setHgap(8);
        panel.setVgap(8);
        panel.setAlignment(Pos.CENTER);
        panel.add(atacar, 0, 0);
        panel.add(mochila, 1, 0);
        panel.add(huir, 0, 1);
        panel.add(equipo, 1, 1);
        return panel;
    }

    private Button crearBoton(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-retro");
        boton.setPrefSize(320, 80);
        return boton;
    }

    private void aplicarBindings() {
        barraHpJugador.progressProperty().bind(viewModel.hpJugadorProperty());
        barraHpEnemigo.progressProperty().bind(viewModel.hpEnemigoProperty());
        lblNombreJugador.textProperty().bind(viewModel.nombreJugadorProperty());
        lblNivelJugador.textProperty().bind(viewModel.nivelJugadorProperty());

        viewModel.getLogsCombate().addListener(
                (ListChangeListener<String>) cambio -> {
                    if (!viewModel.getLogsCombate().isEmpty()) {
                        lblMensajeActual.setText(viewModel.getLogsCombate()
                                .get(viewModel.getLogsCombate().size() - 1));
                    }
                }
        );
        if (!viewModel.getLogsCombate().isEmpty()) {
            lblMensajeActual.setText(viewModel.getLogsCombate()
                    .get(viewModel.getLogsCombate().size() - 1));
        }
    }

    @Override
    public void alMostrar() {
        viewModel.refrescarEstado();
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/estilos/estilo.css");
        aplicarBindings();
    }
}
