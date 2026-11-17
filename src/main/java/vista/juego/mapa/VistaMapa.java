/**
 * Representa visualmente el mapa del juego.
 *
 * Hereda de Pane para poder contener y mostrar el Canvas donde se
 * dibuja el mapa. También almacena las figuras que representan las
 * zonas de colisión y una referencia al cargador encargado de
 * obtener los datos del mapa.
 *
 * Se encarga de cargar el mapa, establecer sus dimensiones y
 * proporcionar acceso a las colisiones y al cargador utilizado.
 */

package vista.juego.mapa;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.List;

public class VistaMapa extends Pane {

    private final Canvas canvas;
    private final List<Shape> colisiones;
    private final CargadorMapaTiled cargador;

    public VistaMapa(CargadorMapaTiled cargador) {

        this.cargador = cargador;

        try {
            canvas = cargador.cargarMapa();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo cargar el mapa de Tiled.", e
            );
        }

        colisiones = cargador.getColisiones();

        setMinSize(canvas.getWidth(), canvas.getHeight());
        setPrefSize(canvas.getWidth(), canvas.getHeight());
        setMaxSize(canvas.getWidth(), canvas.getHeight());

        getChildren().add(canvas);
    }

    public List<Shape> getColisiones() {
        return colisiones;
    }

    public CargadorMapaTiled getCargador() {
        return cargador;
    }
}
