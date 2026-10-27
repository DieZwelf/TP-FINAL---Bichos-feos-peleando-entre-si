package model;

import java.util.List;
import java.util.TreeMap;

/**
 * La clase ENTRENADOR tiene como atributos un id autoincremental, un nombre, una 'Pokedex', un equipo
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

public class Entrenador {
    private static int contador = 0;
    private int id;
    private String nombre;
    private TreeMap<Integer,Pokemon> pokemonesCapturados;
    private List<Pokemon> equipo;
    private String rango;
    private Pokemon pokemonActivo;
    private Inventario inventario;

    public Entrenador() {
        id = contador++;
    }

    public Entrenador(String nombre, TreeMap<Integer, Pokemon> pokemonesCapturados, List<Pokemon> equipo, String rango) {
        id = contador++;
        this.nombre = nombre;
        this.pokemonesCapturados = pokemonesCapturados;
        this.equipo = equipo;
        this.rango = rango;
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

    public TreeMap<Integer, Pokemon> getPokemonesCapturados() {
        return pokemonesCapturados;
    }

    public void setPokemonesCapturados(TreeMap<Integer, Pokemon> pokemonesCapturados) {
        this.pokemonesCapturados = pokemonesCapturados;
    }

    public List<Pokemon> getEquipo() {
        return equipo;
    }

    public void setEquipo(List<Pokemon> equipo) {
        this.equipo = equipo;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }


    /**
     * Fundamental para el metodo atacar de combate, llamara al menos 2 veces por combate
     * @return Pokemon (el activo)
     */
    public Pokemon getPokemonActivo() {
        return pokemonActivo;
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
    public void aniadir(Pokemon p)
    {
        equipo.add(p);

        if (pokemonActivo == null)
        {
            pokemonActivo = p;
        }
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
        if(item == null || inventario.contarCantidad(item) < 0)
        {
            registro.add("No tienes suficiente cantidad de ese item");
        }

        boolean aplicado = item.usar(this, objetivo, registro);

        if(aplicado)
        {
            return inventario.consumir(item);
        }

        return aplicado;
    }
}

