
package vista.base;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import vista.gestor.GestorPantallas;

public abstract class PantallaMenuBase extends PantallaBase {

    private StackPane root;
    private VBox botonesBox;
    private VBox encabezado;
    private StackPane capaBotones;

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

        // Reutilizar el reproductor compartido de GestorPantallas.
        MediaPlayer reproductor = pantallas.obtenerReproductorMenu();

        MediaView vistaVideo = new MediaView(reproductor);

        vistaVideo.setPreserveRatio(false);
        vistaVideo.setSmooth(true);

        vistaVideo.fitWidthProperty().bind(root.widthProperty());
        vistaVideo.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().add(vistaVideo);
    }

    private void crearEncabezadoYContenedor() {

        AnchorPane contenido = new AnchorPane();

        // Título.
        encabezado = new VBox(0);
        encabezado.setAlignment(Pos.TOP_LEFT);

        Label linea1 = new Label("POKEMON");
        Label linea2 = new Label("ARGENTINO");

        String estiloTitulo =
                "-fx-text-fill: #ffde00;" +
                        "-fx-font-family: 'Monospaced';" +
                        "-fx-font-size: 100px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 12, 0.4, 3, 4);";

        linea1.setStyle(estiloTitulo);
        linea2.setStyle(estiloTitulo);

        encabezado.getChildren().addAll(linea1, linea2);

        AnchorPane.setTopAnchor(encabezado, 35.0);
        AnchorPane.setLeftAnchor(encabezado, 45.0);

        // Capa de botones ocupando toda la pantalla.
        capaBotones = new StackPane();
        capaBotones.setAlignment(Pos.TOP_LEFT);

        AnchorPane.setTopAnchor(capaBotones, 0.0);
        AnchorPane.setBottomAnchor(capaBotones, 0.0);
        AnchorPane.setLeftAnchor(capaBotones, 0.0);
        AnchorPane.setRightAnchor(capaBotones, 0.0);

        botonesBox = new VBox(25);
        botonesBox.setAlignment(Pos.CENTER_LEFT);
        botonesBox.setPrefWidth(600);
        botonesBox.setMaxWidth(600);

        // Posición de los menús principales.
        botonesBox.setTranslateX(920);
        botonesBox.setTranslateY(-40);

        capaBotones.getChildren().add(botonesBox);

        contenido.getChildren().addAll(
                capaBotones,
                encabezado
        );

        root.getChildren().add(contenido);
    }

    private void animarEntrada() {

        botonesBox.setOpacity(0);

        FadeTransition entrada =
                new FadeTransition(Duration.millis(300), botonesBox);

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

        boton.setOnMouseEntered(evento ->
                boton.setText("▶ " + texto)
        );

        boton.setOnMouseExited(evento ->
                boton.setText(texto)
        );

        return boton;
    }

    protected abstract void configurarBotones();

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }

    @Override
    public void alOcultar() {
        // No detener ni destruir el reproductor compartido.
    }

    protected final void ocultarEncabezado() {

        // Ocultar solamente el título.
        encabezado.setVisible(false);
        encabezado.setManaged(false);

        // Centrar los slots de Cargar Partida.
        botonesBox.setTranslateX(0);
        botonesBox.setTranslateY(0);

        capaBotones.setAlignment(Pos.CENTER);
    }
}
