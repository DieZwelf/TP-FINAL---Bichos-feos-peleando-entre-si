package com.example.demojuego.model;

import java.util.ArrayList;
import java.util.List;

/*
Combate es una clase gestora, por ahora solo cuenta con la accion de atacar dividido en 3 metodos
 */

public class Combate {
    private Entrenador entrenador;
    private Pokemon pokemonSalvaje;
    private EstadoCombate estado;

    public Combate(Entrenador entrenador, Pokemon pokemonSalvaje) {
        this.entrenador = entrenador;
        this.pokemonSalvaje = pokemonSalvaje;
        this.estado = EstadoCombate.ACTIVO;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public Pokemon getPokemonSalvaje() {
        return pokemonSalvaje;
    }

    public EstadoCombate getEstado() {
        return estado;
    }


    /** Utiliza el metodo atacar de Pokemon.java
    És un metodo privado aparte, para evitar repetir lineas de codigo, simplificar y
    que solo sea accesible por el metodo turno.
    retorna String a propósito, es la única forma que encontré de registrar la accion
    */
    private String ejecutarAtaque(Pokemon atacante, Pokemon objetivo)
    {
        double danio = atacante.atacar(objetivo);

        return atacante.getNombre() + " le ha hecho " + Math.round(danio)
                + " de daño a " + objetivo.getNombre();
    }

    /** Metodo privado accesible por turno, utilizado para evitar repetir la verificacion
    en el metodo turno. Lo hice bool a propósito, para darle una utilidad con la interfaz a futuro
     */
    private boolean verificarFinal(List<String> registro)
    {
        Pokemon activo = entrenador.getPokemonActivo();

        if (pokemonSalvaje.getVida() <= 0)
        {
            estado = EstadoCombate.VICTORIA;
            registro.add(activo.getNombre() + " ha vencido a " + pokemonSalvaje.getNombre());
            return true;
        }

        if (activo.getVida() <= 0)
        {
            estado = EstadoCombate.DERROTA;
            registro.add(pokemonSalvaje.getNombre() + " ha vencido a " + activo.getNombre());
            return true;
        }

        return false;
    }


/** Clase turno de tipo List<String> para devolver el registro de lo que pasó
Recibe la accion que va a venir de la interacción del usuario con la interfaz
*/
    public List<String> turno(AccionJugador accion)
    {
        //Crea una lista distinta en cada combate distinto
        List<String> registrosCombate = new ArrayList<>();

        //Verifica que este activo, sino la devuelve vacía
        if (estado != EstadoCombate.ACTIVO)
        {
            return registrosCombate;
        }

        if (accion == AccionJugador.ATACAR)
         {
                //El pokemon activo es el que va a realizar la accion
                Pokemon activo = entrenador.getPokemonActivo();
                Pokemon primero;
                Pokemon segundo;

                //Compara velocidades para determinar quien ataca primero
                if(activo.getVelocidad() >= pokemonSalvaje.getVelocidad())
                {
                 primero = activo;
                 segundo = pokemonSalvaje;
                } else
                {
                primero = pokemonSalvaje;
                segundo = activo;
                }

            registrosCombate.add(ejecutarAtaque(primero,segundo));
           //Verificacion si el Pokemon esta caido  o no para permitirle o no pasar a su ataque
            if (verificarFinal(registrosCombate))
            {
                return registrosCombate;
            }

            registrosCombate.add(ejecutarAtaque(segundo,primero));
            verificarFinal(registrosCombate);

         }

    //Este es el return final de los logs, llevará en el caso futuro cuando se agreguen mas acciones
    //que pueda realizar un jugador
    return registrosCombate;
    }
}
