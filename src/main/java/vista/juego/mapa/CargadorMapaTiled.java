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

package vista.juego.mapa;

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

    protected static final int TAM_TILE = 16; // cada tile es de 16p
    protected static final int ESCALA = 3; // Representa el factor de ampliación

    private final List<Shape> colisiones = new ArrayList<>(); // lista que almacena las formas geométricas que representan los obstáculos del mapa
        /*
        ¿Por qué utilizamos Shape?

        Porque las colisiones pueden tener diferentes formas:
        - Rectangle: colisiones rectangulares.
        - Polygon: colisiones poligonales.
        Ambas son formas de JavaFX y pueden almacenarse en una misma lista.
         */

    protected abstract String obtenerRutaMapa();

    protected abstract String obtenerRutaTileset();

    // Cada cargador prepara sus recursos de manera diferente.
    protected abstract void prepararTileset() throws Exception;

    // Cada cargador dibuja sus tiles de manera diferente.
    protected abstract void dibujarCapaTiles(
            JsonArray datos, // Los identificadores de los tiles que forman la capa
            int anchoCapa, // Cantidad de tiles que tiene la capa horizontalmente.
            int altoCapa, // Cantidad de tiles que tiene la capa verticalmente.
            int anchoTile, // 	Ancho de cada tile en píxeles.
            int altoTile, // 	Alto de cada tile en píxeles.
            int firstGid, // Identificador global inicial de los tiles de ese tileset
            GraphicsContext gc // Herramienta que permite dibujar sobre el Canvas
    );

    public final Canvas cargarMapa() throws Exception {

        colisiones.clear();

        JsonObject mapa = leerJson(obtenerRutaMapa()); // obtiene la ruta del archivo JSON, leerJson() abre ese archivo y devuelve su contenido como un JsonObject

        int ancho = mapa.get("width").getAsInt();
        int alto = mapa.get("height").getAsInt();

        int anchoTile = mapa.get("tilewidth").getAsInt();
        int altoTile = mapa.get("tileheight").getAsInt();

        double anchoCanvas = ancho * anchoTile * ESCALA;
        double altoCanvas = alto * altoTile * ESCALA; // Acomoda la escala

        Canvas canvas = new Canvas(anchoCanvas, altoCanvas); // Espacio donde se va a dibujar el mapa

        GraphicsContext gc = canvas.getGraphicsContext2D(); // Obtiene el objeto que nos permite dibujar sobre ese Canvas
        gc.setImageSmoothing(false);

        agregarLimites(anchoCanvas, altoCanvas); //Estos funcionan como obstáculos para evitar que el jugador salga de los límites del escenario

        JsonArray tilesets = mapa.getAsJsonArray("tilesets"); //Obtiene del JSON el arreglo de tilesets asociados al mapa

        if (tilesets == null || tilesets.isEmpty()) { // Comprobar que existan tilesets
            throw new IllegalStateException(
                    "El mapa no tiene ningún tileset asociado."
            );
        }

        int firstGid = tilesets.get(0) // Obtiene el identificador global inicial del primer tileset
                .getAsJsonObject()
                .get("firstgid")
                .getAsInt();

        prepararTileset(); // Llama a un método abstracto que deben implementar las clases hijas

        dibujarCapas(
                mapa,
                gc,
                ancho,
                anchoTile,
                altoTile,
                firstGid
        );
        /*
        Dentro de dibujarCapas() ocurre lo siguiente:
        1. Obtiene las capas del JSON.
        2. Recorre cada capa.
        3. Identifica si es una capa de colisiones o una capa de tiles.
        4. Si contiene tiles, llama a dibujarCapaTiles().
        5. La clase hija utiliza el GraphicsContext para dibujar las imágenes en las posiciones correspondientes
         */

        return canvas;
    }

    /**
     * Este método se encarga de leer los archivos JSON de los mapas.
     * Primero busca el recurso mediante getResourceAsStream y comprueba que exista.
     * Después utiliza un InputStreamReader con codificación UTF-8 para leer su contenido como texto.
     * Finalmente, mediante Gson, interpreta ese contenido y lo convierte en un JsonObject
     * que podemos utilizar para acceder a las dimensiones, capas y tilesets del mapa.
     * @param ruta
     * @return
     * @throws Exception
     */
    protected JsonObject leerJson(String ruta) throws Exception {

        InputStream archivo = getClass().getResourceAsStream(ruta); // busca el archivo dentro de los recursos del proyecto y obtiene un flujo de entrada para poder leerlo

        if (archivo == null) {
            throw new IllegalStateException(
                    "No se encontró el archivo: " + ruta
            );
        }

        try (InputStreamReader lector = // Crear el lector
                     new InputStreamReader(
                             archivo,
                             StandardCharsets.UTF_8
                     )) {
            /*
            ¿Por qué usamos UTF-8?

             Porque es una codificación de caracteres que permite interpretar correctamente letras, símbolos y caracteres especiales
             */

            return JsonParser.parseReader(lector).getAsJsonObject(); // JsonParser.parseReader(lector) lee el contenido del archivo a través del lector y lo interpreta como una estructura JSON
                                                                     // .getAsJsonObject() Obtiene esa estructura como un objeto JSON
        }
    }

    /**
     * Agrega cuatro rectángulos de colisión alrededor del mapa
     * para impedir que el jugador pueda salir de sus límites.
     *
     * @param ancho ancho del mapa en píxeles
     * @param alto alto del mapa en píxeles
     */
    protected void agregarLimites(double ancho, double alto) {

        colisiones.add(new Rectangle(-10, 0, 10, alto)); // Cada línea crea un rectángulo y lo agrega a la lista de colisiones
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

        JsonArray capas = mapa.getAsJsonArray("layers"); // Dentro del JSON del mapa existe una propiedad llamada "layers"

        for (JsonElement elemento : capas) { // El for recorre todas las capas una por una

            JsonObject capa = elemento.getAsJsonObject();

            String nombre = capa.get("name").getAsString(); // Obtener el nombre y el tipo de capa
            String tipo = capa.get("type").getAsString();

            if (nombre.equals("colisiones")) { // pregunta si el nombre de la capa es "colisiones"
                leerColisiones(
                        capa,
                        tipo,
                        anchoMapa,
                        anchoTile,
                        altoTile
                );
                continue; // Hace que el for termine esa vuelta y pase directamente a la siguiente capa
            }

            if (!tipo.equals("tilelayer")) { // Si el tipo de capa NO es tilelayer, pasar a la siguiente
                continue;
            }

            if (capa.has("visible") && // Si una capa está oculta, no queremos dibujarla
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

    /**
     * Lee la capa de colisiones del mapa y genera las figuras geométricas
     * correspondientes para detectar obstáculos.
     *
     * Si la capa es de tipo tilelayer, crea rectángulos en las posiciones
     * donde existen tiles marcados como colisión.
     *
     * Si es de tipo objectgroup, crea rectángulos o polígonos según la
     * forma de los objetos definidos en Tiled.
     *
     * Todas las figuras se almacenan en la lista colisiones para su
     * posterior utilización en la detección de choques del jugador.
     *
     * @param capa capa de colisiones obtenida del JSON
     * @param tipo tipo de capa (tilelayer u objectgroup)
     * @param anchoMapa cantidad de tiles de ancho del mapa
     * @param anchoTile ancho de cada tile en píxeles
     * @param altoTile alto de cada tile en píxeles
     */
    protected void leerColisiones(
            JsonObject capa,
            String tipo,
            int anchoMapa,
            int anchoTile,
            int altoTile
    ) {

        double tamX = anchoTile * ESCALA;
        double tamY = altoTile * ESCALA;

        if (tipo.equals("tilelayer")) { // Primera posibilidad: colisiones mediante tiles

            JsonArray datos = capa.getAsJsonArray("data");

            for (int i = 0; i < datos.size(); i++) {

                if (datos.get(i).getAsLong() != 0) {

                    colisiones.add(new Rectangle( // Crear el rectángulo de colisión
                            (i % anchoMapa) * tamX,
                            (i / anchoMapa) * tamY,
                            tamX,
                            tamY
                    ));
                }
            }

        } else if (tipo.equals("objectgroup")) { // Segunda posibilidad: colisiones mediante objetos

            JsonArray objetos = capa.getAsJsonArray("objects"); // Obtenemos el array "objects" del JSON, que contiene todos los objetos que dibujamos en esa capa

            for (JsonElement elemento : objetos) {

                JsonObject objeto = elemento.getAsJsonObject(); // En cada vuelta, objeto representa un objeto diferente.

                double x = objeto.get("x").getAsDouble() * ESCALA; // Obtenemos las coordenadas X e Y del objeto y las multiplicamos por ESCALA
                double y = objeto.get("y").getAsDouble() * ESCALA;

                if (objeto.has("polygon")) {

                    JsonArray puntos = objeto.getAsJsonArray("polygon"); // primero obtenemos todos los puntos que forman el polígono

                    Polygon poligono = new Polygon(); // Después creamos un objeto Polygon, que representa una figura geométrica formada por varios puntos

                    for (JsonElement puntoElemento : puntos) { // Recorremos cada punto del polígono y lo convertimos en un JsonObject

                        JsonObject punto = puntoElemento.getAsJsonObject();

                        double px = x +
                                punto.get("x").getAsDouble() * ESCALA;

                        double py = y +
                                punto.get("y").getAsDouble() * ESCALA; // Calculamos la posición de cada punto.

                        poligono.getPoints().addAll(px, py);
                    }

                    colisiones.add(poligono); // Agregamos las coordenadas calculadas a la lista de puntos del polígono

                } else { // Si el objeto no tiene la propiedad "polygon", el código lo trata como un rectángulo

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

    /**
     * Define las transiciones disponibles desde el mapa de la casa.
     *
     * Devuelve una lista con los objetos TransicionMapa, indicando para cada
     * uno la zona que activa el cambio, el mapa de destino y las coordenadas
     * donde aparecerá el jugador al ingresar al nuevo mapa.
     *
     * @return lista de transiciones disponibles en el mapa
     */
    public List<TransicionMapa> obtenerTransiciones() {
        return List.of();
    }
}
