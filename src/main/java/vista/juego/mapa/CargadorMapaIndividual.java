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

    private final Map<Integer, Image> imagenesTiles = new HashMap<>();

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

        imagenesTiles.clear();

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
