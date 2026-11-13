
package vista.base;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import vista.gestor.GestorPantallas;

/**
 * Clase base para las pantallas de menú del juego.
 *
 * Administra el fondo animado, el encabezado, el contenedor
 * de botones y las animaciones de entrada.
 */
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

    /**
     * Coloca el video de fondo compartido por los menús.
     */
    private void crearFondoAnimado() {

        MediaPlayer reproductor = pantallas.obtenerReproductorMenu();

        MediaView vistaVideo = new MediaView(reproductor);

        vistaVideo.setPreserveRatio(false);
        vistaVideo.setSmooth(true);

        vistaVideo.fitWidthProperty().bind(root.widthProperty());
        vistaVideo.fitHeightProperty().bind(root.heightProperty());

        root.getChildren().add(vistaVideo);
    }

    /**
     * Construye el encabezado visual y el contenedor de botones.
     */
    private void crearEncabezadoYContenedor() {
        AnchorPane contenido = new AnchorPane();

        encabezado = new VBox(0);
        encabezado.setAlignment(Pos.TOP_LEFT);
        encabezado.getStyleClass().add("logo-juego");

        Label linea1 = new Label("POKEMON");
        linea1.getStyleClass().add("titulo-pokemon");

        Label linea2 = new Label("ARGENTINO");
        linea2.getStyleClass().add("titulo-argentino");

        Region separador = new Region();
        separador.getStyleClass().add("titulo-separador");

        encabezado.getChildren().addAll(linea1, linea2, separador);

        AnchorPane.setTopAnchor(encabezado, 45.0);
        AnchorPane.setLeftAnchor(encabezado, 55.0);

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

        botonesBox.setTranslateX(920);
        botonesBox.setTranslateY(-40);

        capaBotones.getChildren().add(botonesBox);

        contenido.getChildren().addAll(capaBotones, encabezado);
        root.getChildren().add(contenido);
    }

    /**
     * Aplica una transición de aparición a los botones.
     */
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

    /**
     * Crea un botón con el estilo general del menú.
     */
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

    /**
     * Oculta el encabezado y centra los botones.
     * Se utiliza en pantallas que no necesitan mostrar el título.
     */
    protected final void ocultarEncabezado() {

        encabezado.setVisible(false);
        encabezado.setManaged(false);

        botonesBox.setTranslateX(0);
        botonesBox.setTranslateY(0);

        capaBotones.setAlignment(Pos.CENTER);
    }
}
