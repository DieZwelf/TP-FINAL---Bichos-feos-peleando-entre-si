package com.example.demojuego.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/*
Entrenador aparte de lo basico, cuenta con meétodos propios para el combate,
tambien deberia modificarse en un futuro si se agrega nivel, etc.
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


    //Fundamental para el metodo atacar de combate, llamara al menos 2 veces por combate
    public Pokemon getPokemonActivo() {
        return pokemonActivo;
    }

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

    public void aniadir(Pokemon p)
    {
        equipo.add(p);

        if (pokemonActivo == null)
        {
            pokemonActivo = p;
        }
    }

//Temporal para las pruebas, la interfaz deberia usar getEquipo()
    public void mostrarEquipo()
    {
        for (int i = 0; i< equipo.size(); i++)
        {
            System.out.println(equipo.get(i));
        }
    }

///Esto está incompleto, osea la idea correcta sería que dentro del if llamara al metodo consumir en
/// inventario,java (a desarrollar)
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

