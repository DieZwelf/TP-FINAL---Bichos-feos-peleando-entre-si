
package vista.combate;

import controlador.CombateViewModel;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import model.*;

import vista.GestorPantallas;
import vista.base.PantallaBase;

import java.net.URL;
import java.util.List;

public class PantallaCombate extends PantallaBase {

    // =====================================================
    // ATRIBUTOS
    // =====================================================

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

    private BorderPane interfazCombate;

    private boolean presentacionIniciada = false;
    private boolean animacionResultadosIniciada = false;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantallaCombate(
            CombateViewModel viewModel,
            GestorPantallas pantallas
    ) {
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

        interfazCombate = crearCapaInterfaz();

        // Ocultamos la interfaz hasta que entren los Pokémon.
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


    // =====================================================
    // CAPA DE INTERFAZ
    // =====================================================

    private BorderPane crearCapaInterfaz() {

        BorderPane interfaz = new BorderPane();

        interfaz.setPickOnBounds(false);

        // HUD enemigo.
        VBox hudEnemigo = crearHudEnemigo();

        HBox contenedorHud = new HBox(hudEnemigo);

        contenedorHud.setAlignment(Pos.TOP_RIGHT);
        contenedorHud.setPadding(
                new Insets(40, 50, 0, 0)
        );

        interfaz.setTop(contenedorHud);

        // Panel inferior.
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

        viewModel.hpEnemigoProperty().addListener(
                (obs, oldValue, newValue) -> {

                    if (newValue.doubleValue() < oldValue.doubleValue()) {
                        reproducirAnimacionGolpe(imagenPokemonSalvaje);
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


    // =====================================================
    // CICLO DE VIDA
    // =====================================================

    @Override
    public void alMostrar() {

        viewModel.refrescarEstado();
        cargarImagenPokemon();

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

        // No agregamos acá el evento de ESPACIO.
        // La pantalla de resultados se ocupa de regresar al mapa.
    }


    // =====================================================
    // CARGA DE IMÁGENES
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
    // ANIMACIÓN DE GOLPE
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

        if (animacionResultadosIniciada) {
            return;
        }

        animacionResultadosIniciada = true;

        EstadoCombate estado =
                viewModel.getModeloCombate().getEstado();

        System.out.println(
                "Finalizó el combate: " + estado
        );

        if (estado == EstadoCombate.VICTORIA) {

            animarResultado(
                    true,
                    this::construirContenidoResultados
            );

        } else if (estado == EstadoCombate.DERROTA) {

            animarResultado(
                    false,
                    this::construirContenidoResultados
            );

        } else {

            // HUIDA o CAPTURA.
            construirContenidoResultados();
        }
    }


    // =====================================================
    // CONSTRUCCIÓN DE LA PANTALLA DE RESULTADOS
    // =====================================================

    private void construirContenidoResultados() {

        StackPane root = (StackPane) getEscena().getRoot();

        // Fondo del escenario.
        ImageView fondo = crearFondo(root);

        // Capa principal.
        BorderPane pantalla = new BorderPane();

        pantalla.getStyleClass().add("pantalla-combate-root");

        pantalla.setStyle(
                "-fx-background-color: rgba(12, 20, 25, 0.88);"
        );

        // Título según el resultado.
        EstadoCombate estado =
                viewModel.getModeloCombate().getEstado();

        String textoTitulo;

        switch (estado) {
            case VICTORIA:
                textoTitulo = "¡VICTORIA!";
                break;

            case DERROTA:
                textoTitulo = "DERROTA";
                break;

            case HUIDA:
                textoTitulo = "¡ESCAPASTE!";
                break;

            case CAPTURA:
                textoTitulo = "¡POKÉMON CAPTURADO!";
                break;

            default:
                textoTitulo = "COMBATE FINALIZADO";
                break;
        }

        Label titulo = new Label(textoTitulo);

        titulo.getStyleClass().add("texto-retro");

        titulo.setStyle(
                "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffde00;"
        );

        VBox encabezado = new VBox(titulo);

        encabezado.setAlignment(Pos.CENTER);
        encabezado.setPadding(new Insets(25));

        pantalla.setTop(encabezado);

        // IMPORTANTE:
        // Acá se construyen las dos tarjetas.
        VBox registro = crearTarjetaRegistro();
        VBox recompensas = crearTarjetaRecompensas();

        HBox tarjetas = new HBox(
                25,
                registro,
                recompensas
        );

        tarjetas.setAlignment(Pos.CENTER);
        tarjetas.setPadding(new Insets(20));

        HBox.setHgrow(registro, Priority.ALWAYS);
        HBox.setHgrow(recompensas, Priority.ALWAYS);

        ScrollPane contenedor = new ScrollPane(tarjetas);

        contenedor.setFitToWidth(true);
        contenedor.setFitToHeight(true);
        contenedor.setPannable(true);

        contenedor.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        pantalla.setCenter(contenedor);

        // IMPORTANTE:
        // Acá se construye el botón CONTINUAR.
        pantalla.setBottom(crearPanelResultadosInferior());

        // Reemplazamos el contenido del combate.
        root.getChildren().setAll(
                fondo,
                pantalla
        );

        // ESPACIO también permite regresar al mapa.
        Scene escena = getEscena();

        escena.addEventFilter(
                KeyEvent.KEY_PRESSED,
                event -> {

                    if (event.getCode() == KeyCode.SPACE) {

                        pantallas.regresarAlMapa();

                        event.consume();
                    }
                }
        );

        root.setFocusTraversable(true);

        Platform.runLater(root::requestFocus);
    }


    // =====================================================
    // TARJETA: REGISTRO DEL COMBATE
    // =====================================================

    private VBox crearTarjetaRegistro() {

        Label titulo = new Label("REGISTRO DEL COMBATE");

        titulo.getStyleClass().add("texto-retro");

        titulo.setStyle(
                "-fx-text-fill: #ffde00;"
        );

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
        scrollLogs.setPrefHeight(200);

        scrollLogs.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        VBox tarjeta = new VBox(
                10,
                titulo,
                scrollLogs
        );

        tarjeta.getStyleClass().add("caja-hud");

        tarjeta.setPadding(new Insets(15));
        tarjeta.setPrefWidth(500);
        tarjeta.setMaxWidth(700);

        return tarjeta;
    }


    // =====================================================
    // TARJETA: RECOMPENSAS Y EXPERIENCIA
    // =====================================================

    private VBox crearTarjetaRecompensas() {

        Label titulo = new Label(
                "RECOMPENSAS Y EXPERIENCIA"
        );

        titulo.getStyleClass().add("texto-retro");

        titulo.setStyle(
                "-fx-text-fill: #5fae3f;"
        );

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

        VBox tarjeta = new VBox(
                10,
                titulo,
                boxRecompensas
        );

        tarjeta.getStyleClass().add("caja-hud");

        tarjeta.setPadding(new Insets(15));
        tarjeta.setPrefWidth(500);
        tarjeta.setMaxWidth(700);

        return tarjeta;
    }


    // =====================================================
    // PANEL INFERIOR DE RESULTADOS
    // =====================================================

    private VBox crearPanelResultadosInferior() {

        Label instruccion = new Label(
                "PULSA [ESPACIO] PARA REGRESAR AL MAPA"
        );

        instruccion.getStyleClass().add("texto-retro");

        instruccion.setStyle(
                "-fx-text-fill: #f3ecd0;"
        );

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


    // =====================================================
    // ANIMACIÓN DE ENTRADA DE LOS POKÉMON
    // =====================================================

    private void animarEntradaPokemon() {

        imagenPokemonSalvaje.setTranslateX(550);
        imagenPokemonActivo.setTranslateX(-550);

        TranslateTransition entradaSalvaje =
                new TranslateTransition(
                        Duration.millis(650),
                        imagenPokemonSalvaje
                );

        entradaSalvaje.setFromX(550);
        entradaSalvaje.setToX(0);
        entradaSalvaje.setInterpolator(
                Interpolator.EASE_OUT
        );

        TranslateTransition entradaJugador =
                new TranslateTransition(
                        Duration.millis(650),
                        imagenPokemonActivo
                );

        entradaJugador.setFromX(-550);
        entradaJugador.setToX(0);
        entradaJugador.setInterpolator(
                Interpolator.EASE_OUT
        );

        ParallelTransition entradas = new ParallelTransition(
                entradaSalvaje,
                entradaJugador
        );

        TranslateTransition reboteSalvaje =
                crearRebote(imagenPokemonSalvaje);

        TranslateTransition reboteJugador =
                crearRebote(imagenPokemonActivo);

        ParallelTransition rebotes = new ParallelTransition(
                reboteSalvaje,
                reboteJugador
        );

        SequentialTransition presentacion =
                new SequentialTransition(
                        entradas,
                        rebotes
                );

        presentacion.setOnFinished(evento -> {

            lblMensajeActual.setText(
                    "¡Un Pokémon salvaje apareció!"
            );

            FadeTransition aparecerInterfaz =
                    new FadeTransition(
                            Duration.millis(300),
                            interfazCombate
                    );

            aparecerInterfaz.setFromValue(0);
            aparecerInterfaz.setToValue(1);

            aparecerInterfaz.setOnFinished(fin ->
                    interfazCombate.setDisable(false)
            );

            aparecerInterfaz.play();
        });

        presentacion.play();
    }

    private TranslateTransition crearRebote(ImageView pokemon) {

        TranslateTransition rebote =
                new TranslateTransition(
                        Duration.millis(180),
                        pokemon
                );

        rebote.setFromY(0);
        rebote.setToY(-15);
        rebote.setCycleCount(2);
        rebote.setAutoReverse(true);

        return rebote;
    }


    // =====================================================
    // ANIMACIÓN DE VICTORIA / DERROTA
    // =====================================================

    private void animarResultado(
            boolean victoria,
            Runnable alTerminar
    ) {

        StackPane root = (StackPane) getEscena().getRoot();

        StackPane overlay = new StackPane();

        overlay.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        String colorFondo = victoria
                ? "rgba(25, 20, 5, 0.94)"
                : "rgba(25, 5, 10, 0.95)";

        String colorTexto = victoria
                ? "#FFD700"
                : "#FF5555";

        String colorBrillo = victoria
                ? "#FFF2A6"
                : "#FF9999";

        overlay.setStyle(
                "-fx-background-color: " + colorFondo + ";"
        );

        Label titulo = new Label(
                victoria ? "¡VICTORIA!" : "¡DERROTA!"
        );

        titulo.setStyle(
                "-fx-font-size: 58px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + colorTexto + ";" +
                        "-fx-effect: dropshadow(gaussian, " +
                        colorBrillo +
                        ", 24, 0.8, 0, 0);"
        );

        Label subtitulo = new Label(
                victoria
                        ? "HAS GANADO EL COMBATE"
                        : "HAS SIDO DERROTADO"
        );

        subtitulo.setStyle(
                "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;" +
                        "-fx-opacity: 0.85;"
        );

        VBox contenido = new VBox(
                18,
                titulo,
                subtitulo
        );

        contenido.setAlignment(Pos.CENTER);

        overlay.getChildren().add(contenido);

        overlay.setOpacity(0);

        titulo.setScaleX(0.65);
        titulo.setScaleY(0.65);

        root.getChildren().add(overlay);

        FadeTransition entrada = new FadeTransition(
                Duration.millis(300),
                overlay
        );

        entrada.setFromValue(0);
        entrada.setToValue(1);

        ScaleTransition impacto = new ScaleTransition(
                Duration.millis(450),
                titulo
        );

        impacto.setFromX(0.65);
        impacto.setFromY(0.65);
        impacto.setToX(1.08);
        impacto.setToY(1.08);

        impacto.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition ajuste = new ScaleTransition(
                Duration.millis(180),
                titulo
        );

        ajuste.setToX(1.0);
        ajuste.setToY(1.0);

        ajuste.setInterpolator(Interpolator.EASE_OUT);

        PauseTransition pausa = new PauseTransition(
                Duration.millis(1100)
        );

        FadeTransition salida = new FadeTransition(
                Duration.millis(300),
                overlay
        );

        salida.setFromValue(1);
        salida.setToValue(0);

        SequentialTransition secuencia =
                new SequentialTransition(
                        new ParallelTransition(
                                entrada,
                                impacto
                        ),
                        ajuste,
                        pausa,
                        salida
                );

        secuencia.setOnFinished(event -> {

            root.getChildren().remove(overlay);

            // Después de la animación se construye
            // la pantalla completa de resultados.
            alTerminar.run();
        });

        secuencia.play();
    }
}
