
package vista.juego;

import com.google.gson.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class CargadorMapaTiled {

    protected static final int TAM_TILE = 16;

    protected static final int COLUMNAS_TILESET = 27;

    protected static final int ESCALA = 3;

    private final List<Shape> colisiones = new ArrayList<>();

    // Cada mapa proporciona sus propios recursos.
    protected abstract String obtenerRutaMapa();

    protected abstract String obtenerRutaTileset();

    public final Canvas cargarMapa() throws Exception {

        colisiones.clear();

        JsonObject mapa = leerJson(obtenerRutaMapa());

        int ancho = mapa.get("width").getAsInt();
        int alto = mapa.get("height").getAsInt();

        double anchoCanvas = ancho * TAM_TILE * ESCALA;
        double altoCanvas = alto * TAM_TILE * ESCALA;

        Canvas canvas = new Canvas(anchoCanvas, altoCanvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        agregarLimites(anchoCanvas, altoCanvas);

        Image tileset = cargarTileset();

        dibujarCapas(mapa, gc, tileset, ancho);

        return canvas;
    }

    // Lee el archivo JSON generado por Tiled.
    protected JsonObject leerJson(String ruta) throws Exception {

        InputStream archivo = getClass().getResourceAsStream(ruta);

        if (archivo == null) {
            throw new IllegalStateException(
                    "No se encontró el archivo: " + ruta
            );
        }

        try (InputStreamReader lector =
                     new InputStreamReader(archivo, StandardCharsets.UTF_8)) {

            return JsonParser.parseReader(lector).getAsJsonObject();
        }
    }

    // Carga la imagen del tileset.
    protected Image cargarTileset() throws Exception {

        String ruta = obtenerRutaTileset();

        try (InputStream imagen = getClass().getResourceAsStream(ruta)) {

            if (imagen == null) {
                throw new IllegalStateException(
                        "No se encontró el tileset: " + ruta
                );
            }

            Image tileset = new Image(imagen);

            if (tileset.isError()) {
                throw new IllegalStateException(
                        "No se pudo cargar el tileset.",
                        tileset.getException()
                );
            }

            return tileset;
        }
    }

    // Agrega las colisiones de los bordes del mapa.
    protected void agregarLimites(double ancho, double alto) {

        colisiones.add(new Rectangle(-10, 0, 10, alto));
        colisiones.add(new Rectangle(ancho, 0, 10, alto));
        colisiones.add(new Rectangle(0, -10, ancho, 10));
        colisiones.add(new Rectangle(0, alto, ancho, 10));
    }

    // Recorre todas las capas del archivo Tiled.
    protected void dibujarCapas(JsonObject mapa, GraphicsContext gc, Image tileset, int anchoMapa) {

        JsonArray capas = mapa.getAsJsonArray("layers");

        for (JsonElement elemento : capas) {

            JsonObject capa = elemento.getAsJsonObject();

            String nombre = capa.get("name").getAsString();
            String tipo = capa.get("type").getAsString();

            if (nombre.equals("colisiones")) {
                leerColisiones(capa, tipo, anchoMapa);
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

            for (int y = 0; y < altoCapa; y++) {

                for (int x = 0; x < anchoCapa; x++) {

                    int gid = datos.get(
                            y * anchoCapa + x
                    ).getAsInt();

                    if (gid == 0) {
                        continue;
                    }

                    int tileId = gid - 1;

                    int origenX =
                            (tileId % COLUMNAS_TILESET) * TAM_TILE;

                    int origenY =
                            (tileId / COLUMNAS_TILESET) * TAM_TILE;

                    double destinoX = x * TAM_TILE * ESCALA;
                    double destinoY = y * TAM_TILE * ESCALA;

                    double tamDestino = TAM_TILE * ESCALA;

                    gc.drawImage(
                            tileset,
                            origenX,
                            origenY,
                            TAM_TILE,
                            TAM_TILE,
                            destinoX,
                            destinoY,
                            tamDestino + 0.5,
                            tamDestino + 0.5
                    );
                }
            }
        }
    }

    // Obtiene las colisiones de las capas de Tiled.
    protected void leerColisiones(
            JsonObject capa,
            String tipo,
            int anchoMapa
    ) {

        double tam = TAM_TILE * ESCALA;

        if (tipo.equals("tilelayer")) {

            JsonArray datos = capa.getAsJsonArray("data");

            for (int i = 0; i < datos.size(); i++) {

                if (datos.get(i).getAsLong() != 0) {

                    colisiones.add(new Rectangle(
                            (i % anchoMapa) * tam,
                            (i / anchoMapa) * tam,
                            tam,
                            tam
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
