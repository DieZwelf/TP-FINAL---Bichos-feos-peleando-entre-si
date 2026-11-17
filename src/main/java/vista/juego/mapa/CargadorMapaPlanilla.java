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

package vista.juego.mapa;

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

    /**
     * Dibuja una capa del mapa utilizando una planilla de imágenes (tileset).
     *
     * Recorre las filas y columnas de la capa, obtiene el GID de cada posición
     * y calcula qué tile corresponde dentro del tileset. Luego determina las
     * coordenadas de origen en la imagen y las coordenadas de destino en el
     * Canvas, aplicando la escala correspondiente.
     *
     * Las posiciones vacías (GID 0) se ignoran y los tiles restantes se
     * dibujan mediante GraphicsContext.
     *
     * @param datos identificadores de los tiles de la capa
     * @param anchoCapa cantidad de tiles de ancho de la capa
     * @param altoCapa cantidad de tiles de alto de la capa
     * @param anchoTile ancho de cada tile en píxeles
     * @param altoTile alto de cada tile en píxeles
     * @param firstGid primer identificador global del tileset
     * @param gc contexto gráfico utilizado para dibujar en el Canvas
     */
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
