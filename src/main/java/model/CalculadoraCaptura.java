package model;

public class CalculadoraCaptura {

    private static final double PROBABILIDAD_BASE = 0.20;
    private static final double BONIFICACION_VIDA = 0.70;
    private static final double PENALIZACION_NIVEL = 0.05;

    public CalculadoraCaptura() {
    }

    public static double calcularProbabilidad(Pokemon pokemon)
    {
        if(pokemon == null && pokemon.getVida() < 0)
        {
            return 0;
        }

        double proporcionVida = pokemon.getVida() / pokemon.getVidaMaxima();
        double probabilidad = PROBABILIDAD_BASE + BONIFICACION_VIDA * (1-proporcionVida);


        double modificadorNivel = 1.0 / (1 + PENALIZACION_NIVEL * (pokemon.getNivel() -1));
        probabilidad *= modificadorNivel;

        return Math.max(0.05,Math.min(0.90,probabilidad));
    }
}
