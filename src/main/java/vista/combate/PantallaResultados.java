
package vista.combate;

import controlador.CombateViewModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import model.EstadoCombate;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import java.net.URL;

public class PantallaResultados extends PantallaBase {

    private final CombateViewModel viewModel;

    public PantallaResultados(
            CombateViewModel viewModel,
            GestorPantallas pantallas
    ) {
        super(pantallas);
        this.viewModel = viewModel;
    }

    @Override
    protected StackPane crearContenido() {
        StackPane root = new StackPane();

        ImageView fondo = crearFondo(root);

        BorderPane pantalla = new BorderPane();
        pantalla.getStyleClass().add("pantalla-combate-root");

        pantalla.setStyle(
                "-fx-background-color: rgba(12, 20, 25, 0.88);"
        );

        Label titulo = ComponentesCombate.crearTituloResultado(
                obtenerTitulo()
        );

        VBox encabezado = new VBox(titulo);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setPadding(new Insets(25));

        pantalla.setTop(encabezado);

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

        pantalla.setCenter(contenedor);
        pantalla.setBottom(crearPanelInferior());

        root.getChildren().addAll(fondo, pantalla);

        return root;
    }

    private ImageView crearFondo(StackPane root) {
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

    private String obtenerTitulo() {
        EstadoCombate estado =
                viewModel.getModeloCombate().getEstado();

        switch (estado) {
            case VICTORIA:
                return "¡VICTORIA!";

            case DERROTA:
                return "DERROTA";

            case HUIDA:
                return "¡ESCAPASTE!";

            case CAPTURA:
                return "¡POKÉMON CAPTURADO!";

            default:
                return "COMBATE FINALIZADO";
        }
    }

    private VBox crearTarjetaRegistro() {
        Label titulo = new Label("REGISTRO DEL COMBATE");
        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle("-fx-text-fill: #ffde00;");

        VBox boxLogs = new VBox(5);
        boxLogs.setAlignment(Pos.CENTER_LEFT);

        for (String resultado : viewModel.getLogsCombate()) {
            if (!esMensajeDeRecompensa(resultado)) {
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

        VBox tarjeta = new VBox(10, titulo, scrollLogs);
        tarjeta.getStyleClass().add("caja-hud");
        tarjeta.setPadding(new Insets(15));
        tarjeta.setPrefWidth(500);
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
            if (esMensajeDeRecompensa(resultado)) {
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
        tarjeta.setPrefWidth(500);
        tarjeta.setMaxWidth(700);

        return tarjeta;
    }

    private boolean esMensajeDeRecompensa(String mensaje) {
        return mensaje.contains("XP")
                || mensaje.contains("nivel")
                || mensaje.contains("obtenido");
    }

    private VBox crearPanelInferior() {
        Label instruccion = new Label(
                "PULSA [ESPACIO] PARA REGRESAR AL MAPA"
        );

        instruccion.getStyleClass().add("texto-retro");
        instruccion.setStyle("-fx-text-fill: #f3ecd0;");

        Button continuar = new Button("CONTINUAR");
        continuar.getStyleClass().add("boton-retro");
        continuar.setOnAction(event ->
                pantallas.regresarAlMapa()
        );

        VBox inferior = new VBox(10, instruccion, continuar);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(20));

        return inferior;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/estilos/estilo.css");

        escena.addEventFilter(
                KeyEvent.KEY_PRESSED,
                evento -> {
                    if (evento.getCode() == KeyCode.SPACE) {
                        pantallas.regresarAlMapa();
                        evento.consume();
                    }
                }
        );
    }

    @Override
    public void alMostrar() {
        Platform.runLater(() -> {
            if (getEscena() != null) {
                getEscena().getRoot().requestFocus();
            }
        });
    }
}
