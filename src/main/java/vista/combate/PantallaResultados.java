
package vista.combate;

import controlador.CombateViewModel;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import model.EstadoCombate;
import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;
import vista.combate.animaciones.AnimacionResultado;

import java.net.URL;

public class PantallaResultados extends PantallaBase {

    private final CombateViewModel viewModel;
    private boolean transicionIniciada = false;

    private final AnimacionResultado animacionResultado =
            new AnimacionResultado();

    public PantallaResultados(CombateViewModel viewModel, GestorPantallas pantallas) {
        super(pantallas);
        this.viewModel = viewModel;
    }

    @Override
    protected StackPane crearContenido() {
        StackPane root = new StackPane();

        ImageView fondo = crearFondo(root);

        BorderPane pantalla = new BorderPane();
        pantalla.getStyleClass().add("pantalla-combate-root");
        pantalla.setStyle("-fx-background-color: rgba(12, 20, 25, 0.78);");

        VBox registro = crearTarjetaRegistro();
        VBox recompensas = crearTarjetaRecompensas();

        HBox tarjetas = new HBox(25, registro, recompensas);
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

        pantalla.setTop(crearEncabezado());
        pantalla.setCenter(contenedor);
        pantalla.setBottom(crearPanelInferior());

        root.getChildren().addAll(fondo, pantalla);

        return root;
    }

    private ImageView crearFondo(StackPane root) {
        URL url = getClass().getResource("/images/mapa/escenario_prueba.jpg");

        if (url == null) {
            return new ImageView();
        }

        Image imagen = new Image(url.toExternalForm());

        ImageView fondo = new ImageView(imagen);
        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());

        return fondo;
    }

    private String obtenerTitulo() {
        EstadoCombate estado = viewModel.getModeloCombate().getEstado();

        switch (estado) {
            case VICTORIA:
                return "¡VICTORIA!";
            case DERROTA:
                return "DERROTA";
            case HUIDA:
                return "¡ESCAPASTE!";
            case CAPTURA:
                return "¡CAPTURADO!";
            default:
                return "COMBATE FINALIZADO";
        }
    }

    // ---------------------------------------------------------
    // ENCABEZADO RETRO
    // ---------------------------------------------------------

    private VBox crearEncabezado() {

        Label resultado = new Label(obtenerTitulo());
        resultado.getStyleClass().add("texto-retro");
        resultado.setFont(Font.font("Monospaced", FontWeight.BOLD, 38));
        resultado.setTextFill(obtenerColorResultado());

        Label subtitulo = new Label("RESULTADO DEL COMBATE");
        subtitulo.getStyleClass().add("texto-retro");
        subtitulo.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        subtitulo.setTextFill(Color.web("#e8dfbd"));

        Region separador = new Region();
        separador.setPrefHeight(3);
        separador.setMaxWidth(350);
        separador.setStyle("-fx-background-color: #b99b52;");

        VBox contenido = new VBox(12, resultado, separador, subtitulo);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(18, 35, 18, 35));
        contenido.setMaxWidth(550);

        contenido.setStyle(
                "-fx-background-color: #252c32;" +
                        "-fx-border-color: #b99b52;" +
                        "-fx-border-width: 4;" +
                        "-fx-background-radius: 2;" +
                        "-fx-border-radius: 2;"
        );

        VBox encabezado = new VBox(contenido);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setPadding(new Insets(22, 15, 10, 15));

        encabezado.setOpacity(0);

        FadeTransition entrada =
                new FadeTransition(Duration.millis(500), encabezado);

        entrada.setFromValue(0);
        entrada.setToValue(1);
        entrada.setInterpolator(Interpolator.EASE_OUT);
        entrada.play();

        return encabezado;
    }

    private Color obtenerColorResultado() {
        EstadoCombate estado = viewModel.getModeloCombate().getEstado();

        return switch (estado) {
            case VICTORIA -> Color.web("#f5d76e");
            case DERROTA -> Color.web("#e87979");
            case HUIDA -> Color.web("#83cce0");
            case CAPTURA -> Color.web("#8ed18b");
            default -> Color.web("#f3ecd0");
        };
    }

    // ---------------------------------------------------------
    // TARJETA DE REGISTRO
    // ---------------------------------------------------------

    private VBox crearTarjetaRegistro() {

        Label titulo = new Label("REGISTRO DEL COMBATE");
        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle("-fx-text-fill: #f5d76e;");

        VBox boxLogs = new VBox(8);
        boxLogs.setAlignment(Pos.CENTER_LEFT);

        for (String resultado : viewModel.getLogsCombate()) {

            if (!esMensajeDeRecompensa(resultado)) {

                Label mensaje = new Label("▶ " + resultado);
                mensaje.getStyleClass().add("texto-retro");
                mensaje.setWrapText(true);
                mensaje.setStyle("-fx-text-fill: #f3ecd0;");

                boxLogs.getChildren().add(mensaje);
            }
        }

        ScrollPane scrollLogs = new ScrollPane(boxLogs);
        scrollLogs.setFitToWidth(true);
        scrollLogs.setPrefHeight(200);

        scrollLogs.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: #171e23;" +
                        "-fx-border-color: #68747a;" +
                        "-fx-border-width: 2;"
        );

        VBox tarjeta = new VBox(12, titulo, scrollLogs);
        tarjeta.getStyleClass().add("caja-hud");
        tarjeta.setPadding(new Insets(18));
        tarjeta.setPrefWidth(500);
        tarjeta.setMaxWidth(700);

        tarjeta.setStyle(
                "-fx-background-color: #252c32;" +
                        "-fx-border-color: #b99b52;" +
                        "-fx-border-width: 3;"
        );

        return tarjeta;
    }

    // ---------------------------------------------------------
    // TARJETA DE RECOMPENSAS
    // ---------------------------------------------------------

    private VBox crearTarjetaRecompensas() {

        Label titulo = new Label("RECOMPENSAS Y EXPERIENCIA");
        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle("-fx-text-fill: #8ed18b;");

        VBox boxRecompensas = new VBox(8);
        boxRecompensas.setAlignment(Pos.CENTER_LEFT);

        boolean hayRecompensas = false;

        for (String resultado : viewModel.getLogsCombate()) {

            if (esMensajeDeRecompensa(resultado)) {

                Label mensaje = new Label("★ " + resultado);
                mensaje.getStyleClass().add("texto-retro");
                mensaje.setWrapText(true);
                mensaje.setStyle("-fx-text-fill: #f3ecd0;");

                boxRecompensas.getChildren().add(mensaje);
                hayRecompensas = true;
            }
        }

        if (!hayRecompensas) {

            Label sinRecompensa = new Label(
                    "No se obtuvieron recompensas en este combate."
            );

            sinRecompensa.getStyleClass().add("texto-retro");
            sinRecompensa.setWrapText(true);
            sinRecompensa.setStyle("-fx-text-fill: #c4c0ad;");

            boxRecompensas.getChildren().add(sinRecompensa);
        }

        VBox tarjeta = new VBox(12, titulo, boxRecompensas);
        tarjeta.getStyleClass().add("caja-hud");
        tarjeta.setPadding(new Insets(18));
        tarjeta.setPrefWidth(500);
        tarjeta.setMaxWidth(700);

        tarjeta.setStyle(
                "-fx-background-color: #252c32;" +
                        "-fx-border-color: #8aab72;" +
                        "-fx-border-width: 3;"
        );

        return tarjeta;
    }

    private boolean esMensajeDeRecompensa(String mensaje) {
        return mensaje.contains("XP")
                || mensaje.contains("nivel")
                || mensaje.contains("obtenido");
    }

    // ---------------------------------------------------------
    // PANEL INFERIOR
    // ---------------------------------------------------------

    private VBox crearPanelInferior() {

        Label instruccion = new Label(
                "PULSA [ESPACIO] PARA REGRESAR AL MAPA"
        );

        instruccion.getStyleClass().add("texto-retro");
        instruccion.setStyle("-fx-text-fill: #f3ecd0;");

        Button continuar = new Button("CONTINUAR");
        continuar.getStyleClass().add("boton-retro");
        continuar.setOnAction(event ->
                volverAlMapaConTransicion()
        );

        VBox inferior = new VBox(12, instruccion, continuar);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(20));

        return inferior;
    }

    // ---------------------------------------------------------
    // CONTROLES Y TRANSICIÓN
    // ---------------------------------------------------------

    @Override
    protected void configurarEscena(Scene escena) {

        agregarCSS(escena, "/images/estilos/estilo.css");

        escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {

            if (evento.getCode() == KeyCode.SPACE) {
                volverAlMapaConTransicion();
                evento.consume();
            }
        });
    }

    private void volverAlMapaConTransicion() {

        if (transicionIniciada) {
            return;
        }

        transicionIniciada = true;

        FadeTransition fundido = new FadeTransition(
                Duration.millis(500),
                getEscena().getRoot()
        );

        fundido.setFromValue(1);
        fundido.setToValue(0);
        fundido.setInterpolator(Interpolator.EASE_IN);

        fundido.setOnFinished(evento ->
                pantallas.regresarAlMapa()
        );

        fundido.play();
    }
}
