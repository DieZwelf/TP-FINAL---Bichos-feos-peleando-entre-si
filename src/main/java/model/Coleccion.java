
package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Colección genérica que permite almacenar y administrar
 * elementos de cualquier tipo.
 *
 * @param <T> tipo de objeto almacenado.
 */
public class Coleccion<T> {

    private final List<T> elementos;

    public Coleccion() {
        this.elementos = new ArrayList<>();
    }

    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public void agregarTodos(List<T> elementos) {
        if (elementos != null) {
            this.elementos.addAll(elementos);
        }
    }

    public boolean contiene(T elemento) {
        return elementos.contains(elemento);
    }

    public int cantidad() {
        return elementos.size();
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }

    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= elementos.size()) {
            return null;
        }

        return elementos.get(posicion);
    }

    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }

    public Stream<T> stream() {
        return elementos.stream();
    }
}
