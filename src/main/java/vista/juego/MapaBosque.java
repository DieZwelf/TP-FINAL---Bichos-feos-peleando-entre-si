
package vista.juego;

public class MapaBosque extends CargadorMapaTiled {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/bosque.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/punyworld-overworld-tileset.png";
    }
}
