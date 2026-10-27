package com.example.demojuego.model;


import java.util.List;
import java.util.Objects;

/**
 *  La clase abstracta ITEM tiene como atributos el nombre, la descripcion y la ruta de su imagen.
 *  Tiene un constructor con todos los parametros.
 *  métodos: getters y setters, equals(), hashCode() y toString()
 *  Otros métodos: usar(): abstracto heredado por las hijas.
 *  @author Uriel
 *  @since 2026
 *  @version 1.0
 */
public abstract class Item {
    private final String nombre;
    private String descripcion;
    private final String rutaImagen;


    public Item(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rutaImagen = "/images/items/" + nombre.toLowerCase() + ".png";
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(nombre, item.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nombre);
    }

    @Override
    public String toString() {
        return "Item{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", rutaImagen='" + rutaImagen + '\'' +
                '}';
    }

    public abstract boolean usar(Entrenador entrenador, Pokemon objetivo, List<String> registro);
}
