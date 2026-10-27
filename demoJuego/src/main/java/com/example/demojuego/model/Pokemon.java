package com.example.demojuego.model;

import java.util.Objects;

/**
 * La clase POKEMON tiene como atributo un id autoincremental, nombre, tipo, experiencia y todas las
 * formas de los atributos (vida, danio, defensa, velocidad) que se necesitan en los diferentes
 * momentos del progreso del pokemon (son recalculados conforme sube de nivel).
 * Tiene un constructor vacío y uno con todos los atributos.
 * Métodos: toString(), equals() y hashCode() y getters y setters de todos los atirbutos menos los que
 * terminan en 'Base' (estos se usan en el cálculo de la ganancia de experiencia).
 * Otros métodos: recibirDanio(): calcula cuánto daño recibió el Pokemon.
 * atacar(): 'ataca' usando la auxiliar.
 * curar(): Calcula cuánto realmente curó la poción.
 * esNivelMaximo(): verifica que no haya llegado al nivel máximo.
 * ganarExperiencia(): recibe la cantidad ganada, verifica en qué nivel está y llama a recalcularEstadisticas().
 * recalcularEstadisticas(): llama a aplicarMejora() y le pasa todos los atributos 'Base'
 * aplicarMejora(): hace el cálculo estadistica x estadistica recibiendo el nivel y el atributo. Llama a
 * TablaProgresion.porcentaje
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Pokemon {
    private static int contador = 0;
    private int id;
    private String nombre;
    private double vidaMaxima;
    private double vida;
    private double danio;
    private double defensa;
    private double velocidad;
    private TipoPokemon tipo;
    private final String rutaImagen;
    private int nivel = 1;
    private int experiencia = 0;
    private double vidaBase;
    private double danioBase;
    private double defensaBase;
    private double velocidadBase;

    public Pokemon() {
        id = contador++;
        this.rutaImagen = "/images/items/" + nombre.toLowerCase() + ".png";
    }

    public Pokemon(String nombre, double vidaMaxima, double vida, double danio,double defensa, double velocidad, TipoPokemon tipo, int nivel, int experiencia,int danioBase, int defensaBase, int velocidadBase) {
        id = contador++;
        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vida;
        this.danio = danio;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.tipo = tipo;
        this.rutaImagen = "/images/items/" + nombre.toLowerCase() + ".png";
        this.nivel = nivel;
        this.experiencia = experiencia;
        this.vidaBase = vida;
        this.danioBase = danioBase;
        this.defensaBase = defensaBase;
        this.velocidadBase = velocidadBase;
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

    public double getDanio() {
        return danio;
    }

    public void setDanio(double danio) {
        this.danio = danio;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public double getVida() {
        return vida;
    }

    public void setVida(double vida) {
        this.vida = vida;
    }

    public double getDefensa() {
        return defensa;
    }

    public void setDefensa(double defensa) {
        this.defensa = defensa;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(double velocidad) {
        this.velocidad = velocidad;
    }

    public TipoPokemon getTipo() {
        return tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "nombre='" + nombre + '\'' +
                ", vida=" + vida +
                ", danio=" + danio +
                ", defensa=" + defensa +
                ", velocidad=" + velocidad +
                ", tipo=" + tipo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pokemon pokemon)) return false;
        return id == pokemon.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Accion de recibir el daño, hace un calculo basico con lo que sería la defensa del bicho
     *  Retorna el danio real tras el calculo.
     * @param danio El danio 'crudo' que recibe.
     * @return el danio real tras el calculo
     */
    public double recibirDanio(double danio)
    {
        double danioReal = Math.max(1, danio - defensa);
        danioReal = Math.min(danioReal, vida);

        vida -= danioReal;

        return danioReal;
    }

    /**
     * Accion de atacar verificando que no sea la vida 0 o menos
     * @param p el pokemon atacado
     * @return danio real causado al objetivo.
     */
    public double atacar(Pokemon p)
    {
        if (p.vida <= 0)
        {
            return 0;
        }

        return p.recibirDanio(danio);
    }

    /**
     * Recibe el atributo crudo, calcula lo que sería respecto a la vida actual del Pokemon y la maxima
     * y retorna lo que realmente curó
     * @param cantidad lo que cura la poción
     * @return cantidad real que curó de vida.
     */
    public double curar(double cantidad)
    {
        double curado = Math.max(0,Math.min(cantidad,vidaMaxima-vida));

        vida+=curado;

        return curado;
    }

    //===================================================MÉTODOS PARA GESTIONAR LA SUBIDA DE NIVEL

    /**
     * Verificacion de que no sea el nivel maximo para no aplicar estadisticas a Pokemones maxeados
     * @return llegó o no (T/F)
     */
    public boolean esNivelMaximo()
    {
        if(nivel >= TablaProgresion.NIVEL_MAXIMO)
        {
            return true;
        }

        return false;
    }

    /**
     * Verifica que la cantidad no sea menor a 0 o que haya llegado al máximo. Calcula el 'techo' como un
     * limite fijo para sacar cuánta experiencia se sacó (Si bien es aleatorio, tiene parámetros).
     * Si el nivel actual no es el máximo, entonces calcula las nuevas estadísticas y retorna el nivel
     * actual nuevo.
     * @param cantidad cantidad de xp ganada.
     * @return nivel actual o 0 en caso de fallo.
     */
    public int ganarExperiencia(int cantidad)
    {
        //Verificar que no este en el lvl maximo, o q la cantidad de xp que ganó sea 0
        if (esNivelMaximo() || cantidad <= 0)
        {
            return 0;
        }

        //Tope es la xp total que falta para estar en el nivel 5, lo uso como tope fijo
        int tope = TablaProgresion.experienciaParaNivel(TablaProgresion.NIVEL_MAXIMO);
        //Esto devuelve el menor de dos numeros, en teoría sería siempre experiencia a menos que este
        //ya en el tope permitido de xp (nivel 5)
        experiencia = Math.min(experiencia + cantidad, tope);

        int nivelAnterior = nivel;
        //Esto es una verificacion, si el nivel ya es el maximo no pasa nada
        nivel = TablaProgresion.nivelPorExperiencia(experiencia);

        //si el nivel actual no es el maximo entonces calcula las nuevas estadísticas
        if (nivel > nivelAnterior)
        {
            recalcularEstadisticas();
        }

        //retorna el nivel actual actualizado
        return nivel - nivelAnterior;
    }

    /**
     * Esto es un metodo intermedio. Es llamado por ganarExperiencia() y busca la nueva estadística
     * en TablaProgresion.Estadistica y las asigna tras hacer el calculo a través de aplicarMejora().
     */
    private void recalcularEstadisticas()
    {
        double vidaMaximaAnterior = vidaMaxima;

        //Llama a aplicarMejora, ésta recibe el valor base y el atributo
        vidaMaxima = aplicarMejora(vidaBase, TablaProgresion.Estadistica.VIDA);
        danio = aplicarMejora(danioBase, TablaProgresion.Estadistica.DANIO);
        defensa = aplicarMejora(defensaBase, TablaProgresion.Estadistica.DEFENSA);
        velocidad = aplicarMejora(velocidadBase, TablaProgresion.Estadistica.VELOCIDAD);

        // subir nivel no te cura el bicho
        vida += vidaMaxima - vidaMaximaAnterior;
    }

    /**
     * Esto es la quinta versión de este metodo :(
     * Base son todos los atributos (la mejora se calcula a partir de los atributos base)
     * e es la estadística a mejorar (por nivel se reciben todas, por eso se llama 4 veces x nivel
     * @param base el valor base a recalcular. TODA ESTADISTICA SE CALCULA A PARTIR DEL VALOR BASE
     * @param e estadística a buscar en TablaProgresion.Estadistica
     * @return la nueva estadistica, buscada en el metodo de Tabla.Progresion que busca x nivel.
     */
    private double aplicarMejora(double base, TablaProgresion.Estadistica e)
    {
        /**
         * Multiplicar y dividir por 100 saca los redondeos, se multiplica el atributo base
         * por las características que encuentre en TablaProgresion.porcentaje
         */
        return base * (100 + TablaProgresion.porcentaje(e,nivel)) / 100;
    }



}
