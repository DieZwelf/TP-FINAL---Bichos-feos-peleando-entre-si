package model;

import java.util.List;

/**
 * La clase POKEBOLA, hija de ITEM tiene como atributos si está ocupada y la ruta de su imagen.
 * Tiene un constructor con todos los atributos propios y los que hereda de Item.
 * Métodos: getters y setters.
 * Otros métodos: usar(): Implementación del metodo heredado de ITEM.
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Pokebola extends Item{
    private boolean ocupada;

    public Pokebola(String nombre, String descripcion, boolean ocupada) {
        super(nombre, descripcion);
        this.ocupada = ocupada;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    /**
     * En desarrollo...
     * @param entrenador
     * @param objetivo
     * @param registro
     * @return
     */
    @Override
    public boolean usar(Jugador jugador, Pokemon objetivo, List<String> registro) {
        if (ocupada)
        {
            registro.add("Esa Pokebola ya está ocupada!");
            return false;
        }

        if (objetivo == null || jugador == null)
        {
            registro.add("No se puede realizar la captura");
        }

        if (jugador.getEquipo().size() >= 5)
        {
            registro.add("Tu equipo esta lleno");
            return false;
        }

        double probabilidad = CalculadoraCaptura.calcularProbabilidad(objetivo);

        if (Math.random() < probabilidad)
        {
            ocupada = true;
            jugador.aniadir(objetivo);

            registro.add("¡Se ha capturado con exito a: " + objetivo.getNombre() + "!");

        } else
        {
            registro.add(objetivo.getNombre() + " escapo de la pokebola!");

        }
            return true;
    }
}
