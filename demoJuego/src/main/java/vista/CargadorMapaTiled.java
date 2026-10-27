
package vista;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.geometry.Rectangle2D;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CargadorMapaTiled {

    private static final String RUTA_MAPA = "/images/mapa/samplemap1.tmj";
    private static final String RUTA_TILESET = "/images/mapa/punyworld-overworld-tileset.png";
    private static final int TAM_TILE = 16;
    private static final int COLUMNAS_TILESET = 27;
    private static final int ESCALA = 3;
    private final List<Rectangle2D> colisiones = new ArrayList<>();

    public List<Rectangle2D> getColisiones() {
        return colisiones;
    }

    private void leerColisiones(JsonObject capa, String tipo, int anchoMapa)
    {
        double t = TAM_TILE * ESCALA;

        if(tipo.equals("tilelayer"))
        {
            JsonArray datos = capa.getAsJsonArray("data");
            for(int i = 0; i < datos.size(); i++)
            {
                if (datos.get(i).getAsLong() != 0)
                {
                    colisiones.add(new Rectangle2D((i % anchoMapa) * t, (i / anchoMapa) * t, t, t));
                }
            }
        } else
        {
            for (JsonElement element: capa.getAsJsonArray("objects"))
            {
                JsonObject o = element.getAsJsonObject();
                colisiones.add(new Rectangle2D(o.get("x").getAsDouble() * ESCALA,
                        o.get("y").getAsDouble() * ESCALA,
                        o.get("width").getAsDouble() * ESCALA,
                        o.get("height").getAsDouble() * ESCALA));

            }

        }

    }

    public Canvas cargarMapa() throws Exception {

        JsonObject mapa = leerJson(RUTA_MAPA);

        int ancho = mapa.get("width").getAsInt();
        int alto = mapa.get("height").getAsInt();

        Canvas canvas = new Canvas(
                ancho * TAM_TILE * ESCALA,
                alto * TAM_TILE * ESCALA
        );

        double w = canvas.getWidth();
        double h = canvas.getHeight();
        colisiones.add(new Rectangle2D(-10,0,10,h));
        colisiones.add(new Rectangle2D(w, 0, 10, h));
        colisiones.add(new Rectangle2D(0, -10, w, 10));
        colisiones.add(new Rectangle2D(0, h, w, 10));

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Evita el suavizado que puede generar bordes borrosos.
        gc.setImageSmoothing(false);

        Image tileset;

        try (InputStream imagen = getClass()
                .getResourceAsStream(RUTA_TILESET)) {

            if (imagen == null) {
                throw new IllegalStateException(
                        "No se encontró el tileset: " + RUTA_TILESET
                );
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

        // Dibujamos las capas en el orden de Tiled.
        for (int i = 0; i < capas.size(); i++) {
            JsonObject capa = capas.get(i).getAsJsonObject();
            String tipo = capa.get("type").getAsString();

            if (capa.get("name").getAsString().equals("colisiones")) {
                leerColisiones(capa, tipo, ancho);
                continue;
            }

            if (!capa.get("type").getAsString().equals("tilelayer")) {
                continue;
            }

            if (capa.has("visible") && !capa.get("visible").getAsBoolean()) {
                continue;
            }

            JsonArray datos = capa.getAsJsonArray("data");

            int anchoCapa = capa.get("width").getAsInt();
            int altoCapa = capa.get("height").getAsInt();

            for (int y = 0; y < altoCapa; y++) {
                for (int x = 0; x < anchoCapa; x++) {
                    int gid = datos.get(y * anchoCapa + x).getAsInt();

                    // GID 0 significa que no hay tile.
                    if (gid == 0) {
                        continue;
                    }

                    // El tileset comienza en firstgid = 1.
                    int tileId = gid - 1;

                    // Coordenadas del tile dentro del PNG.
                    int origenX = (tileId % COLUMNAS_TILESET) * TAM_TILE;

                    int origenY = (tileId / COLUMNAS_TILESET) * TAM_TILE;

                    // Coordenadas en el mapa ampliado.
                    double destinoX = x * TAM_TILE * ESCALA;
                    double destinoY = y * TAM_TILE * ESCALA;
                    double tamDestino = TAM_TILE * ESCALA;

                    // Solapamiento mínimo para disimular huecos.
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
            throw new IllegalStateException("No se encontró el archivo: " + ruta);
        }

        try (InputStreamReader lector = new InputStreamReader(
                archivo,
                StandardCharsets.UTF_8
        )) {
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
