package vista.juego.mapa;

import javafx.scene.shape.Rectangle;

import java.util.function.Supplier;

public class TransicionMapa {

    private final Rectangle zona;
    private final Supplier<CargadorMapaTiled> mapaDestino;
    private final double spawnX;
    private final double spawnY;

    public TransicionMapa(
            double x, double y,
            double ancho, double alto,
            Supplier<CargadorMapaTiled> mapaDestino,
            double spawnX, double spawnY) {

        this.zona = new Rectangle(x, y, ancho, alto);
        this.mapaDestino = mapaDestino;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public boolean jugadorEntro(double x, double y) {

        Rectangle jugador = new Rectangle(
                x + 12,
                y + 32,
                24,
                16
        );

        return zona.getBoundsInParent()
                .intersects(jugador.getBoundsInParent());
    }

    public CargadorMapaTiled getMapaDestino() {
        return mapaDestino.get();
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }
}