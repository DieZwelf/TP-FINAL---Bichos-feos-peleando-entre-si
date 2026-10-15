package com.example.demojuego.model;

import java.util.List;

public class Pocion extends Item{
    private int nivel;
    private int duracion;
    private double curar;

    public Pocion(String nombre, String descripcion, int nivel, int duracion, double curar) {
        super(nombre, descripcion);
        this.nivel = nivel;
        this.duracion = duracion;
        this.curar = curar;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    @Override
    public boolean usar(Entrenador entrenador, Pokemon objetivo, List<String> registro) {
        if(objetivo.getVida() <= 0)
        {
            registro.add("El objetivo esta debilitado");
        }

        double curado = objetivo.curar(curar);

        if (curado <= 0)
        {
            registro.add(objetivo.getNombre() + " ya tiene la vida al maximo");
            return false;
        }

        registro.add("Usaste " + getNombre() + ": " + objetivo.getNombre() + " recupero " +
                        Math.round(curado) + " puntos de vida");
        return true;
    }
}
