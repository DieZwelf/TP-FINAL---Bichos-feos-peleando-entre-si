package com.example.demojuego.model;

public class Pocion extends Item{
    int nivel;
    int duracion;

    public Pocion(String nombre, String descripcion, int nivel, int duracion) {
        super(nombre, descripcion);
        this.nivel = nivel;
        this.duracion = duracion;
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
    public void usar() {
        System.out.println("Usando la pocion");
    }
}
