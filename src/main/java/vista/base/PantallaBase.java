package vista.base;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vista.GestorPantallas;
import vista.IPantalla;

/**
 * Base común de las pantallas. Centraliza la creación de la Scene y
 * el ciclo de vida al entrar o salir de una pantalla.
 */
public abstract class PantallaBase implements IPantalla {

    protected final GestorPantallas pantallas;
    protected static final double ANCHO_PANTALLA = 1400;
    protected static final double ALTO_PANTALLA = 800;
    private Scene escena;

    protected PantallaBase(GestorPantallas pantallas) {
        this.pantallas = pantallas;
    }



    /** Cada pantalla construye su contenido en este método. */
    protected abstract Parent crearContenido();

    /** Permite agregar CSS, eventos de teclado o bindings a la escena. */
    protected void configurarEscena(Scene escena) {
    }

    /** Se ejecuta cuando la pantalla vuelve a quedar visible. */
    public void alMostrar() {
    }

    /** Se ejecuta antes de abandonar la pantalla. */
    public void alOcultar() {
    }


    @Override
    public final void mostrar(Stage stage) {
        escena = new Scene(crearContenido(), ANCHO_PANTALLA, ALTO_PANTALLA);
        configurarEscena(escena);
        pantallas.mostrarPantalla(this, escena);
    }

    public final Scene getEscena() {
        return escena;
    }

    protected final void agregarCSS(Scene escena, String ruta) {
        var recurso = getClass().getResource(ruta);
        if (recurso != null) {
            escena.getStylesheets().add(recurso.toExternalForm());
        } else {
            System.err.println("No se encontró la hoja de estilos: " + ruta);
        }
    }
}
