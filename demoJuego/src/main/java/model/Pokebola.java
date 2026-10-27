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
    private final String rutaImagen;

    public Pokebola(String nombre, String descripcion, boolean ocupada) {
        super(nombre, descripcion);
        this.rutaImagen = "/images/items/" + nombre.toLowerCase() + ".png";
        this.ocupada = ocupada;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    /**
     * En desarrollo...
     * @param entrenador
     * @param objetivo
     * @param registro
     * @return
     */
    @Override
    public boolean usar(Entrenador entrenador, Pokemon objetivo, List<String> registro) {
        return false;
    }
}
