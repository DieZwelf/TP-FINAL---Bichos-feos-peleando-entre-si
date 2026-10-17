package com.example.demojuego.model;

public final class TablaProgresion {

    public static final int  NIVEL_MAXIMO = 5;
    private final int nivel;
    private final int experienciaAcumulada;
    private final int vida;
    private final int danio;
    private final int velocidad;
    private final int defensa;

    public TablaProgresion(int nivel, int experienciaAcumulada, int vida, int danio, int velocidad, int defensa) {
        this.nivel = nivel;
        this.experienciaAcumulada = experienciaAcumulada;
        this.vida = vida;
        this.danio = danio;
        this.velocidad = velocidad;
        this.defensa = defensa;
    }

    //Aca inicializo, osea los porcentajes de subida siempre son iguales para todos los bichos
    private static final TablaProgresion[] FILAS =
            {
                    new TablaProgresion(2,     90,       15,   10,    0,   0),
                    new TablaProgresion(3,     198,      15,    10,     8,  10),
                    new TablaProgresion(4,     328,      27,   20,    8,   10),
                    new TablaProgresion(5,     484,      27,    20,     16,  20)
            };


    //La xp q necesitas para estar en el nivel maximo (osea sirve para cualq nivel, pero lo uso en ganarExperiencia()
    public static int experienciaParaNivel(int nivel) {
        for (TablaProgresion f : FILAS) {
            if (f.nivel == nivel) {
                return f.experienciaAcumulada;
            }
        }
        //Nivel 1 no necesita nada
        return 0;
    }

    // Nivel más alto cuyo requisito ya se alcanzó con esa experiencia total
    public static int nivelPorExperiencia(int experiencia) {
        int nivel = 1;
        for (TablaProgresion f : FILAS) {
            if (experiencia >= f.experienciaAcumulada) {
                nivel = f.nivel;
            }
        }
        return nivel;
    }

    //Lo cree acá mismo pq me daba mucho quilombo hacer una clase separada
    public enum Estadistica
    {
        VIDA,
        DANIO,
        VELOCIDAD,
        DEFENSA;
    }

    /**
     *  Me saqué las ganas de usar este switch con flechas >:)
     *  Esto es llamado por aplicarMejora en Pokemon.java
     *  Recibe e y nivel, básicamente busca en que nivel está y devuelve el valor para cada atributo
     */
    public static int porcentaje(Estadistica e, int nivel)
    {
        for (TablaProgresion t: FILAS)
        {
            if(t.nivel == nivel)
            {
                return switch (e){
                    case VIDA -> t.vida;
                    case DANIO -> t.danio;
                    case VELOCIDAD -> t.velocidad;
                    case DEFENSA -> t.defensa;
                };
            }
        }
        return 0;
    }
}


