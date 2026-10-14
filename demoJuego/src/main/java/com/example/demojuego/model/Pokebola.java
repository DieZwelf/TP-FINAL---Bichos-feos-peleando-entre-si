package com.example.demojuego.model;

public class Pokebola extends Item{
    private boolean ocupada;
    private final String rutaImagen;

    public Pokebola(String nombre, String descripcion, boolean ocupada) {
        super(nombre, descripcion);
        this.rutaImagen = "/images/items/" + nombre.toLowerCase() + ".png";
        this.ocupada = ocupada;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    @Override
    public void usar() {
        System.out.println("Usando la Pokebola");
    }
}
