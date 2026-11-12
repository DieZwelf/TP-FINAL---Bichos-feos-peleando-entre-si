
package vista.juego;

import java.util.List;

public class MapaPueblo extends CargadorMapaPlanilla {

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
                        767, 2400,
                        16, 16,
                        MapaCasa::new,
                        47, 70
                )
        );
    }
}
