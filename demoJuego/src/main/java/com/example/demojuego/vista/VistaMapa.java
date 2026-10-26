package com.example.demojuego.vista;

import com.example.demojuego.model.Mapa;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class VistaMapa extends Pane {

    private static final int TAM_CELDA = 50;

    private final Mapa mapa;
    private final Canvas canvas;

    private final Image pasto;
    private final Image agua;
    private final Image tierra;
    private final Image piedra;

    public VistaMapa(Mapa mapa) {
        this.mapa = mapa;

        pasto = cargarImagen("/images/mapa/pasto.png");
        agua = cargarImagen("/images/mapa/agua.png");
        tierra = cargarImagen("/images/mapa/tierra.png");
        piedra = cargarImagen("/images/mapa/piedra.png");

        double ancho = (double) mapa.getColumnas() * TAM_CELDA;
        double alto = (double) mapa.getFilas() * TAM_CELDA;

        canvas = new Canvas(ancho, alto);

        setMinSize(ancho, alto);
        setPrefSize(ancho, alto);
        setMaxSize(ancho, alto);

        getChildren().add(canvas);

        dibujarMapa();
    }

    private Image cargarImagen(String ruta) {
        var recurso = getClass().getResource(ruta);

        if (recurso == null) {
            throw new IllegalArgumentException(
                    "No se encontró la imagen: " + ruta
            );
        }

        return new Image(recurso.toExternalForm());
    }

    private void dibujarMapa() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int fila = 0; fila < mapa.getFilas(); fila++) {
            for (int columna = 0;
                 columna < mapa.getColumnas();
                 columna++) {

                Image imagen = obtenerImagen(
                        mapa.getTerreno(fila, columna)
                );

                double x = (double) columna * TAM_CELDA;
                double y = (double) fila * TAM_CELDA;

                gc.drawImage(
                        imagen,
                        x, y,
                        TAM_CELDA, TAM_CELDA
                );
            }
        }
    }

    private Image obtenerImagen(int tipo) {
        return switch (tipo) {
            case Mapa.AGUA -> agua;
            case Mapa.TIERRA -> tierra;
            case Mapa.PIEDRA -> piedra;
            default -> pasto;
        };
    }
}
