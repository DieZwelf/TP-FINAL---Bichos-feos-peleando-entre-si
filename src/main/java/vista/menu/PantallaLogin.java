package vista.menu;

import model.TipoJugador;
import vista.gestor.GestorPantallas;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import vista.base.PantallaBase;

import java.net.URL;

public class PantallaLogin extends PantallaBase {

    private static final String CONTRASENA_ADMIN = "3254";

    private StackPane root;
    private ImageView fondo;
    private VBox menuInicial;
    private VBox formularioAdmin;
    private Label mensajeError;
    private PasswordField campoContrasena;

    public PantallaLogin(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected Parent crearContenido() {
        root = new StackPane();
        root.getStyleClass().add("root-login");
        crearFondo();
        crearMenuInicial();
        crearFormularioAdministrador();
        root.getChildren().setAll(fondo, menuInicial);
        return root;
    }

    private void crearFondo() {
        URL recurso = getClass().getResource("/images/pantallaMenu/prueba.jpg");
        if (recurso == null) {
            throw new IllegalStateException("No se encontró /images/pantallaMenu/prueba.jpg");
        }
        fondo = new ImageView(new Image(recurso.toExternalForm()));
        fondo.setPreserveRatio(false);
        fondo.fitWidthProperty().bind(root.widthProperty());
        fondo.fitHeightProperty().bind(root.heightProperty());
    }

    private void crearMenuInicial() {
        Label titulo = new Label("LOGIN");
        titulo.getStyleClass().add("texto-login-inicio");

        Button usuario = crearBoton("Usuario", "boton-retro", "boton-login");
        Button administrador = crearBoton("Administrador", "boton-retro", "boton-login");
        HBox botones = new HBox(24, usuario, administrador);
        botones.setAlignment(Pos.CENTER);
        botones.getStyleClass().add("caja-botones");

        menuInicial = new VBox(20, titulo, botones);
        menuInicial.setAlignment(Pos.CENTER);

        usuario.setOnAction(e -> {
            pantallas.setModoAdministrador(false);
            pantallas.mostrarInicio();
        });
        administrador.setOnAction(e -> mostrarFormulario());
    }

    private void crearFormularioAdministrador() {
        Label titulo = new Label("Ingrese contraseña");
        titulo.getStyleClass().add("titulo-retro");

        campoContrasena = new PasswordField();
        campoContrasena.setPromptText("Contraseña");
        campoContrasena.getStyleClass().add("campo-retro");

        Button ingresar = crearBoton("Ingresar", "boton-retro");
        Button volver = crearBoton("Volver", "boton-retro");
        mensajeError = new Label();
        mensajeError.getStyleClass().addAll("texto-retro", "mensaje-error");

        formularioAdmin = new VBox(12, titulo, campoContrasena, ingresar, mensajeError, volver);
        formularioAdmin.setAlignment(Pos.CENTER);
        formularioAdmin.setPadding(new Insets(30));
        formularioAdmin.getStyleClass().add("formulario-login");

        StackPane.setAlignment(formularioAdmin, Pos.CENTER);
        ingresar.setDefaultButton(true);
        ingresar.setOnAction(e -> validarContrasena());
        volver.setOnAction(e -> mostrarMenuInicial());
    }

    private Button crearBoton(String texto, String... estilos) {
        Button boton = new Button(texto);
        boton.getStyleClass().addAll(estilos);
        return boton;
    }

    private void mostrarFormulario() {
        campoContrasena.clear();
        mensajeError.setText("");
        root.getChildren().setAll(fondo, formularioAdmin);
    }

    private void mostrarMenuInicial() {
        root.getChildren().setAll(fondo, menuInicial);
    }

    private void validarContrasena() {
        if (CONTRASENA_ADMIN.equals(campoContrasena.getText())) {
            pantallas.setTipo(TipoJugador.ADMINISTRADOR);
            pantallas.mostrarInicio();
            pantallas.setModoAdministrador(true);
            pantallas.mostrarInicio();
            return;
        }

        mensajeError.setOpacity(1);
        mensajeError.setText("Contraseña incorrecta");
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));
        pausa.setOnFinished(e -> {
            FadeTransition fade = new FadeTransition(Duration.seconds(1), mensajeError);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(fin -> {
                mensajeError.setText("");
                mensajeError.setOpacity(1);
            });
            fade.play();
        });
        pausa.play();
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
