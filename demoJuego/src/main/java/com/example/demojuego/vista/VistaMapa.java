
package com.example.demojuego.vista;

import com.example.demojuego.model.Mapa;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import javafx.geometry.Rectangle2D;
import java.util.List;

public class VistaMapa extends Pane {

    private final Canvas canvas;
    private final List<Rectangle2D> colisiones;

    public List<Rectangle2D> getColisiones() {
        return colisiones;
    }

    public VistaMapa(Mapa mapa) {
        CargadorMapaTiled cargador = new CargadorMapaTiled();

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
}
