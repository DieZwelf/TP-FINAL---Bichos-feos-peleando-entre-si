
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

/**
 * La clase PANTALLALOGIN representa la pantalla de inicio de sesión.
 * Permite ingresar como usuario o administrador.
 * Contiene un menú inicial, un formulario de contraseña y mensajes de error.
 * Tiene un constructor que recibe el gestor de pantallas.
 * Otros métodos: crearContenido(), crearFondo(), crearMenuInicial(),
 * crearFormularioAdministrador(), crearBoton(), mostrarFormulario(),
 * mostrarMenuInicial() y validarContrasena().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class PantallaLogin extends PantallaBase {

    private static final String CONTRASENA_ADMIN = "3254";

    private StackPane root;
    private ImageView fondo;
    private VBox menuInicial;
    private VBox formularioAdmin;
    private Label mensajeError;
    private PasswordField campoContrasena;

    /**
     * Construye la pantalla de inicio de sesión.
     *
     * @param pantallas gestor encargado de la navegación entre pantallas
     */
    public PantallaLogin(GestorPantallas pantallas) {
        super(pantallas);
    }

    /**
     * Construye los componentes visuales de la pantalla de inicio de sesión.
     *
     * @return contenedor principal de la pantalla
     */
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

    /**
     * Carga y configura la imagen de fondo de la pantalla.
     * Ajusta sus dimensiones al tamaño del contenedor principal.
     */
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

    /**
     * Construye el menú inicial con las opciones de usuario y administrador.
     * Configura las acciones correspondientes a cada botón.
     */
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

    /**
     * Construye el formulario de acceso del administrador.
     * Configura el campo de contraseña, los botones y el mensaje de error.
     */
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

    /**
     * Crea un botón y le asigna los estilos CSS recibidos.
     *
     * @param texto texto que mostrará el botón
     * @param estilos clases CSS que se aplicarán al botón
     * @return botón configurado
     */
    private Button crearBoton(String texto, String... estilos) {
        Button boton = new Button(texto);
        boton.getStyleClass().addAll(estilos);
        return boton;
    }

    /**
     * Muestra el formulario de administrador.
     * Limpia previamente el campo de contraseña y el mensaje de error.
     */
    private void mostrarFormulario() {
        campoContrasena.clear();
        mensajeError.setText("");
        root.getChildren().setAll(fondo, formularioAdmin);
    }

    /**
     * Regresa al menú inicial de selección de tipo de usuario.
     */
    private void mostrarMenuInicial() {
        root.getChildren().setAll(fondo, menuInicial);
    }

    /**
     * Comprueba la contraseña ingresada por el administrador.
     * Si es correcta, habilita el modo administrador.
     * Si es incorrecta, muestra un mensaje temporal con una animación.
     */
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

    /**
     * Configura la escena y agrega la hoja de estilos CSS correspondiente.
     *
     * @param escena escena JavaFX que se va a configurar
     */
    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
