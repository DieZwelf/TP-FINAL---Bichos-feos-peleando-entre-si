package model;

import java.util.List;

public class GeneradorPokemonSalvaje {
    private static final List<String> ESPECIES = List.of("Pidgey", "Rattata", "Caterpie", "Weedle");

    public static Pokemon generarPokemonSegunNivel(int nivelJugador) {
        String especie = ESPECIES.get((int) (Math.random() * ESPECIES.size()));

        // Valores base de ejemplo para nivel 1
        double vidaBase = 50.0;
        double danioBase = 10.0;
        double defensaBase = 5.0;
        double velocidadBase = 10.0;

        Pokemon salvaje = new Pokemon(especie, vidaBase, vidaBase, danioBase, defensaBase, velocidadBase,
                TipoPokemon.FUEGO, 1, 0, (int)danioBase, (int)defensaBase, (int)velocidadBase
        );

        // Si el jugador es mayor a nivel 1, escala al salvaje usando el metodo de tabla progresion
        if (nivelJugador > 1) {
            int xpNecesaria = TablaProgresion.experienciaParaNivel(nivelJugador);
            salvaje.ganarExperiencia(xpNecesaria);
        }

        return salvaje;
    }
}
