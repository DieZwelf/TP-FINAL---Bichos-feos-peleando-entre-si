package model;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase gestora COMBATE tiene como atributos el entrenador, el pokemon salvaje y el estado del combate
 *  Tiene un constructor con todos los atributos.
 *  métodos: getters de todos los atributos, no tiene sentido setearlos desde afuera
 *  otros métodos: ejecutarAtaque(): forma de registrar la acción de atacar
 *  verificarFinal(): verificar si terminó o no el combate.
 *  turno(Item item, Pokemon objetivo): Usar un item gasta un turno
 *  participa(): verificación, comprueba que un Pokemon esté presente en el combate al usar un item
 *  turno(AccionJugador accion): Atacar, usa métodos auxiliares, calcula el más rápido para los turnos.
 *  @author Uriel
 *  @since 2026
 *  @version 1.2
 */

public class Combate {
    private Jugador jugador;
    private Pokemon pokemonSalvaje;
    private EstadoCombate estado;

    public Combate(Jugador jugador, Pokemon pokemonSalvaje) {
        this.jugador = jugador;
        this.pokemonSalvaje = pokemonSalvaje;
        this.estado = EstadoCombate.ACTIVO;
    }

    public Jugador getJugador() {
        return jugador;
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
        @param atacante el que ataca.
        @param objetivo el que recibe el ataque.
    */
    private String ejecutarAtaque(Pokemon atacante, Pokemon objetivo)
    {
        System.out.println("Atacante: " + atacante.getNombre());
        System.out.println("Nivel: " + atacante.getNivel());
        System.out.println("Daño: " + atacante.getDanio());

        System.out.println("Objetivo: " + objetivo.getNombre());
        System.out.println("Nivel: " + objetivo.getNivel());
        System.out.println("Defensa: " + objetivo.getDefensa());
        System.out.println("Vida: " + objetivo.getVida());

        double danio = atacante.atacar(objetivo);

        System.out.println("Daño causado: " + danio);
        System.out.println("-------------------------");

        return atacante.getNombre() + " le ha hecho " + Math.round(danio)
                + " de daño a " + objetivo.getNombre();
    }

    /** Metodo privado accesible por turno, utilizado para evitar repetir la verificacion
    en el metodo turno. Lo hice bool a propósito, para darle una utilidad con la interfaz a futuro
     @param registro registra todas las acciones.
     @return boolean, es el final o no.
     */
    private boolean verificarFinal(List<String> registro)
    {
        Pokemon activo = jugador.getPokemonActivo();

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

    /**
     * turno(Item item, Pokemon objetivo): usa un item en el objetivo deseado, retorna la lista de
     * 'logs', utiliza el auxiliar participa() para que no se use un item en alguien que no está
     * participando.
     * @param item: item a usar.
     * @param objetivo: donde se va a usar el item seleccionado.
     * @return lista de 'logs', lo que ocurrió en este turno.
     */
    public List<String> turno(Item item, Pokemon objetivo)
    {
        List<String> registros = new ArrayList<>();

        if(estado != EstadoCombate.ACTIVO)
        {
            return registros;
        }

        if (!participa(objetivo))
        {
            registros.add("Ese pokemon no participa en el combate");
            return registros;
        }

        if(!jugador.usarItem(item,objetivo,registros))
        {
            return registros;
        }

        if (item instanceof Pokebola pokebola && pokebola.isOcupada())
        {
            estado = EstadoCombate.CAPTURA;
            return registros;
        }

        registros.add(ejecutarAtaque(pokemonSalvaje, jugador.getPokemonActivo()));
        verificarFinal(registros);

        return registros;
    }


    /**
     * Verifica si el Pokemon recibido es o el Pokemon Salvaje o uno en el equipo.
     * @param p el Pokemon parametro a ver si está participando en el turno actual (salvaje o equipo)
     * @return true o false
     */
    private boolean participa(Pokemon p)
    {
        return p != null && (p == pokemonSalvaje || jugador.getEquipo().contains(p));
    }

    /** Clase turno de tipo List<String> para devolver el registro de lo que pasó (turno Combate)
     *  @param accion lo que el usuario decidió hacer.
     *  @return los registros de lo que pasó.
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


        if (accion == AccionJugador.HUIR) {
            estado = EstadoCombate.HUIDA;
            registrosCombate.add("Escapaste del combate.");
            return registrosCombate;
        }

        if (accion == AccionJugador.ATACAR)
         {
                //El pokemon activo es el que va a realizar la accion
                Pokemon activo = jugador.getPokemonActivo();
                if (activo == null) {
                    registrosCombate.add("No hay un Pokémon activo.");
                    return registrosCombate;
                }
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
