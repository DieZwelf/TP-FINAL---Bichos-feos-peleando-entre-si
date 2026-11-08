
package vista.juego;

public class MapaPueblo extends CargadorMapaTiled {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/samplemap1.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/punyworld-overworld-tileset.png";
    }
}
