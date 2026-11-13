
package model;

import java.util.Objects;

public class Pokemon {

    private int id;
    private String nombre;
    private double vidaMaxima;
    private double vida;
    private double danio;
    private double defensa;
    private TipoPokemon tipo;
    private final String rutaImagen;
    private int nivel = 1;
    private int experiencia = 0;

    private double vidaBase;
    private double danioBase;
    private double defensaBase;

    // Baja lógica.
    private boolean enEquipo = true;

    public Pokemon() {
        this.rutaImagen = "/images/items/.png";
    }

    public Pokemon(String nombre, double vidaMaxima, double vida,
                   double danio, double defensa, TipoPokemon tipo,
                   int nivel, int experiencia, int danioBase,
                   int defensaBase) {

        this.nombre = nombre;
        this.vidaMaxima = vidaMaxima;
        this.vida = vida;
        this.danio = danio;
        this.defensa = defensa;
        this.tipo = tipo;

        this.rutaImagen = "/images/items/"
                + (nombre != null ? nombre.toLowerCase() : "") + ".png";

        this.nivel = nivel;
        this.experiencia = experiencia;

        this.vidaBase = vidaMaxima;
        this.danioBase = danioBase;
        this.defensaBase = defensaBase;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public TipoPokemon getTipo() {
        return tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public double getDanioBase() {
        return danioBase;
    }

    public void setDanioBase(double danioBase) {
        this.danioBase = danioBase;
    }

    public double getDefensaBase() {
        return defensaBase;
    }

    public void setDefensaBase(double defensaBase) {
        this.defensaBase = defensaBase;
    }

    public boolean isEnEquipo() {
        return enEquipo;
    }

    public void setEnEquipo(boolean enEquipo) {
        this.enEquipo = enEquipo;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "nombre='" + nombre + '\'' +
                ", vida=" + vida +
                ", danio=" + danio +
                ", defensa=" + defensa +
                ", tipo=" + tipo +
                ", enEquipo=" + enEquipo +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Pokemon pokemon)) {
            return false;
        }

        // Los Pokémon sin guardar se comparan por identidad.
        if (id == 0 || pokemon.id == 0) {
            return false;
        }

        return id == pokemon.id;
    }

    @Override
    public int hashCode() {
        return id == 0
                ? System.identityHashCode(this)
                : Objects.hash(id);
    }

    public double recibirDanio(double danio) {

        double danioReal = Math.max(1, danio - defensa);
        danioReal = Math.min(danioReal, vida);

        vida -= danioReal;

        return danioReal;
    }

    public double atacar(Pokemon p) {

        if (p.vida <= 0) {
            return 0;
        }

        return p.recibirDanio(danio);
    }

    public double curar(double cantidad) {

        double curado = Math.max(
                0,
                Math.min(cantidad, vidaMaxima - vida)
        );

        vida += curado;

        return curado;
    }

    public boolean esNivelMaximo() {
        return nivel >= TablaProgresion.NIVEL_MAXIMO;
    }

    public int ganarExperiencia(int cantidad) {

        if (esNivelMaximo() || cantidad <= 0) {
            return 0;
        }

        int tope = TablaProgresion.experienciaParaNivel(
                TablaProgresion.NIVEL_MAXIMO
        );

        experiencia = Math.min(experiencia + cantidad, tope);

        int nivelAnterior = nivel;

        nivel = TablaProgresion.nivelPorExperiencia(experiencia);

        if (nivel > nivelAnterior) {
            recalcularEstadisticas();
        }

        return nivel - nivelAnterior;
    }

    private void recalcularEstadisticas() {

        double vidaMaximaAnterior = vidaMaxima;

        vidaMaxima = aplicarMejora(
                vidaBase,
                TablaProgresion.Estadistica.VIDA
        );

        danio = aplicarMejora(
                danioBase,
                TablaProgresion.Estadistica.DANIO
        );

        defensa = aplicarMejora(
                defensaBase,
                TablaProgresion.Estadistica.DEFENSA
        );

        vida += vidaMaxima - vidaMaximaAnterior;
    }

    private double aplicarMejora(
            double base,
            TablaProgresion.Estadistica e
    ) {

        return base * (
                100 + TablaProgresion.porcentaje(e, nivel)
        ) / 100;
    }
}
