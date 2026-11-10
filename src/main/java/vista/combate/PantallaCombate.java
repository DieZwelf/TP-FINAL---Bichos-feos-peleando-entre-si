package vista.combate;

import controlador.CombateViewModel;
import javafx.animation.TranslateTransition;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.DatosEspecie;
import model.GeneradorPokemonSalvaje;
import model.Pokemon;
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
import model.AccionJugador;

import java.net.URL;
import java.util.List;

public class PantallaCombate extends PantallaBase {

    private final CombateViewModel viewModel;

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


    public PantallaCombate(CombateViewModel viewModel, GestorPantallas pantallas) {
        super(pantallas);
        this.viewModel = viewModel;
    }

    @Override
    protected StackPane crearContenido() {
        StackPane root = new StackPane();
        root.getStyleClass().add("pantalla-combate-root");

        inicializarComponentes();
        imagenPokemonActivo = new ImageView(pokemonActivoImagen);
        imagenPokemonSalvaje = new ImageView(pokemonSalvajeImagen);

        // Tamaños configurados para guardar proporción retro
        imagenPokemonActivo.setFitWidth(700);
        imagenPokemonActivo.setFitHeight(700);
        imagenPokemonActivo.setPreserveRatio(true);
        imagenPokemonActivo.getStyleClass().add("imagen-pokemon");

        imagenPokemonSalvaje.setFitWidth(400);
        imagenPokemonSalvaje.setFitHeight(400);
        imagenPokemonSalvaje.setPreserveRatio(true);
        imagenPokemonSalvaje.getStyleClass().add("imagen-pokemon");

        // Contenedor Pane exclusivo para los sprites con clics transparentes para no bloquear botones
        Pane capaSprites = new Pane();
        capaSprites.setMouseTransparent(true);

        // Posiciones exactas en el escenario
        imagenPokemonSalvaje.setLayoutX(710);
        imagenPokemonSalvaje.setLayoutY(160);

        imagenPokemonActivo.setLayoutX(90);
        imagenPokemonActivo.setLayoutY(120);

        capaSprites.getChildren().addAll(imagenPokemonSalvaje, imagenPokemonActivo);

        root.getChildren().addAll(
                crearFondo(root),
                capaSprites,
                crearHudEnemigo(),
                crearPanelInferior()
        );

        return root;
    }

    private void inicializarComponentes() {
        barraHpJugador = crearBarraVida();
        barraHpEnemigo = crearBarraVida();

        lblNombreJugador = crearLabel("texto-retro");
        lblNivelJugador = crearLabel("texto-retro");
        lblMensajeActual = crearLabel("texto-retro");
        lblNombreEnemigo = crearLabel("texto-retro");
        lblNivelEnemigo = crearLabel("texto-retro");
        cargarImagenPokemon();
    }

    private ProgressBar crearBarraVida() {
        ProgressBar barra = new ProgressBar(1.0);
        barra.setPrefWidth(180);
        barra.getStyleClass().add("barra-hp");
        return barra;
    }

    public void mostrarResultados() {
        StackPane root = (StackPane) getEscena().getRoot();

        VBox pantalla = new VBox(20);
        pantalla.getStyleClass().add("pantalla-resultados");
        pantalla.setAlignment(Pos.CENTER);

        Label titulo = new Label("RESULTADO DEL COMBATE");
        titulo.getStyleClass().add("resultados-titulo");

        VBox mensajes = new VBox(12);
        mensajes.setAlignment(Pos.CENTER);

        for (String resultado : viewModel.getLogsCombate()) {
            Label mensaje = new Label(resultado);
            mensaje.getStyleClass().add("resultados-mensaje");
            mensaje.setWrapText(true);
            mensajes.getChildren().add(mensaje);
        }

        Label instruccion = new Label("PULSA ESPACIO PARA CONTINUAR");
        instruccion.getStyleClass().add("resultados-instruccion");

        pantalla.getChildren().addAll(titulo, mensajes, instruccion);

        pantalla.setFocusTraversable(true);
        pantalla.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                pantallas.regresarAlMapa();
                event.consume();
            }
        });

        root.getChildren().setAll(pantalla);

        javafx.application.Platform.runLater(pantalla::requestFocus);
    }

    private Label crearLabel(String estilo) {
        Label label = new Label();
        label.getStyleClass().add(estilo);
        return label;
    }

    private ImageView crearFondo(Pane root) {
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
        VBox hud = new VBox(5, lblNombreEnemigo, lblNivelEnemigo, barraHpEnemigo);

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
        mochila.setOnAction(e -> pantallas.mostrarInventario(viewModel));
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
        lblNombreEnemigo.textProperty().bind(viewModel.nombreEnemigoProperty());
        lblNivelEnemigo.textProperty().bind(viewModel.nivelEnemigoProperty());

        // --- ANIMACIÓN DE GOLPE: SOLO PARA EL ENEMIGO ---
        viewModel.hpEnemigoProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.doubleValue() < oldValue.doubleValue()) {
                reproducirAnimacionGolpe(imagenPokemonSalvaje);
            }
        });

        viewModel.getLogsCombate().addListener(
                (ListChangeListener<String>) cambio -> {
                    if (!viewModel.getLogsCombate().isEmpty()) {
                        lblMensajeActual.setText(viewModel.getLogsCombate().get(viewModel.getLogsCombate().size() - 1));
                    }
                }
        );
        if (!viewModel.getLogsCombate().isEmpty()) {
            lblMensajeActual.setText(viewModel.getLogsCombate().get(viewModel.getLogsCombate().size() - 1));
        }
    }

    @Override
    public void alMostrar() {
        viewModel.refrescarEstado();
        cargarImagenPokemon();
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/estilos/estilo.css");
        aplicarBindings();

        escena.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE && viewModel.getLogsCombate().stream().anyMatch(
                    log -> log.contains("ha vencido a"))) {
                pantallas.regresarAlMapa();
                event.consume();
            }
        });
    }

    public void cargarImagenPokemon (){
        Pokemon activo = viewModel.getModeloCombate().getEntrenador().getPokemonActivo();
        Pokemon salvaje = viewModel.getModeloCombate().getPokemonSalvaje();

        List<DatosEspecie> DATOS = GeneradorPokemonSalvaje.getESPECIES();
        String rutaActivo = "/images/pokemones/espalda/" + activo.getNombre() + ".png";

        String rutaSalvaje = "/images/pokemones/frente/" + salvaje.getNombre() + ".png";

        for (int i = 0; i < DATOS.size(); i++)
        {
            if (activo.getNombre().equalsIgnoreCase(DATOS.get(i).getNombre()))
            {
                pokemonActivoImagen = new Image(String.valueOf(getClass().getResource(rutaActivo)));

                URL recurso = getClass().getResource(rutaActivo);

                if (recurso != null) {
                    pokemonActivoImagen = new Image(recurso.toExternalForm());

                    if (imagenPokemonActivo != null) {
                        imagenPokemonActivo.setImage(pokemonActivoImagen);
                    }
                }

            }

            if (salvaje.getNombre().equalsIgnoreCase(DATOS.get(i).getNombre()))
            {
                pokemonSalvajeImagen = new Image(String.valueOf(getClass().getResource(rutaSalvaje)));

            }
        }
    }

    public void reproducirAnimacionGolpe(ImageView spriteAfectado) {
        if (spriteAfectado == null) return;

        TranslateTransition sacudida = new TranslateTransition(Duration.millis(50), spriteAfectado);
        sacudida.setFromX(0);
        // Se mueve rápidamente a la izquierda y derecha para simular el impacto
        sacudida.setByX(10);
        sacudida.setCycleCount(6); // Cuántas veces se repite el temblor
        sacudida.setAutoReverse(true); // Vuelve a su posición original

        // Al terminar, nos aseguramos de que quede exactamente en su sitio
        sacudida.setOnFinished(e -> spriteAfectado.setTranslateX(0));

        sacudida.play();
    }
}