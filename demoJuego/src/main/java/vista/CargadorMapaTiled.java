
package vista;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CargadorMapaTiled {

    private static final String RUTA_MAPA =
            "/images/mapa/samplemap1.tmj";

    private static final String RUTA_TILESET =
            "/images/mapa/punyworld-overworld-tileset.png";

    private static final int TAM_TILE = 16;
    private static final int COLUMNAS_TILESET = 27;
    private static final int ESCALA = 3;

    private final List<Shape> colisiones = new ArrayList<>();

    public List<Shape> getColisiones() {
        return colisiones;
    }

    private void leerColisiones(
            JsonObject capa,
            String tipo,
            int anchoMapa) {

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

                    JsonArray puntos =
                            objeto.getAsJsonArray("polygon");

                    Polygon poligono = new Polygon();

                    for (JsonElement puntoElemento : puntos) {

                        JsonObject punto =
                                puntoElemento.getAsJsonObject();

                        double px = x
                                + punto.get("x").getAsDouble() * ESCALA;

                        double py = y
                                + punto.get("y").getAsDouble() * ESCALA;

                        poligono.getPoints().addAll(px, py);
                    }

                    colisiones.add(poligono);

                } else {

                    double ancho =
                            objeto.get("width").getAsDouble() * ESCALA;

                    double alto =
                            objeto.get("height").getAsDouble() * ESCALA;

                    colisiones.add(new Rectangle(
                            x, y, ancho, alto
                    ));
                }
            }
        }
    }

    public Canvas cargarMapa() throws Exception {

        colisiones.clear();

        JsonObject mapa = leerJson(RUTA_MAPA);

        int ancho = mapa.get("width").getAsInt();
        int alto = mapa.get("height").getAsInt();

        double anchoCanvas = ancho * TAM_TILE * ESCALA;
        double altoCanvas = alto * TAM_TILE * ESCALA;

        Canvas canvas = new Canvas(anchoCanvas, altoCanvas);

        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // Límites del mapa.
        colisiones.add(new Rectangle(-10, 0, 10, h));
        colisiones.add(new Rectangle(w, 0, 10, h));
        colisiones.add(new Rectangle(0, -10, w, 10));
        colisiones.add(new Rectangle(0, h, w, 10));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        Image tileset;

        try (InputStream imagen =
                     getClass().getResourceAsStream(RUTA_TILESET)) {

            if (imagen == null) {
                throw new IllegalStateException(
                        "No se encontró el tileset: " + RUTA_TILESET);
            }

            tileset = new Image(imagen);

            if (tileset.isError()) {
                throw new IllegalStateException(
                        "No se pudo cargar el tileset.",
                        tileset.getException()
                );
            }
        }

        JsonArray capas = mapa.getAsJsonArray("layers");

        // Todas las capas visuales vuelven a dibujarse en el mismo canvas.
        for (JsonElement elemento : capas) {

            JsonObject capa = elemento.getAsJsonObject();

            String nombre = capa.get("name").getAsString();
            String tipo = capa.get("type").getAsString();

            if (nombre.equals("colisiones")) {
                leerColisiones(capa, tipo, ancho);
                continue;
            }

            if (!tipo.equals("tilelayer")) {
                continue;
            }

            if (capa.has("visible")
                    && !capa.get("visible").getAsBoolean()) {
                continue;
            }

            JsonArray datos = capa.getAsJsonArray("data");

            int anchoCapa = capa.get("width").getAsInt();
            int altoCapa = capa.get("height").getAsInt();

            for (int y = 0; y < altoCapa; y++) {
                for (int x = 0; x < anchoCapa; x++) {

                    int gid = datos.get(
                            y * anchoCapa + x).getAsInt();

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

        return canvas;
    }

    private JsonObject leerJson(String ruta) throws Exception {

        InputStream archivo = getClass().getResourceAsStream(ruta);

        if (archivo == null) {
            throw new IllegalStateException(
                    "No se encontró el archivo: " + ruta);
        }

        try (InputStreamReader lector = new InputStreamReader(
                archivo, StandardCharsets.UTF_8)) {

            return JsonParser.parseReader(lector).getAsJsonObject();
        }
    }

    public void mostrarPrueba() {

        try {
            Canvas canvas = cargarMapa();

            Group grupo = new Group(canvas);
            Scene escena = new Scene(grupo);

            Stage ventana = new Stage();
            ventana.setTitle("Prueba del mapa de Tiled");
            ventana.setScene(escena);
            ventana.setMaximized(true);
            ventana.show();

        } catch (Exception e) {
            System.err.println("Error al cargar el mapa:");
            e.printStackTrace();
        }
    }
}
