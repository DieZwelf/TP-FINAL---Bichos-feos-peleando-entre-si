
package model;

import java.util.List;

public class GeneradorPokemonSalvaje {

    private static final List<DatosEspecie> ESPECIES = List.of(

            // ================= FUEGO =================
            new DatosEspecie("Charmander", 39, 52, 43, 65, TipoPokemon.FUEGO),
            new DatosEspecie("Vulpix", 38, 41, 40, 65, TipoPokemon.FUEGO),
            new DatosEspecie("Growlithe", 55, 70, 45, 60, TipoPokemon.FUEGO),

            // ================= AGUA =================
            new DatosEspecie("Squirtle", 44, 48, 65, 43, TipoPokemon.AGUA),
            new DatosEspecie("Psyduck", 50, 52, 48, 55, TipoPokemon.AGUA),
            new DatosEspecie("Poliwag", 40, 50, 40, 90, TipoPokemon.AGUA),

            // ================= TIERRA =================
            new DatosEspecie("Diglett", 10, 55, 25, 95, TipoPokemon.TIERRA),
            new DatosEspecie("Sandshrew", 50, 75, 85, 40, TipoPokemon.TIERRA),
            new DatosEspecie("Cubone", 50, 50, 95, 35, TipoPokemon.TIERRA),

            // ================= ELECTRICO =================
            new DatosEspecie("Pikachu", 35, 55, 40, 90, TipoPokemon.ELECTRICO),
            new DatosEspecie("Voltorb", 40, 30, 50, 100, TipoPokemon.ELECTRICO),
            new DatosEspecie("Electabuzz", 65, 83, 57, 105, TipoPokemon.ELECTRICO),

            // ================= PLANTA =================
            new DatosEspecie("Bulbasaur", 45, 49, 49, 45, TipoPokemon.PLANTA),
            new DatosEspecie("Oddish", 45, 50, 55, 30, TipoPokemon.PLANTA),
            new DatosEspecie("Bellsprout", 50, 75, 35, 40, TipoPokemon.PLANTA)
    );

    public static List<DatosEspecie> getESPECIES() {
        return ESPECIES;
    }

    public static Pokemon generarPokemonSegunNivel(int nivelPokemon) {

        if (nivelPokemon < 1 || nivelPokemon > TablaProgresion.NIVEL_MAXIMO) {
            throw new IllegalArgumentException("Nivel de pokemon inválido");
        }

        // Selección aleatoria entre las 15 especies
        int indice = (int) (Math.random() * ESPECIES.size());
        DatosEspecie d = ESPECIES.get(indice);

        // Conversión de estadísticas originales a la escala del juego
        double vida = 20 + d.getVida() * 0.55;
        int danio = (int) Math.round(d.getDanio() * 0.17);
        int defensa = (int) Math.round(d.getDefensa() * 0.10);
        int velocidad = (int) Math.round(d.getVelocidad() * 0.12);

// Construcción del Pokémon en nivel 1
        Pokemon salvaje = new Pokemon(d.getNombre(), vida, vida, danio, defensa, velocidad, d.getTipo(), 1, 0,
                danio, defensa, velocidad);

        // Escalado según el nivel del jugador
        if (nivelPokemon > 1) {
            int xpNecesaria = TablaProgresion.experienciaParaNivel(nivelPokemon);
            salvaje.ganarExperiencia(xpNecesaria);
        }

        return salvaje;
    }
}
