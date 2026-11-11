
package vista.juego;

import java.util.List;

public class MapaCasa extends CargadorMapaTiled {

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
                        480, 240,
                        48, 48,
                        MapaCasa::new,
                        480, 600
                )
        );
    }
}
