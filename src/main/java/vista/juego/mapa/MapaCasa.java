
package vista.juego.mapa;

import java.util.List;

public class MapaCasa extends CargadorMapaIndividual {

    @Override
    protected String obtenerRutaMapa() {
        return "/images/mapa/casa.tmj";
    }

    @Override
    protected String obtenerRutaTileset() {
        return "/images/mapa/TiledCasa.tsj";
    }

    @Override
    public List<TransicionMapa> obtenerTransiciones() {
        return List.of(
                new TransicionMapa(
                        47, 48,
                        48, 48,
                        MapaPueblo::new,
                        767, 2352
                )
        );
    }
}
