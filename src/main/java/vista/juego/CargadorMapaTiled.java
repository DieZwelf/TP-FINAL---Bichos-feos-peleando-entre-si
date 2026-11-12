/**
 * Clase abstracta base encargada de centralizar la carga y el procesamiento
 * de mapas creados con Tiled.
 *
 * Lee la estructura del mapa en formato JSON, crea el Canvas, procesa
 * las capas, administra las colisiones y proporciona las transiciones
 * entre mapas.
 *
 * Define métodos abstractos para que las clases hijas implementen
 * la carga del tileset y el dibujo de los tiles según el formato
 * de imágenes utilizado.
 */

package vista.juego;

import com.google.gson.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class CargadorMapaTiled {

    protected static final int TAM_TILE = 16;
    protected static final int ESCALA = 3;

    private final List<Shape> colisiones = new ArrayList<>();

    protected abstract String obtenerRutaMapa();

    protected abstract String obtenerRutaTileset();

    // Cada cargador prepara sus recursos de manera diferente.
    protected abstract void prepararTileset() throws Exception;

    // Cada cargador dibuja sus tiles de manera diferente.
    protected abstract void dibujarCapaTiles(
            JsonArray datos,
            int anchoCapa,
            int altoCapa,
            int anchoTile,
            int altoTile,
            int firstGid,
            GraphicsContext gc
    );

    public final Canvas cargarMapa() throws Exception {

        colisiones.clear();

        JsonObject mapa = leerJson(obtenerRutaMapa());

        int ancho = mapa.get("width").getAsInt();
        int alto = mapa.get("height").getAsInt();

        int anchoTile = mapa.get("tilewidth").getAsInt();
        int altoTile = mapa.get("tileheight").getAsInt();

        double anchoCanvas = ancho * anchoTile * ESCALA;
        double altoCanvas = alto * altoTile * ESCALA;

        Canvas canvas = new Canvas(anchoCanvas, altoCanvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        agregarLimites(anchoCanvas, altoCanvas);

        JsonArray tilesets = mapa.getAsJsonArray("tilesets");

        if (tilesets == null || tilesets.isEmpty()) {
            throw new IllegalStateException(
                    "El mapa no tiene ningún tileset asociado."
            );
        }

        int firstGid = tilesets.get(0)
                .getAsJsonObject()
                .get("firstgid")
                .getAsInt();

        prepararTileset();

        dibujarCapas(
                mapa,
                gc,
                ancho,
                anchoTile,
                altoTile,
                firstGid
        );

        return canvas;
    }

    protected JsonObject leerJson(String ruta) throws Exception {

        InputStream archivo = getClass().getResourceAsStream(ruta);

        if (archivo == null) {
            throw new IllegalStateException(
                    "No se encontró el archivo: " + ruta
            );
        }

        try (InputStreamReader lector =
                     new InputStreamReader(
                             archivo,
                             StandardCharsets.UTF_8
                     )) {

            return JsonParser.parseReader(lector).getAsJsonObject();
        }
    }

    protected void agregarLimites(double ancho, double alto) {

        colisiones.add(new Rectangle(-10, 0, 10, alto));
        colisiones.add(new Rectangle(ancho, 0, 10, alto));
        colisiones.add(new Rectangle(0, -10, ancho, 10));
        colisiones.add(new Rectangle(0, alto, ancho, 10));
    }

    protected void dibujarCapas(
            JsonObject mapa,
            GraphicsContext gc,
            int anchoMapa,
            int anchoTile,
            int altoTile,
            int firstGid
    ) {

        JsonArray capas = mapa.getAsJsonArray("layers");

        for (JsonElement elemento : capas) {

            JsonObject capa = elemento.getAsJsonObject();

            String nombre = capa.get("name").getAsString();
            String tipo = capa.get("type").getAsString();

            if (nombre.equals("colisiones")) {
                leerColisiones(
                        capa,
                        tipo,
                        anchoMapa,
                        anchoTile,
                        altoTile
                );
                continue;
            }

            if (!tipo.equals("tilelayer")) {
                continue;
            }

            if (capa.has("visible") &&
                    !capa.get("visible").getAsBoolean()) {
                continue;
            }

            JsonArray datos = capa.getAsJsonArray("data");

            int anchoCapa = capa.get("width").getAsInt();
            int altoCapa = capa.get("height").getAsInt();

            dibujarCapaTiles(
                    datos,
                    anchoCapa,
                    altoCapa,
                    anchoTile,
                    altoTile,
                    firstGid,
                    gc
            );
        }
    }

    protected void leerColisiones(
            JsonObject capa,
            String tipo,
            int anchoMapa,
            int anchoTile,
            int altoTile
    ) {

        double tamX = anchoTile * ESCALA;
        double tamY = altoTile * ESCALA;

        if (tipo.equals("tilelayer")) {

            JsonArray datos = capa.getAsJsonArray("data");

            for (int i = 0; i < datos.size(); i++) {

                if (datos.get(i).getAsLong() != 0) {

                    colisiones.add(new Rectangle(
                            (i % anchoMapa) * tamX,
                            (i / anchoMapa) * tamY,
                            tamX,
                            tamY
                    ));
                }
            }

        } else if (tipo.equals("objectgroup")) {

            JsonArray objetos = capa.getAsJsonArray("objects");

            for (JsonElement elemento : objetos) {

                JsonObject objeto = elemento.getAsJsonObject();

                double x = objeto.get("x").getAsDouble() * ESCALA;
                double y = objeto.get("y").getAsDouble() * ESCALA;

                if (objeto.has("polygon")) {

                    JsonArray puntos = objeto.getAsJsonArray("polygon");

                    Polygon poligono = new Polygon();

                    for (JsonElement puntoElemento : puntos) {

                        JsonObject punto = puntoElemento.getAsJsonObject();

                        double px = x +
                                punto.get("x").getAsDouble() * ESCALA;

                        double py = y +
                                punto.get("y").getAsDouble() * ESCALA;

                        poligono.getPoints().addAll(px, py);
                    }

                    colisiones.add(poligono);

                } else {

                    double ancho =
                            objeto.get("width").getAsDouble() * ESCALA;

                    double alto =
                            objeto.get("height").getAsDouble() * ESCALA;

                    colisiones.add(new Rectangle(x, y, ancho, alto));
                }
            }
        }
    }

    public List<Shape> getColisiones() {
        return colisiones;
    }

    public List<TransicionMapa> obtenerTransiciones() {
        return List.of();
    }
}
