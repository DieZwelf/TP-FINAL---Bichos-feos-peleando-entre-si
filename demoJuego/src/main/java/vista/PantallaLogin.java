package vista;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.geometry.Insets;

public class PantallaLogin implements IPantalla {

    private static final String CONTRASEÑA = "3254";

    public PantallaLogin() {
    }

    @Override
    public void mostrar(Stage stage) {
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

        botonIngresar.setOnAction(e -> {
            String con = contraseña.getText();
            if(con.equals(CONTRASEÑA)){
                pantallas.mostrarInicio();
            }else{
                mensaje.setText("Contrseña incorrecta");
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
