
package model;

import java.util.List;
import java.util.Objects;

/**
 * Representa una Pokebola del inventario.
 * Permite capturar Pokémon y registra si está ocupada.
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Pokebola extends Item {

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
     * Intenta utilizar la Pokebola para capturar un Pokémon.
     */
    @Override
    public boolean usar(Jugador jugador, Pokemon objetivo, List<String> registro) {

        if (ocupada) {
            registro.add("¡Esa Pokebola ya está ocupada!");
            return false;
        }

        if (objetivo == null || jugador == null) {
            registro.add("No se puede realizar la captura");
            return false;
        }

        if (jugador.getEquipo().size() >= 5) {
            registro.add("Tu equipo está lleno");
            return false;
        }

        double probabilidad = CalculadoraCaptura.calcularProbabilidad(objetivo);

        if (Math.random() < probabilidad) {
            ocupada = true;
            jugador.aniadir(objetivo);

            registro.add(
                    "¡Se ha capturado con éxito a: "
                            + objetivo.getNombre() + "!"
            );
        } else {
            registro.add(objetivo.getNombre() + " escapó de la Pokebola!");
        }

        return true;
    }

    /**
     * Compara dos Pokebolas según sus datos.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Pokebola otra)) return false;

        return ocupada == otra.ocupada
                && Objects.equals(getNombre(), otra.getNombre())
                && Objects.equals(getDescripcion(), otra.getDescripcion());
    }

    /**
     * Genera el código hash según los mismos atributos usados en equals.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getNombre(), getDescripcion(), ocupada);
    }
}
