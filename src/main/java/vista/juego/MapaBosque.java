
package vista.juego;

import java.util.List;

public class MapaBosque extends CargadorMapaTiled {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/samplemap2.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/punyworld-overworld-tileset.png";
    }

    @Override
    public List<TransicionMapa> obtenerTransiciones() {
        return List.of(
                new TransicionMapa(
                        480, 240,
                        48, 48,
                        MapaBosque::new,
                        480, 600
                )
        );
    }
}
