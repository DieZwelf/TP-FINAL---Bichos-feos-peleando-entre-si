
package vista;

import model.Mapa;
import model.Jugador;
import controlador.JuegoController;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class VistaJuego extends Pane implements IPantalla {

    private final Jugador jugador;
    private final VistaJugador vistaJugador;
    private final VistaMapa vistaMapa;
    private final Pane mundo = new Pane();

    private JuegoController controlador;
    private Scene escena;
    protected GestorPantallas pantallas;

    public VistaJuego(Mapa mapa, Jugador jugador, GestorPantallas pantallas) {
        this.jugador = jugador;
        this.pantallas = pantallas;
        vistaMapa = new VistaMapa(mapa);
        vistaJugador = new VistaJugador(jugador);

        mundo.getChildren().addAll(vistaMapa, vistaJugador);
        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());

        getChildren().add(mundo);
    }

    public VistaJugador getVistaJugador() {
        return vistaJugador;
    }

    @Override
    public void mostrar(Stage stage) {
        escena = new Scene(this, 1000, 800);

        escena.widthProperty().addListener(
                (obs, anterior, nuevo) -> actualizarCamara()
        );
        escena.heightProperty().addListener(
                (obs, anterior, nuevo) -> actualizarCamara()
        );

        controlador = new JuegoController(
                jugador,
                vistaJugador,
                this,
                escena
        );


        actualizarCamara();
        controlador.iniciarGameLoop();
        pantallas.mostrarEscena(escena);
    }


    public void actualizarCamara() {
        if (escena == null) {
            return;
        }

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();

        double anchoMapa = vistaMapa.getPrefWidth();
        double altoMapa = vistaMapa.getPrefHeight();

        double centroJugadorX = jugador.getX() + 24;
        double centroJugadorY = jugador.getY() + 24;

        double desplazamientoX = anchoVentana / 2 - centroJugadorX;
        double desplazamientoY = altoVentana / 2 - centroJugadorY;

        // No mostrar el exterior del mapa por los costados.
        if (anchoMapa >= anchoVentana) {
            desplazamientoX = Math.max(anchoVentana - anchoMapa, Math.min(0, desplazamientoX));
        } else {
            desplazamientoX = (anchoVentana - anchoMapa) / 2;
        }

        if (altoMapa >= altoVentana) {
            desplazamientoY = Math.max(altoVentana - altoMapa, Math.min(0, desplazamientoY));
        } else {
            desplazamientoY = (altoVentana - altoMapa) / 2;
        }

        mundo.setTranslateX(desplazamientoX);
        mundo.setTranslateY(desplazamientoY);
    }

}
