/**
 * Representa la pantalla de combate entre el Pokémon del jugador y un Pokémon salvaje.
 *
 * Se encarga de construir y organizar los elementos visuales del combate,
 * incluyendo el escenario, los sprites de los Pokémon, las barras de vida,
 * los datos de cada participante y el panel de acciones.
 *
 * Utiliza un CombateViewModel para obtener y actualizar la información
 * del combate mediante bindings, manteniendo sincronizada la interfaz
 * con el estado del modelo.
 *
 * También gestiona las animaciones de los Pokémon, la carga de imágenes,
 * las acciones del jugador y la visualización de los resultados
 * una vez finalizado el combate.
 *
 * Hereda de PantallaBase para reutilizar la estructura y configuración
 * general de las pantallas del juego.
 */

package vista.combate;

import controlador.CombateViewModel;
import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.AccionJugador;
import model.DatosEspecie;
import model.GeneradorPokemonSalvaje;
import model.Pokemon;
import vista.GestorPantallas;
import vista.base.PantallaBase;
import javafx.animation.*;
import javafx.application.Platform;

import java.net.URL;
import java.util.List;

public class PantallaCombate extends PantallaBase {

    // =====================================================
    // ATRIBUTOS
    // =====================================================

    private final CombateViewModel viewModel;

    // Barras de vida
    private ProgressBar barraHpJugador;
    private ProgressBar barraHpEnemigo;

    // Textos
    private Label lblNombreJugador;
    private Label lblNivelJugador;
    private Label lblMensajeActual;

    private Label lblNombreEnemigo;
    private Label lblNivelEnemigo;

    // Imágenes
    private Image pokemonActivoImagen;
    private Image pokemonSalvajeImagen;

    private ImageView imagenPokemonActivo;
    private ImageView imagenPokemonSalvaje;

    private BorderPane interfazCombate;

    private boolean presentacionIniciada = false;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantallaCombate(CombateViewModel viewModel, GestorPantallas pantallas) {
        super(pantallas);
        this.viewModel = viewModel;
    }


    // =====================================================
    // CONSTRUCCIÓN DE LA PANTALLA
    // =====================================================

    @Override
    protected StackPane crearContenido() {

        StackPane root = new StackPane();
        root.getStyleClass().add("pantalla-combate-root");

        inicializarComponentes();
        crearSprites();

        ImageView fondo = crearFondo(root);
        Pane capaSprites = crearCapaSprites();

        // Capa independiente para posicionar HUD y panel inferior.
        interfazCombate = crearCapaInterfaz();

// Ocultamos la interfaz hasta que entren los Pokémon.
        interfazCombate.setOpacity(0);
        interfazCombate.setDisable(true);

        root.getChildren().addAll(
                fondo,
                capaSprites,
                interfazCombate
        );

        // Los sprites se posicionan desde la esquina superior izquierda.
        StackPane.setAlignment(capaSprites, Pos.TOP_LEFT);

        // La interfaz ocupa toda la pantalla.
        StackPane.setAlignment(interfazCombate, Pos.CENTER);
        interfazCombate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        return root;
    }


    // =====================================================
    // INICIALIZACIÓN DE COMPONENTES
    // =====================================================

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

    private Label crearLabel(String estilo) {

        Label label = new Label();
        label.getStyleClass().add(estilo);

        return label;
    }

    private ProgressBar crearBarraVida() {

        ProgressBar barra = new ProgressBar(1.0);

        barra.setPrefWidth(180);
        barra.getStyleClass().add("barra-hp");

        return barra;
    }


    // =====================================================
    // SPRITES Y FONDO
    // =====================================================

    private void crearSprites() {

        imagenPokemonActivo = new ImageView(pokemonActivoImagen);
        imagenPokemonSalvaje = new ImageView(pokemonSalvajeImagen);

        // Pokémon del jugador.
        imagenPokemonActivo.setFitWidth(700);
        imagenPokemonActivo.setFitHeight(700);
        imagenPokemonActivo.setPreserveRatio(true);
        imagenPokemonActivo.getStyleClass().add("imagen-pokemon");

        // Pokémon salvaje.
        imagenPokemonSalvaje.setFitWidth(400);
        imagenPokemonSalvaje.setFitHeight(400);
        imagenPokemonSalvaje.setPreserveRatio(true);
        imagenPokemonSalvaje.getStyleClass().add("imagen-pokemon");
    }

    private Pane crearCapaSprites() {

        Pane capaSprites = new Pane();

        capaSprites.setMouseTransparent(true);

        // Posiciones de los Pokémon.
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


    // =====================================================
    // CAPA DE INTERFAZ
    // =====================================================

    private BorderPane crearCapaInterfaz() {

        BorderPane interfaz = new BorderPane();

        interfaz.setPickOnBounds(false);

        // HUD enemigo: arriba a la derecha.
        VBox hudEnemigo = crearHudEnemigo();

        HBox contenedorHud = new HBox(hudEnemigo);
        contenedorHud.setAlignment(Pos.TOP_RIGHT);
        contenedorHud.setPadding(new Insets(40, 50, 0, 0));

        interfaz.setTop(contenedorHud);

        // Panel del jugador: abajo y centrado.
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

        // Información del Pokémon del jugador.
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

        // Cuadro de diálogo.
        StackPane cajaDialogo = new StackPane(lblMensajeActual);

        cajaDialogo.getStyleClass().add("caja-dialogo-rpg");
        cajaDialogo.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(cajaDialogo, Priority.ALWAYS);

        // Botones de combate.
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


    // =====================================================
    // BOTONERA
    // =====================================================

    private GridPane crearBotonera() {

        Button atacar = crearBoton("LUCHA");
        Button mochila = crearBoton("MOCHILA");
        Button huir = crearBoton("HUIR");
        Button equipo = crearBoton("POKEMONES");

        atacar.setOnAction(e ->
                viewModel.ejecutarAccion(AccionJugador.ATACAR)
        );

        mochila.setOnAction(e ->
                pantallas.mostrarInventario(viewModel)
        );

        huir.setOnAction(e ->
                viewModel.ejecutarAccion(AccionJugador.HUIR)
        );

        equipo.setOnAction(e ->
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

    private Button crearBoton(String texto) {

        Button boton = new Button(texto);

        boton.getStyleClass().add("boton-retro");
        boton.setPrefSize(320, 80);

        return boton;
    }


    // =====================================================
    // BINDINGS
    // =====================================================

    private void aplicarBindings() {

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

        // Animación cuando el enemigo recibe daño.
        viewModel.hpEnemigoProperty().addListener(
                (obs, oldValue, newValue) -> {

                    if (newValue.doubleValue() < oldValue.doubleValue()) {
                        reproducirAnimacionGolpe(imagenPokemonSalvaje);
                    }
                }
        );

        // Actualización del mensaje de combate.
        viewModel.getLogsCombate().addListener(
                (ListChangeListener<String>) cambio ->
                        actualizarMensaje()
        );

        actualizarMensaje();
    }

    private void actualizarMensaje() {

        if (!viewModel.getLogsCombate().isEmpty()) {

            int ultimoIndice = viewModel.getLogsCombate().size() - 1;

            lblMensajeActual.setText(
                    viewModel.getLogsCombate().get(ultimoIndice)
            );
        }
    }


    // =====================================================
    // CICLO DE VIDA DE LA PANTALLA
    // =====================================================

    @Override
    public void alMostrar() {

        viewModel.refrescarEstado();
        cargarImagenPokemon();

        // La presentación solo se ejecuta al entrar por primera vez.
        if (!presentacionIniciada) {

            presentacionIniciada = true;

            Platform.runLater(this::animarEntradaPokemon);
        }
    }

    @Override
    protected void configurarEscena(Scene escena) {

        agregarCSS(
                escena,
                "/images/estilos/estilo.css"
        );

        aplicarBindings();

        escena.addEventFilter(
                KeyEvent.KEY_PRESSED,
                event -> {

                    if (event.getCode() == KeyCode.SPACE
                            && viewModel.getLogsCombate().stream().anyMatch(
                            log -> log.contains("ha vencido a")
                    )) {

                        pantallas.regresarAlMapa();
                        event.consume();
                    }
                }
        );
    }


    // =====================================================
    // CARGA DE IMÁGENES POKÉMON
    // =====================================================

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

                if (imagenPokemonActivo != null) {
                    imagenPokemonActivo.setImage(pokemonActivoImagen);
                }
            }

            if (salvaje.getNombre().equalsIgnoreCase(
                    especie.getNombre()
            )) {

                pokemonSalvajeImagen = cargarImagen(
                        "/images/pokemones/frente/"
                                + salvaje.getNombre()
                                + ".png"
                );

                if (imagenPokemonSalvaje != null) {
                    imagenPokemonSalvaje.setImage(pokemonSalvajeImagen);
                }
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


    // =====================================================
    // ANIMACIONES
    // =====================================================

    public void reproducirAnimacionGolpe(ImageView spriteAfectado) {

        if (spriteAfectado == null) {
            return;
        }

        TranslateTransition sacudida = new TranslateTransition(
                Duration.millis(50),
                spriteAfectado
        );

        sacudida.setFromX(0);
        sacudida.setByX(10);
        sacudida.setCycleCount(6);
        sacudida.setAutoReverse(true);

        sacudida.setOnFinished(e ->
                spriteAfectado.setTranslateX(0)
        );

        sacudida.play();
    }


    // =====================================================
    // RESULTADOS DEL COMBATE
    // =====================================================

    public void mostrarResultados() {

        StackPane root = (StackPane) getEscena().getRoot();

        BorderPane pantalla = new BorderPane();
        pantalla.getStyleClass().add("pantalla-combate-root");

        // Título.
        Label titulo = new Label("RESULTADO DEL COMBATE");
        titulo.getStyleClass().add("titulo-retro");

        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(25, 0, 10, 0));

        pantalla.setTop(titulo);

        // Contenido central.
        VBox contenidoCentral = new VBox(20);

        contenidoCentral.setAlignment(Pos.CENTER);
        contenidoCentral.setPadding(
                new Insets(10, 40, 10, 40)
        );

        VBox tarjetaRegistro = crearTarjetaRegistro();
        VBox tarjetaRecompensas = crearTarjetaRecompensas();

        contenidoCentral.getChildren().addAll(
                tarjetaRegistro,
                tarjetaRecompensas
        );

        pantalla.setCenter(contenidoCentral);

        // Parte inferior.
        VBox inferior = crearPanelResultadosInferior();

        pantalla.setBottom(inferior);

        // Fondo.
        URL recurso = getClass().getResource(
                "/images/pantallaMenu/prueba.jpg"
        );

        if (recurso == null) {
            throw new IllegalStateException(
                    "No se encontró /images/pantallaMenu/prueba.jpg"
            );
        }

        ImageView fondo = new ImageView(
                new Image(recurso.toExternalForm())
        );

        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().setAll(fondo, pantalla);

        // Regresar al mapa con espacio.
        pantalla.setFocusTraversable(true);

        pantalla.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.SPACE) {
                pantallas.regresarAlMapa();
                event.consume();
            }
        });

        javafx.application.Platform.runLater(
                pantalla::requestFocus
        );
    }

    private VBox crearTarjetaRegistro() {

        Label titulo = new Label("REGISTRO DEL COMBATE");

        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle("-fx-text-fill: #ffde00;");

        VBox boxLogs = new VBox(5);
        boxLogs.setAlignment(Pos.CENTER_LEFT);

        for (String resultado : viewModel.getLogsCombate()) {

            if (!resultado.contains("XP")
                    && !resultado.contains("nivel")
                    && !resultado.contains("obtenido")) {

                Label mensaje = new Label("• " + resultado);

                mensaje.getStyleClass().add("texto-retro");
                mensaje.setWrapText(true);

                boxLogs.getChildren().add(mensaje);
            }
        }

        ScrollPane scrollLogs = new ScrollPane(boxLogs);

        scrollLogs.setFitToWidth(true);
        scrollLogs.setPrefHeight(160);
        scrollLogs.setStyle(
                "-fx-background: transparent; "
                        + "-fx-background-color: transparent;"
        );

        VBox tarjeta = new VBox(10, titulo, scrollLogs);

        tarjeta.getStyleClass().add("caja-hud");
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(700);

        return tarjeta;
    }

    private VBox crearTarjetaRecompensas() {

        Label titulo = new Label("RECOMPENSAS Y EXPERIENCIA");

        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle("-fx-text-fill: #5fae3f;");

        VBox boxRecompensas = new VBox(5);
        boxRecompensas.setAlignment(Pos.CENTER_LEFT);

        boolean hayRecompensas = false;

        for (String resultado : viewModel.getLogsCombate()) {

            if (resultado.contains("XP")
                    || resultado.contains("nivel")
                    || resultado.contains("obtenido")) {

                Label mensaje = new Label("★ " + resultado);

                mensaje.getStyleClass().add("texto-retro");
                mensaje.setWrapText(true);

                boxRecompensas.getChildren().add(mensaje);

                hayRecompensas = true;
            }
        }

        if (!hayRecompensas) {

            Label sinRecompensa = new Label(
                    "No se obtuvieron recompensas en este combate."
            );

            sinRecompensa.getStyleClass().add("texto-retro");

            boxRecompensas.getChildren().add(sinRecompensa);
        }

        VBox tarjeta = new VBox(10, titulo, boxRecompensas);

        tarjeta.getStyleClass().add("caja-hud");
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(700);

        return tarjeta;
    }

    private VBox crearPanelResultadosInferior() {

        Label instruccion = new Label(
                "PULSA [ESPACIO] PARA REGRESAR AL MAPA"
        );

        instruccion.getStyleClass().add("texto-retro");
        instruccion.setStyle("-fx-text-fill: #f3ecd0;");

        Button btnContinuar = new Button("CONTINUAR");

        btnContinuar.getStyleClass().add("boton-retro");
        btnContinuar.setOnAction(e ->
                pantallas.regresarAlMapa()
        );

        VBox inferior = new VBox(
                10,
                instruccion,
                btnContinuar
        );

        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(20));

        return inferior;
    }

    private void animarEntradaPokemon() {

        // Posiciones iniciales fuera de la pantalla.
        imagenPokemonSalvaje.setTranslateX(550);
        imagenPokemonActivo.setTranslateX(-550);

        // Entrada del Pokémon salvaje.
        TranslateTransition entradaSalvaje = new TranslateTransition(
                Duration.millis(650),
                imagenPokemonSalvaje
        );

        entradaSalvaje.setFromX(550);
        entradaSalvaje.setToX(0);
        entradaSalvaje.setInterpolator(Interpolator.EASE_OUT);

        // Entrada del Pokémon del jugador.
        TranslateTransition entradaJugador = new TranslateTransition(
                Duration.millis(650),
                imagenPokemonActivo
        );

        entradaJugador.setFromX(-550);
        entradaJugador.setToX(0);
        entradaJugador.setInterpolator(Interpolator.EASE_OUT);

        // Ambos entran al mismo tiempo.
        ParallelTransition entradas = new ParallelTransition(
                entradaSalvaje,
                entradaJugador
        );

        // Pequeño rebote al llegar.
        TranslateTransition reboteSalvaje = crearRebote(imagenPokemonSalvaje);
        TranslateTransition reboteJugador = crearRebote(imagenPokemonActivo);

        ParallelTransition rebotes = new ParallelTransition(
                reboteSalvaje,
                reboteJugador
        );

        // Primero entran, después rebotan.
        SequentialTransition presentacion = new SequentialTransition(
                entradas,
                rebotes
        );

        presentacion.setOnFinished(evento -> {

            // Mensaje inicial del combate.
            lblMensajeActual.setText("¡Un Pokémon salvaje apareció!");

            // Mostramos HUD, barras de vida y botones.
            FadeTransition aparecerInterfaz = new FadeTransition(
                    Duration.millis(300),
                    interfazCombate
            );

            aparecerInterfaz.setFromValue(0);
            aparecerInterfaz.setToValue(1);

            aparecerInterfaz.setOnFinished(fin -> {
                interfazCombate.setDisable(false);
            });

            aparecerInterfaz.play();
        });

        presentacion.play();
    }

    private TranslateTransition crearRebote(ImageView pokemon) {

        TranslateTransition rebote = new TranslateTransition(
                Duration.millis(180),
                pokemon
        );

        rebote.setFromY(0);
        rebote.setToY(-15);
        rebote.setCycleCount(2);
        rebote.setAutoReverse(true);

        return rebote;
    }
}
