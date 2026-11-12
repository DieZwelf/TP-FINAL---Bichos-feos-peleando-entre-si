/**
 * Cargador encargado de interpretar y dibujar mapas de Tiled que utilizan
 * un tileset de tipo planilla (spritesheet), es decir, una única imagen
 * que contiene todos los tiles organizados en una cuadrícula.
 *
 * Se encarga de cargar la imagen, identificar los tiles correspondientes
 * a cada GID del mapa y dibujarlos en el Canvas.
 *
 * Hereda de CargadorMapaTiled, que centraliza la lectura del mapa,
 * el procesamiento de las capas y la detección de colisiones.
 */

package vista.juego;

import com.google.gson.JsonArray;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

public class CargadorMapaPlanilla extends CargadorMapaTiled {

    private static final int COLUMNAS_TILESET = 27;

    private Image tileset;

    @Override
    protected String obtenerRutaMapa() {
        return "";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "";
    }

    @Override
    protected void prepararTileset() throws Exception {

        try (InputStream archivo =
                     getClass().getResourceAsStream(obtenerRutaTileset())) {

            if (archivo == null) {
                throw new IllegalStateException(
                        "No se encontró el tileset: " + obtenerRutaTileset()
                );
            }

            tileset = new Image(archivo);

            if (tileset.isError()) {
                throw new IllegalStateException(
                        "No se pudo cargar la planilla PNG.",
                        tileset.getException()
                );
            }
        }
    }

    @Override
    protected void dibujarCapaTiles(
            JsonArray datos,
            int anchoCapa,
            int altoCapa,
            int anchoTile,
            int altoTile,
            int firstGid,
            GraphicsContext gc
    ) {

        for (int y = 0; y < altoCapa; y++) {

            for (int x = 0; x < anchoCapa; x++) {

                int gid = datos.get(y * anchoCapa + x).getAsInt();

                if (gid == 0) {
                    continue;
                }

                int tileId = gid - firstGid;

                int origenX =
                        (tileId % COLUMNAS_TILESET) * TAM_TILE;

                int origenY =
                        (tileId / COLUMNAS_TILESET) * TAM_TILE;

                double destinoX = x * anchoTile * ESCALA;
                double destinoY = y * altoTile * ESCALA;

                gc.drawImage(
                        tileset,
                        origenX,
                        origenY,
                        TAM_TILE,
                        TAM_TILE,
                        destinoX,
                        destinoY,
                        anchoTile * ESCALA + 0.5,
                        altoTile * ESCALA + 0.5
                );
            }
        }
    }
}
