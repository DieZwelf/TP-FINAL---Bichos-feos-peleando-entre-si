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
    private final String rutaImagen;

    public Pokemon() {
        id = contador++;
        this.rutaImagen = "/images/items" + nombre.toLowerCase() + ".png";
    }

    public Pokemon(String nombre, double vida, double danio,double defensa, double velocidad, TipoPokemon tipo) {
        id = contador++;
        this.nombre = nombre;
        this.vida = vida;
        this.danio = danio;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.tipo = tipo;
        this.rutaImagen = "/images/items" + nombre.toLowerCase() + ".png";
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

    /** Accion de recibir el daño, hace un calculo basico con lo que sería la defensa del bicho
    Retorna el danio real tras el calculo
    */
    public double recibirDanio(double danio)
    {
        double danioReal = Math.max(1, danio - defensa);
        danioReal = Math.min(danioReal, vida);

        vida -= danioReal;

        return danioReal;
    }

//Accion de atacar verificando que no sea la vida 0 o menos
    public double atacar(Pokemon p)
    {
        if (p.vida <= 0)
        {
            return 0;
        }

        return p.recibirDanio(danio);
    }
}
