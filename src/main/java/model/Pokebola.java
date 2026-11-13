
package model;

import java.util.List;
import java.util.Objects;

/**
 * Representa una Pokebola del inventario.
 * Permite intentar capturar Pokémon.
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Pokebola extends Item {

    private boolean capturaExitosa;

    public Pokebola(String nombre, String descripcion, boolean ocupada) {
        super(nombre, descripcion);
        this.capturaExitosa = false;
    }

    /**
     * Las Pokebolas nunca se consideran ocupadas.
     */
    public boolean isOcupada() {
        return false;
    }

    /**
     * Se conserva por compatibilidad con otras clases.
     */
    public void setOcupada(boolean ocupada) {
        // No modifica el estado.
    }

    /**
     * Indica si la última utilización consiguió capturar
     * al Pokémon objetivo.
     */
    public boolean isCapturaExitosa() {
        return capturaExitosa;
    }

    /**
     * Intenta capturar un Pokémon.
     */
    @Override
    public boolean usar(
            Jugador jugador,
            Pokemon objetivo,
            List<String> registro
    ) {

        capturaExitosa = false;

        if (jugador == null || objetivo == null) {
            registro.add("No se puede realizar la captura.");
            return false;
        }

        if (jugador.getEquipo().size() >= 5) {
            registro.add("Tu equipo está lleno.");
            return false;
        }

        double probabilidad =
                CalculadoraCaptura.calcularProbabilidad(objetivo);

        if (Math.random() < probabilidad) {

            jugador.aniadir(objetivo);
            capturaExitosa = true;

            registro.add(
                    "¡Se ha capturado con éxito a: "
                            + objetivo.getNombre() + "!"
            );

        } else {

            registro.add(
                    objetivo.getNombre() + " escapó de la Pokebola!"
            );
        }

        return true;
    }

    /**
     * Compara Pokebolas por su nombre.
     */
    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Pokebola otra)) return false;

        return Objects.equals(getNombre(), otra.getNombre());
    }

    /**
     * Genera un hash estable para el inventario.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getNombre());
    }
}
