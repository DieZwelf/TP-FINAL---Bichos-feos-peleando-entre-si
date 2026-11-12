package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * La clase JUGADOR tiene como atributos un id autoincremental, un nombre, una 'Pokedex', un equipo
 * un rango, un pokemon activo y un inventario.
 * Tiene un constructor "vacío" y uno con todos los atributos menos el id
 * métodos: getters y setters para todos los atributos (menos el setter de id)
 * otros métodos: elegirPokemonActivo(): usando la posición en el list, se 'elige' al deseado
 * aniadir(): añade un pokemon al equipo
 * usarItem(): usa el item que le pasan en el objetivo a elección
 * @author Uriel
 * @since 2026
 * @version 1.0
 */



public class Jugador {
    private double x;
    private double y;
    private static double velocidad = 3;
    private int id;

    private String direccion; // "ABAJO", "ARRIBA", "IZQUIERDA", "DERECHA"
    private int frameActual;
    private boolean enMovimiento;
    private TipoJugador tipo;

    private TreeMap<Integer,Pokemon> pokemonesCapturados;
    private List<Pokemon> equipo;
    private String rango;
    private Pokemon pokemonActivo;
    private Inventario inventario;


    //Nuevo jugador
    public Jugador(double xInicial, double yInicial, TipoJugador tipo) {
        this.x = xInicial;
        this.y = yInicial;
        this.velocidad = velocidad;
        this.direccion = "ABAJO";
        this.frameActual = 0;
        this.enMovimiento = false;
        this.tipo = tipo;
        this.pokemonesCapturados = new TreeMap<>();
        this.equipo = new ArrayList<>(5);
        this.inventario = new Inventario(new HashMap<>());
    }

    //Jugador existente
    public Jugador(int id, double x, double y, TipoJugador tipo, TreeMap<Integer, Pokemon> pokemonesCapturados, List<Pokemon> equipo,
                   String rango, Pokemon pokemonActivo, Inventario inventario) {
        this.id=id;
        this.x = x;
        this.y = y;
        this.tipo = tipo;

        this.pokemonesCapturados = pokemonesCapturados != null ? pokemonesCapturados : new TreeMap<>();
        this.equipo = equipo != null ? new ArrayList<>(equipo) : new ArrayList<>(5);
        this.rango = rango;
        this.inventario = new Inventario(new HashMap<>());
        if (!this.equipo.isEmpty()) {
            this.pokemonActivo = this.equipo.get(0);
        }
    }

    // Métodos para mover al personaje
    public void moverArriba() { this.y -= velocidad; this.direccion = "ARRIBA"; }
    public void moverAbajo() { this.y += velocidad; this.direccion = "ABAJO"; }
    public void moverIzquierda() { this.x -= velocidad; this.direccion = "IZQUIERDA"; }
    public void moverDerecha() { this.x += velocidad; this.direccion = "DERECHA"; }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public TipoJugador getTipo() {
        return tipo;
    }
    public int getId() {
        return id;
    }
    public String getDireccion() { return direccion; }
    public int getFrameActual() { return frameActual; }
    public boolean isEnMovimiento() { return enMovimiento; }
    public TreeMap<Integer, Pokemon> getPokemonesCapturados() {
        return pokemonesCapturados;
    }
    public List<Pokemon> getEquipo() {
        return equipo;
    }
    public String getRango() {
        return rango;
    }
    /**
     * Fundamental para el metodo atacar de combate, llamara al menos 2 veces por combate
     * @return Pokemon (el activo)
     */
    public Pokemon getPokemonActivo() {
        return pokemonActivo;
    }
    public Inventario getInventario() {
        return inventario;
    }

    //Setters
    public void setTipo(TipoJugador tipo) {
        this.tipo = tipo;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setFrameActual(int frameActual) { this.frameActual = frameActual; }
    public void setEnMovimiento(boolean enMovimiento) { this.enMovimiento = enMovimiento; }
    public void setPokemonesCapturados(TreeMap<Integer, Pokemon> pokemonesCapturados) {
        this.pokemonesCapturados = pokemonesCapturados != null ? pokemonesCapturados : new TreeMap<>();
    }
    public void setEquipo(List<Pokemon> equipo) {
        this.equipo = equipo != null ? new ArrayList<>(equipo) : new ArrayList<>(5);
        if (pokemonActivo != null && !this.equipo.contains(pokemonActivo)) {
            pokemonActivo = null;
        }
        if (pokemonActivo == null && !this.equipo.isEmpty()) {
            pokemonActivo = this.equipo.get(0);
        }
    }
    public void setRango(String rango) {
        this.rango = rango;
    }
    public void setPokemonActivo(Pokemon pokemonActivo) {
        this.pokemonActivo = pokemonActivo;
    }




    /**
     * Elige el pokemon activo pasado en la posición que le pasan por parámetro
     * @param pos es la posición del Pokemon dentro del equipo.
     * @return true or false
     */
    public boolean elegirPokemonActivo(int pos)
    {
        if (pos < 0 || pos >= equipo.size())
        {
            return false;
        }

        Pokemon elegido = equipo.get(pos);

        if(elegido.getVida() <= 0)
        {
            return false;
        }

        pokemonActivo = elegido;
        return true;

    }




    /**
     * Añadir el pokemon pasado por parametro al equipo.
     * @param p es el Pokemon a agregar al equipo.
     */
    public boolean aniadir(Pokemon p)
    {
        if (equipo.size() >= 5 || p == null) {
            return false;
        }

        equipo.add(p);
        pokemonesCapturados.put(p.getId(), p);

        if (pokemonActivo == null)
        {
            pokemonActivo = p;
        }

        return true;
    }



    /**
     * Temporal para las pruebas, la interfaz deberia usar getEquipo()
     */
    public void mostrarEquipo()
    {
        for (int i = 0; i< equipo.size(); i++)
        {
            System.out.println(equipo.get(i));
        }
    }

    /**
     * usarItem verifica que haya en el inventario y que no sea null. Luego lo 'usa' y guarda el registro
     * @param item item a usar.
     * @param objetivo el pokemon donde se va a usar el item.
     * @param registro el registro de lo que ocurrió.
     * @return true or false.
     */
    public boolean usarItem(Item item, Pokemon objetivo, List<String> registro)
    {
        if (item == null || objetivo == null || inventario.contarCantidad(item) <= 0) {
            registro.add("No tienes suficiente cantidad de ese item o el objetivo no es válido.");
            return false;
        }

        boolean aplicado = item.usar(this, objetivo, registro);

        if(aplicado)
        {
            return inventario.consumir(item);
        }

        return aplicado;
    }

}
