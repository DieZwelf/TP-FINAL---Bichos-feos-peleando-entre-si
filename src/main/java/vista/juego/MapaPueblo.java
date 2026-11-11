
package vista.juego;

import java.util.List;

public class MapaPueblo extends CargadorMapaTiled {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/samplemap1.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/punyworld-overworld-tileset.png";
    }


    @Override
    public List<TransicionMapa> obtenerTransiciones() {
        return List.of(
                new TransicionMapa(
                        767, 2352,
                        48, 48,
                        MapaCasa::new,
                        480, 600
                )
        );
    }
}
