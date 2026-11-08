
package vista.juego;

public class MapaBosque extends CargadorMapaTiled {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/samplemap2.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/punyworld-overworld-tileset.png";
    }
}
