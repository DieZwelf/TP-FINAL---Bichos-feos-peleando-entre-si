package vista.base;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.Scene;
import javafx.util.Duration;
import vista.GestorPantallas;

import java.net.URL;

/**
 * Base para los menús que comparten video de fondo, título y botones.
 * Sus atributos son de instancia: no se comparte estado estático entre pantallas.
 */
public abstract class PantallaMenuBase extends PantallaBase {

    private StackPane root;
    private VBox botonesBox;
    private MediaPlayer reproductor;

    protected PantallaMenuBase(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected final Parent crearContenido() {
        root = new StackPane();
        crearFondoAnimado();
        crearEncabezadoYContenedor();
        configurarBotones();
        animarEntrada();
        return root;
    }

    private void crearFondoAnimado() {
        URL archivoVideo = getClass().getResource("/videos/fondo.mp4");
        if (archivoVideo == null) {
            throw new IllegalStateException("No se encontró /videos/fondo.mp4");
        }

        Media media = new Media(archivoVideo.toExternalForm());
        reproductor = new MediaPlayer(media);
        reproductor.setCycleCount(MediaPlayer.INDEFINITE);

        MediaView vistaVideo = new MediaView(reproductor);
        vistaVideo.setPreserveRatio(false);
        vistaVideo.fitWidthProperty().bind(root.widthProperty());
        vistaVideo.fitHeightProperty().bind(root.heightProperty());
        reproductor.setOnReady(() -> {
            reproductor.play();
        });
        root.getChildren().add(vistaVideo);
    }

    private void crearEncabezadoYContenedor() {
        AnchorPane contenido = new AnchorPane();
        VBox titulo = new VBox(0);
        titulo.setAlignment(Pos.TOP_LEFT);

        Label linea1 = new Label("POKEMON");
        Label linea2 = new Label("ARGENTINO");
        String estiloTitulo = "-fx-text-fill: #ffde00;" +
                "-fx-font-family: 'Monospaced';" +
                "-fx-font-size: 100px;" +
                "-fx-font-weight: bold;";
        linea1.setStyle(estiloTitulo);
        linea2.setStyle(estiloTitulo);
        titulo.getChildren().addAll(linea1, linea2);

        AnchorPane.setTopAnchor(titulo, 35.0);
        AnchorPane.setLeftAnchor(titulo, 45.0);

        botonesBox = new VBox(37);
        botonesBox.setAlignment(Pos.TOP_LEFT);
        botonesBox.setPrefWidth(600);
        botonesBox.setTranslateX(920);
        botonesBox.setTranslateY(120);

        contenido.getChildren().addAll(titulo, botonesBox);
        root.getChildren().add(contenido);
    }

    private void animarEntrada() {
        botonesBox.setOpacity(0);
        FadeTransition entrada = new FadeTransition(Duration.millis(250), botonesBox);
        entrada.setFromValue(0);
        entrada.setToValue(1);
        entrada.play();
    }

    protected final VBox getBotonesBox() {
        return botonesBox;
    }

    protected final Button crearBotonEstilizado(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-menu");
        boton.setOnMouseEntered(evento -> boton.setText("▶ " + texto));
        boton.setOnMouseExited(evento -> boton.setText(texto));
        return boton;
    }

    protected abstract void configurarBotones();

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }

    @Override
    public void alOcultar() {
        liberarReproductor();
    }

    private void liberarReproductor() {
        if (reproductor != null) {
            reproductor.stop();
            reproductor.dispose();
            reproductor = null;
        }
    }
}
