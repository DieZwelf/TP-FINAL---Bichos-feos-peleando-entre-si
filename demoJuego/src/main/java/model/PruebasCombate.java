package model;

import java.util.*;

/**
Hice esta clase para poder probar a medida se agregan funcionalidades al combate hasta
que implementemos la interfaz, la idea sería borrarla a futuro.
 */
/*public class PruebasCombate {

    public static void main(String[] args) {
    //Combate simple donde unicamente se ataca, deberia ganar Pikachu
        Pokemon pokemon1 = new Pokemon("Pikachu",50.2,10.1,3,2,TipoPokemon.ELECTRICO);
        Pokemon pokemon2 = new Pokemon("Charizard",60,8,4,4,TipoPokemon.FUEGO);

        TreeMap<Integer, Pokemon> pokedex = new TreeMap<>();
        List<Pokemon> equipo = new ArrayList<>();

        Entrenador entrenador = new Entrenador("Uriel",pokedex,equipo,"Entrenador Novato");
        entrenador.aniadir(pokemon1);

        Combate combate = new Combate(entrenador,pokemon2);

        int numeroTurno = 1;
        while (combate.getEstado() == EstadoCombate.ACTIVO)
        {
            System.out.println("--- Turno " + numeroTurno + " ---");

            for (String mensaje : combate.turno(AccionJugador.ATACAR))
            {
                System.out.println(mensaje);
            }

            numeroTurno++;
        }
    }
}

 */
