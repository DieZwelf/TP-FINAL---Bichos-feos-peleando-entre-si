/**
 * Cargador encargado de interpretar y dibujar mapas de Tiled que utilizan
 * un tileset de colección de imágenes, donde cada tile tiene su propio
 * archivo de imagen en lugar de formar parte de una única planilla.
 *
 * Carga las imágenes individuales, las asocia con sus identificadores
 * y las dibuja en las posiciones correspondientes del mapa.
 *
 * Hereda de CargadorMapaTiled, que centraliza la lectura del mapa,
 * el procesamiento de las capas y la detección de colisiones.
 */

package vista.juego.mapa;

import com.google.gson.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CargadorMapaIndividual extends CargadorMapaTiled {

    private final Map<Integer, Image> imagenesTiles = new HashMap<>(); // Porque necesitamos poder buscar rápidamente la imagen correspondiente a un identificador.

    @Override
    protected String obtenerRutaMapa() {
        return "";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "";
    }



    /**
     * Carga las imágenes individuales definidas en el archivo JSON del tileset.
     *
     * Primero lee el JSON y obtiene la lista de tiles. Luego recorre cada uno,
     * recupera su ID y la ruta de su imagen, carga el archivo correspondiente
     * y almacena la imagen en el mapa imagenesTiles asociándola con su ID.
     *
     * De esta manera, las imágenes quedan disponibles para ser utilizadas
     * posteriormente al dibujar las capas del mapa.
     *
     * @throws Exception si ocurre algún error al leer el JSON o cargar las imágenes
     */
    @Override
    protected void prepararTileset() throws Exception {

        imagenesTiles.clear(); // Elimina todos los elementos que tenga almacenados el Map

        JsonObject tileset = leerJson(obtenerRutaTileset());

        JsonArray tiles = tileset.getAsJsonArray("tiles");

        String rutaTileset = obtenerRutaTileset();

        String carpetaBase = rutaTileset.substring(
                0,
                rutaTileset.lastIndexOf("/") + 1
        );

        for (JsonElement elemento : tiles) {

            JsonObject tile = elemento.getAsJsonObject();

            int id = tile.get("id").getAsInt();

            String rutaImagen = carpetaBase
                    + tile.get("image").getAsString();

            try (InputStream archivo =
                         getClass().getResourceAsStream(rutaImagen)) {

                if (archivo == null) {
                    throw new IllegalStateException(
                            "No se encontró la imagen: " + rutaImagen
                    );
                }

                Image imagen = new Image(archivo);

                if (imagen.isError()) {
                    throw new IllegalStateException(
                            "No se pudo cargar: " + rutaImagen,
                            imagen.getException()
                    );
                }

                imagenesTiles.put(id, imagen);
            }
        }
    }

    /**
     * Dibuja una capa del mapa utilizando imágenes individuales para cada tile.
     *
     * Recorre las filas y columnas de la capa, obtiene el GID de cada posición
     * y calcula el ID local correspondiente. Luego busca la imagen asociada
     * en el mapa imagenesTiles y la dibuja en el Canvas aplicando la escala.
     *
     * Las imágenes de mayor altura se alinean desde la parte inferior de su
     * celda para mantener una correcta ubicación dentro del mapa.
     *
     * @param datos identificadores globales de los tiles de la capa
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

                Image imagen = imagenesTiles.get(tileId);

                if (imagen == null) {
                    throw new IllegalStateException(
                            "No existe una imagen para el tile ID: " + tileId
                    );
                }

                double destinoX = x * anchoTile * ESCALA;

                // Alinea las imágenes grandes desde la parte inferior.
                double destinoY =
                        (y + 1) * altoTile * ESCALA
                                - imagen.getHeight() * ESCALA;

                gc.drawImage(
                        imagen,
                        destinoX,
                        destinoY,
                        imagen.getWidth() * ESCALA,
                        imagen.getHeight() * ESCALA
                );
            }
        }
    }
}
