
/**
 * La clase GestorPantallas centraliza la navegación entre las
 * distintas pantallas del juego.
 *
 * Su objetivo es separar la lógica de navegación de la construcción
 * de las interfaces gráficas, permitiendo cambiar de pantalla
 * desde un único lugar y facilitando el mantenimiento y la
 * organización del código.
 *
 * Además, administra la ventana principal (Stage) que se utiliza
 * para mostrar las diferentes pantallas del juego.
 */

package com.example.demojuego.vista;

import com.example.demojuego.model.Jugador;
import com.example.demojuego.model.Mapa;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class GestorPantallas {

    private final Stage stage;

    public GestorPantallas(Stage stage) {
        this.stage = stage;
    }

    //Metodo de configuracion del Stage
    public void mostrarEscena(Scene escena) {
        stage.setTitle("Bichos feos");
        stage.setScene(escena);
        stage.setFullScreen(true);
        stage.show();
    }

    // Mostrar menú principal
    public void mostrarInicio() {
        new PantallaInicio(this).mostrar(stage);
    }

    // Ir a la pantalla de nueva partida
    public void mostrarNuevaPartida() {
        new PantallaPartidaNueva(this).mostrar(stage);
    }

    // Crear los objetos necesarios e iniciar el juego
    public void iniciarPartida() {
        Mapa mapa = new Mapa(10, 10);
        Jugador jugador = new Jugador(0, 0, 3);

        new VistaJuego(mapa, jugador, this).mostrar(stage);
    }

    public void mostrarCargarPartida (){
        new PantallaCargarPartida(this).mostrar(stage);
    }

    // Cerrar el juego
    public void salir() {
        stage.close();
    }
}
