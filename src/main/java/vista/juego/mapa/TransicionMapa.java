package vista.juego.mapa;

import javafx.scene.shape.Rectangle;

import java.util.function.Supplier;

public class TransicionMapa {

    private final Rectangle zona; // es un rectángulo que representa el área del mapa que activa la transición. Si el jugador entra en ese rectángulo, se produce el cambio de mapa
    private final Supplier<CargadorMapaTiled> mapaDestino; //indica qué mapa se debe cargar cuando ocurre la transición
    /*
    Significa que mapaDestino almacena una función que, al ejecutarse, devuelve un CargadorMapaTiled.
    ¿Por qué hacerlo así? Porque permite definir el mapa de destino sin tener que cargarlo inmediatamente. Se obtiene cuando realmente ocurre la transición
     */
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

        Rectangle jugador = new Rectangle( // Recibe las coordenadas del jugador y crea un rectángulo de 24 píxeles de ancho y 16 de alto
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