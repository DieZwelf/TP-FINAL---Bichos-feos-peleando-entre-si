package model;

import java.util.List;

/**
 * La clase POCIÓN, hija de ITEM tiene como atributos el nivel de la poción, la duración y cuánto cura
 * Tiene un constructor con todos los atributos propios y los que hereda de Item.
 * Métodos: getters y setters.
 * Otros métodos: usar(): implementación del heredado de ITEM.
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Pocion extends Item{

    private double curacion;

    public Pocion(double curacion) {
        super("Pocion", "Cura a tu bicho");
        this.curacion = curacion;
    }


    public double getCuracion() {
        return curacion;
    }

    public void setCuracion(double curacion) {
        this.curacion = curacion;
    }

    /**
     * Usar la poción, verifica que la vida no sea menor o igual a 0, lo cura y si ésta cura es menor
     * o igual a 0 entonces no pierde el turno y añade que ya está al máximo.
     * Si no pasa lo anterior, entonces si se añade el registro correctamente.
     * @param jugador El que dirige la acción.
     * @param objetivo El que recibe la poción.
     * @param registro Lo que se registra.
     * @return verdadero o falso.
     */
    @Override
    public boolean usar(Jugador jugador, Pokemon objetivo, List<String> registro) {
        if (objetivo == null || objetivo.getVida() <= 0) {
            registro.add("El objetivo está debilitado o no existe.");
            return false;
        }

        double curado = objetivo.curar(curacion);

        if (curado <= 0)
        {
            registro.add(objetivo.getNombre() + " ya tiene la vida al maximo");
            return false;
        }

        registro.add("Usaste " + getNombre() + ": " + objetivo.getNombre() + " recupero " +
                        Math.round(curado) + " puntos de vida");
        return true;
    }

}
