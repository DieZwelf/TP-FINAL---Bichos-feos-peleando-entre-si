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
    private int nivel;
    private int duracion;
    private double curar;

    public Pocion(String nombre, String descripcion, int nivel, int duracion, double curar) {
        super(nombre, descripcion);
        this.nivel = nivel;
        this.duracion = duracion;
        this.curar = curar;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    /**
     * Usar la poción, verifica que la vida no sea menor o igual a 0, lo cura y si ésta cura es menor
     * o igual a 0 entonces no pierde el turno y añade que ya está al máximo.
     * Si no pasa lo anterior, entonces si se añade el registro correctamente.
     * @param entrenador El que dirige la acción.
     * @param objetivo El que recibe la poción.
     * @param registro Lo que se registra.
     * @return verdadero o falso.
     */
    @Override
    public boolean usar(Entrenador entrenador, Pokemon objetivo, List<String> registro) {
        if (objetivo == null || objetivo.getVida() <= 0) {
            registro.add("El objetivo está debilitado o no existe.");
            return false;
        }

        double curado = objetivo.curar(curar);

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
