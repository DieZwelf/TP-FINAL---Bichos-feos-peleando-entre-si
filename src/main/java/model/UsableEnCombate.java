package model;

import java.util.List;

/**
 * Define el comportamiento de los objetos
 * que pueden utilizarse durante un combate.
 */
public interface UsableEnCombate {

    boolean usar(
            Jugador jugador,
            Pokemon objetivo,
            List<String> registro
    );
}