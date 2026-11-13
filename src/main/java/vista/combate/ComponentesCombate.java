
package vista.combate;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public final class ComponentesCombate {

    private ComponentesCombate() {
    }

    public static Label crearLabel(String estilo) {
        Label label = new Label();
        label.getStyleClass().add(estilo);
        return label;
    }

    public static ProgressBar crearBarraVida() {
        ProgressBar barra = new ProgressBar(1.0);
        barra.setPrefWidth(180);
        barra.getStyleClass().add("barra-hp");
        return barra;
    }

    public static Button crearBoton(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-retro");
        boton.setPrefSize(320, 80);
        return boton;
    }

    public static Label crearTituloResultado(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().add("texto-retro");
        titulo.setStyle(
                "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #ffde00;"
        );
        return titulo;
    }

    public static Label crearMensajeResultado(String texto) {
        Label mensaje = new Label(texto);
        mensaje.getStyleClass().add("texto-retro");
        mensaje.setWrapText(true);
        return mensaje;
    }
}
