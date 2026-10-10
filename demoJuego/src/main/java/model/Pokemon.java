package model;

public class Pokemon {
    private static int contador = 0;
    private int id;
    private String nombre;
    private double vida;
    private double danio;
    private double defensa;
    private double velocidad;
    private TipoPokemon tipo;

    public Pokemon() {
        id = contador++;
    }

    public Pokemon(String nombre, double vida, double danio,double defensa, double velocidad, TipoPokemon tipo) {
        id = contador++;
        this.nombre = nombre;
        this.vida = vida;
        this.danio = danio;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getDanio() {
        return danio;
    }

    public void setDanio(double danio) {
        this.danio = danio;
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public double getDefensa() {
        return defensa;
    }

    public void setDefensa(double defensa) {
        this.defensa = defensa;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public TipoPokemon getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "nombre='" + nombre + '\'' +
                ", vida=" + vida +
                ", danio=" + danio +
                ", defensa=" + defensa +
                ", velocidad=" + velocidad +
                ", tipo=" + tipo +
                '}';
    }

    public double recibirDanio(double danio)
    {
        double danioReal = Math.max(1, danio - defensa);
        danioReal = Math.min(danioReal, vida);

        vida -= danioReal;

        return danioReal;
    }

    public double atacar(Pokemon p)
    {
        if (p.vida <= 0)
        {
            return 0;
        }

        return p.recibirDanio(danio);
    }
}
