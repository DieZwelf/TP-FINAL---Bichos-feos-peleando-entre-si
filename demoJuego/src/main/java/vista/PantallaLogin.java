package vista;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
        PauseTransition pausa = new PauseTransition(Duration.seconds(1)); //para que un label desaparezca despues de 3 segundos

        StackPane root = new StackPane();
        VBox vbox = new VBox(12);
        GestorPantallas pantallas = new GestorPantallas(stage);

        Button botonAdministrador = new Button("Administrador");
        Button botonUsuario = new Button("Usuario");
        Button botonIngresar = new Button("Ingresar");

        Label titulo = new Label("Ingrese contraseña");
        Label mensaje = new Label();

        PasswordField contraseña = new PasswordField();
        contraseña.setPromptText("Contraseña");

        vbox.getChildren().addAll(botonUsuario, botonAdministrador);

        VBox formulario = new VBox(
                12, titulo, contraseña, botonIngresar, mensaje
        );

        botonUsuario.setOnAction( e -> {
            pantallas.mostrarInicio();
        });


        botonAdministrador.setOnAction(e -> {
            contraseña.clear();
            mensaje.setText("");
            root.getChildren().setAll(formulario);
        });

       root.getChildren().setAll(vbox);

        botonIngresar.setDefaultButton(true);

        botonIngresar.setOnAction(e -> {
            String con = contraseña.getText();
            if(con.equals(CONTRASEÑA)){
                pantallas.mostrarInicio();
            }else{
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

        formulario.setAlignment(Pos.CENTER);
        formulario.setPadding(new Insets(30));
        formulario.setMaxWidth(360);

        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30));
        vbox.setMaxWidth(360);

       Scene escena = new Scene(root,900, 650);
       pantallas.mostrarEscena(escena);

        }
}
