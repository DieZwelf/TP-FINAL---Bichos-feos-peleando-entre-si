
package model;

/**
 * Es un contenedor, no tiene lógica como tal
 */
public class DatosEspecie {

    private final String nombre;
    private final double vida;
    private final double danio;
    private final double defensa;
    private final double velocidad;
    private final TipoPokemon tipo;

    public DatosEspecie(String nombre, double vida, double danio, double defensa, double velocidad, TipoPokemon tipo) {
        this.nombre = nombre;
        this.vida = vida;
        this.danio = danio;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getVida() {
        return vida;
    }

    public double getDanio() {
        return danio;
    }

    public double getDefensa() {
        return defensa;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public TipoPokemon getTipo() {
        return tipo;
    }
}
