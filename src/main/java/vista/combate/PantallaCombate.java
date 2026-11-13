
package vista.combate;

import controlador.CombateViewModel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.*;
import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;
import vista.combate.animaciones.AnimacionCombate;
import vista.combate.animaciones.AnimacionResultado;

import java.net.URL;
import java.util.List;

public class PantallaCombate extends PantallaBase {

    private final CombateViewModel viewModel;

    private final AnimacionCombate animacionCombate =
            new AnimacionCombate();

    private final AnimacionResultado animacionResultado =
            new AnimacionResultado();

    private ProgressBar barraHpJugador;
    private ProgressBar barraHpEnemigo;

    private Label lblNombreJugador;
    private Label lblNivelJugador;
    private Label lblMensajeActual;

    private Label lblNombreEnemigo;
    private Label lblNivelEnemigo;

    private Image pokemonActivoImagen;
    private Image pokemonSalvajeImagen;

    private ImageView imagenPokemonActivo;
    private ImageView imagenPokemonSalvaje;

    private BorderPane interfazCombate;

    private boolean presentacionIniciada = false;
    private boolean animacionResultadosIniciada = false;
    private boolean bindingsAplicados = false;

    public PantallaCombate(
            CombateViewModel viewModel,
            GestorPantallas pantallas
    ) {
        super(pantallas);
        this.viewModel = viewModel;
    }

    @Override
    protected StackPane crearContenido() {
        StackPane root = new StackPane();

        root.getStyleClass().add("pantalla-combate-root");

        inicializarComponentes();
        crearSprites();

        ImageView fondo = crearFondo(root);
        Pane capaSprites = crearCapaSprites();

        interfazCombate = crearCapaInterfaz();

        interfazCombate.setOpacity(0);
        interfazCombate.setDisable(true);

        root.getChildren().addAll(
                fondo,
                capaSprites,
                interfazCombate
        );

        StackPane.setAlignment(capaSprites, Pos.TOP_LEFT);
        StackPane.setAlignment(interfazCombate, Pos.CENTER);

        interfazCombate.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        return root;
    }

    // -----------------------------------------------------
    // COMPONENTES
    // -----------------------------------------------------

    private void inicializarComponentes() {
        barraHpJugador = ComponentesCombate.crearBarraVida();
        barraHpEnemigo = ComponentesCombate.crearBarraVida();

        lblNombreJugador =
                ComponentesCombate.crearLabel("texto-retro");

        lblNivelJugador =
                ComponentesCombate.crearLabel("texto-retro");

        lblMensajeActual =
                ComponentesCombate.crearLabel("texto-retro");

        lblNombreEnemigo =
                ComponentesCombate.crearLabel("texto-retro");

        lblNivelEnemigo =
                ComponentesCombate.crearLabel("texto-retro");

        cargarImagenPokemon();
    }

    private void crearSprites() {
        imagenPokemonActivo = new ImageView(pokemonActivoImagen);
        imagenPokemonSalvaje = new ImageView(pokemonSalvajeImagen);

        imagenPokemonActivo.setFitWidth(700);
        imagenPokemonActivo.setFitHeight(700);
        imagenPokemonActivo.setPreserveRatio(true);
        imagenPokemonActivo.getStyleClass().add("imagen-pokemon");

        imagenPokemonSalvaje.setFitWidth(400);
        imagenPokemonSalvaje.setFitHeight(400);
        imagenPokemonSalvaje.setPreserveRatio(true);
        imagenPokemonSalvaje.getStyleClass().add("imagen-pokemon");
    }

    private Pane crearCapaSprites() {
        Pane capaSprites = new Pane();
        capaSprites.setMouseTransparent(true);

        imagenPokemonSalvaje.setLayoutX(710);
        imagenPokemonSalvaje.setLayoutY(160);

        imagenPokemonActivo.setLayoutX(90);
        imagenPokemonActivo.setLayoutY(120);

        capaSprites.getChildren().addAll(
                imagenPokemonSalvaje,
                imagenPokemonActivo
        );

        return capaSprites;
    }

    private ImageView crearFondo(Pane root) {
        URL recurso = getClass().getResource(
                "/images/mapa/escenario_prueba.jpg"
        );

        if (recurso == null) {
            throw new IllegalStateException(
                    "No se encontró /images/mapa/escenario_prueba.jpg"
            );
        }

        ImageView fondo = new ImageView(
                new Image(recurso.toExternalForm())
        );

        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());

        return fondo;
    }

    // -----------------------------------------------------
    // INTERFAZ
    // -----------------------------------------------------

    private BorderPane crearCapaInterfaz() {
        BorderPane interfaz = new BorderPane();
        interfaz.setPickOnBounds(false);

        VBox hudEnemigo = crearHudEnemigo();

        HBox contenedorHud = new HBox(hudEnemigo);
        contenedorHud.setAlignment(Pos.TOP_RIGHT);
        contenedorHud.setPadding(new Insets(40, 50, 0, 0));

        interfaz.setTop(contenedorHud);

        HBox panelInferior = crearPanelInferior();

        BorderPane.setAlignment(
                panelInferior,
                Pos.BOTTOM_CENTER
        );

        BorderPane.setMargin(
                panelInferior,
                new Insets(0, 20, 20, 20)
        );

        interfaz.setBottom(panelInferior);

        return interfaz;
    }

    private VBox crearHudEnemigo() {
        VBox hud = new VBox(
                5,
                lblNombreEnemigo,
                lblNivelEnemigo,
                barraHpEnemigo
        );

        hud.getStyleClass().add("caja-hud");

        return hud;
    }

    private HBox crearPanelInferior() {
        HBox datosJugador = new HBox(
                20,
                lblNombreJugador,
                lblNivelJugador
        );

        datosJugador.setAlignment(Pos.CENTER_LEFT);

        VBox miniHudJugador = new VBox(
                5,
                datosJugador,
                new Label("PS:"),
                barraHpJugador
        );

        miniHudJugador.getStyleClass().add("caja-hud");
        miniHudJugador.setMaxWidth(230);

        StackPane cajaDialogo = new StackPane(lblMensajeActual);
        cajaDialogo.getStyleClass().add("caja-dialogo-rpg");
        cajaDialogo.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(cajaDialogo, Priority.ALWAYS);

        GridPane botones = crearBotonera();

        HBox inferior = new HBox(
                15,
                miniHudJugador,
                cajaDialogo,
                botones
        );

        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(15));
        inferior.setMaxHeight(140);

        return inferior;
    }

    private GridPane crearBotonera() {
        Button atacar = ComponentesCombate.crearBoton("LUCHA");
        Button mochila = ComponentesCombate.crearBoton("MOCHILA");
        Button huir = ComponentesCombate.crearBoton("HUIR");
        Button equipo = ComponentesCombate.crearBoton("POKEMONES");

        atacar.setOnAction(event ->
                viewModel.ejecutarAccion(AccionJugador.ATACAR)
        );

        mochila.setOnAction(event ->
                pantallas.mostrarInventario(viewModel)
        );

        huir.setOnAction(event ->
                viewModel.ejecutarAccion(AccionJugador.HUIR)
        );

        equipo.setOnAction(event ->
                pantallas.mostrarEquipo()
        );

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

    // -----------------------------------------------------
    // BINDINGS
    // -----------------------------------------------------

    private void aplicarBindings() {
        if (bindingsAplicados) {
            return;
        }

        bindingsAplicados = true;

        barraHpJugador.progressProperty().bind(
                viewModel.hpJugadorProperty()
        );

        barraHpEnemigo.progressProperty().bind(
                viewModel.hpEnemigoProperty()
        );

        lblNombreJugador.textProperty().bind(
                viewModel.nombreJugadorProperty()
        );

        lblNivelJugador.textProperty().bind(
                viewModel.nivelJugadorProperty()
        );

        lblNombreEnemigo.textProperty().bind(
                viewModel.nombreEnemigoProperty()
        );

        lblNivelEnemigo.textProperty().bind(
                viewModel.nivelEnemigoProperty()
        );

        viewModel.hpEnemigoProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (newValue.doubleValue() < oldValue.doubleValue()) {
                        animacionCombate.animarGolpe(
                                imagenPokemonSalvaje
                        );
                    }
                }
        );

        viewModel.getLogsCombate().addListener(
                (ListChangeListener<String>) cambio ->
                        actualizarMensaje()
        );

        actualizarMensaje();
    }

    private void actualizarMensaje() {
        if (!viewModel.getLogsCombate().isEmpty()) {
            int ultimoIndice =
                    viewModel.getLogsCombate().size() - 1;

            lblMensajeActual.setText(
                    viewModel.getLogsCombate().get(ultimoIndice)
            );
        }
    }

    // -----------------------------------------------------
    // CICLO DE VIDA
    // -----------------------------------------------------

    @Override
    public void alMostrar() {
        viewModel.refrescarEstado();
        cargarImagenPokemon();

        if (imagenPokemonActivo != null) {
            imagenPokemonActivo.setImage(pokemonActivoImagen);
        }

        if (imagenPokemonSalvaje != null) {
            imagenPokemonSalvaje.setImage(pokemonSalvajeImagen);
        }

        if (!presentacionIniciada) {
            presentacionIniciada = true;
            Platform.runLater(this::animarEntradaPokemon);
        }
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/estilos/estilo.css");
        aplicarBindings();
    }

    private void animarEntradaPokemon() {
        lblMensajeActual.setText(
                "¡Un Pokémon salvaje apareció!"
        );

        animacionCombate.animarEntrada(
                imagenPokemonSalvaje,
                imagenPokemonActivo,
                interfazCombate,
                null
        );
    }

    // -----------------------------------------------------
    // CARGA DE IMÁGENES
    // -----------------------------------------------------

    public void cargarImagenPokemon() {
        Pokemon activo = viewModel
                .getModeloCombate()
                .getJugador()
                .getPokemonActivo();

        Pokemon salvaje = viewModel
                .getModeloCombate()
                .getPokemonSalvaje();

        if (activo == null || salvaje == null) {
            return;
        }

        List<DatosEspecie> especies =
                GeneradorPokemonSalvaje.getESPECIES();

        for (DatosEspecie especie : especies) {
            if (activo.getNombre().equalsIgnoreCase(
                    especie.getNombre()
            )) {
                pokemonActivoImagen = cargarImagen(
                        "/images/pokemones/espalda/"
                                + activo.getNombre()
                                + ".png"
                );
            }

            if (salvaje.getNombre().equalsIgnoreCase(
                    especie.getNombre()
            )) {
                pokemonSalvajeImagen = cargarImagen(
                        "/images/pokemones/frente/"
                                + salvaje.getNombre()
                                + ".png"
                );
            }
        }
    }

    private Image cargarImagen(String ruta) {
        URL recurso = getClass().getResource(ruta);

        if (recurso == null) {
            throw new IllegalStateException(
                    "No se encontró la imagen: " + ruta
            );
        }

        return new Image(recurso.toExternalForm());
    }

    // -----------------------------------------------------
    // RESULTADOS
    // -----------------------------------------------------

    public void mostrarResultados() {
        if (animacionResultadosIniciada) {
            return;
        }

        animacionResultadosIniciada = true;

        EstadoCombate estado =
                viewModel.getModeloCombate().getEstado();

        System.out.println("Finalizó el combate: " + estado);

        if (estado == EstadoCombate.VICTORIA) {
            animacionResultado.reproducir(
                    getEscena(),
                    true,
                    this::abrirPantallaResultados
            );

        } else if (estado == EstadoCombate.DERROTA) {
            animacionResultado.reproducir(
                    getEscena(),
                    false,
                    this::abrirPantallaResultados
            );

        } else {
            abrirPantallaResultados();
        }
    }

    private void abrirPantallaResultados() {
        pantallas.mostrarResultados(viewModel);
    }
}