package vista;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.geometry.Insets;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.animation.FadeTransition;

public class PantallaLogin implements IPantalla {

    private static final String CONTRASEÑA = "3254";

    public PantallaLogin() {
    }

    @Override
    public void mostrar(Stage stage) {
        PauseTransition pausa = new PauseTransition(Duration.seconds(1));

        StackPane root = new StackPane();
        root.getStyleClass().add("root-login");

        HBox botones = new HBox(24);
        botones.setAlignment(Pos.CENTER);

        Label labelLoginInicio = new Label("LOGIN");
        labelLoginInicio.getStyleClass().add("texto-login-inicio");

        VBox contenedorInicio = new VBox(20, labelLoginInicio, botones);
        contenedorInicio.setAlignment(Pos.CENTER);

        GestorPantallas pantallas = new GestorPantallas(stage);

        Button botonAdministrador = new Button("Administrador");
        Button botonUsuario = new Button("Usuario");
        Button botonIngresar = new Button("Ingresar");

        botonUsuario.getStyleClass().addAll("boton-retro", "boton-login");
        botonAdministrador.getStyleClass().addAll("boton-retro", "boton-login");
        botonIngresar.getStyleClass().add("boton-retro");

        Label titulo = new Label("Ingrese contraseña");
        titulo.getStyleClass().add("titulo-retro");
        Label mensaje = new Label();
        mensaje.getStyleClass().addAll("texto-retro", "mensaje-error");

        PasswordField contraseña = new PasswordField();
        contraseña.setPromptText("Contraseña");
        contraseña.getStyleClass().add("campo-retro");

        botones.getChildren().addAll(botonUsuario, botonAdministrador);

        VBox formulario = new VBox(12, titulo, contraseña, botonIngresar, mensaje);
        formulario.setAlignment(Pos.CENTER);
        formulario.setPadding(new Insets(30));
        formulario.getStyleClass().add("formulario-login");

        botonUsuario.setOnAction(e -> {
            pantallas.mostrarInicio();
        });

        botonAdministrador.setOnAction(e -> {
            contraseña.clear();
            mensaje.setText("");
            root.getChildren().setAll(formulario);
        });

        root.getChildren().setAll(contenedorInicio);

        botonIngresar.setDefaultButton(true);

        botonIngresar.setOnAction(e -> {
            String con = contraseña.getText();
            if(con.equals(CONTRASEÑA)){
                pantallas.mostrarInicio();
            } else {
                mensaje.setText("Contraseña incorrecta");

                pausa.setOnFinished(evento -> {
                    FadeTransition fade = new FadeTransition(
                            Duration.seconds(1),
                            mensaje
                    );

                    fade.setFromValue(1.0);
                    fade.setToValue(0.0);

                    fade.setOnFinished(fin -> {
                        mensaje.setText("");
                        mensaje.setOpacity(1.0);
                    });

                    fade.play();
                });

                pausa.play();
            }
        });

        botones.getStyleClass().add("caja-botones");

        Scene escena = new Scene(root, 900, 650);

        escena.getStylesheets().add(getClass().getResource("/images/pantallaMenu/estilo.css").toExternalForm());

        pantallas.mostrarEscena(escena);
    }
}