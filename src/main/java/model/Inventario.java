package model;

import java.util.*;

/**
 * La clase INVENTARIO tiene como atributos la capacidad del mismo, y un HashMap representativo del inv
 * Tiene un constructor con el HashMap, la capacidad no será modificable.
 * métodos: getter capacidad.
 * otros métodos: agregarObjeto(): agrega un item al inventario.
 * mostrar(): Retorna un set del inventario ordenado.
 * contarCantidad(): cuenta cuantos items de un mismo tipo hay en el inventario.
 * consumir(): Usa un item del inventario.
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class Inventario {
    private int capacidad = 50;
    private HashMap<Item, Integer> inventario;
    private int id;

    public Inventario(HashMap<Item, Integer> inventario) {
        this.inventario = inventario;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HashMap<Item, Integer> getItems() {
        return inventario;
    }

    public void setItems(HashMap<Item, Integer> inventario) {
        this.inventario = inventario;
    }

    /**
     * Este metodo verifica si está o no el item, para poder sumar a la cantidad existente el que se
     * agrega ahora. Verifica que el inventario no sea mayor a la capacidad y después lo agrega.
     * @param item es el que se va a agregar al inventario.
     * @return true or false
     */
    public boolean agregarObjeto(Item item)
    {
        if (item == null) {
            return false;
        }

        if (inventario.containsKey(item))
        {
            inventario.put(item, inventario.get(item) + 1);
            return true;
        }

        if(inventario.size() >= capacidad)
        {
            return false;
        }

        inventario.put(item, 1);
        return true;
    }

    /**
     * Mostrar está pensado especialmente para retornar la lista ya ordenada y que la interfaz
     * la capture cuando trate con el evento de mostrar el inventario.
     * Crea un Comparator (usar la interfaz Comparable no tiene sentido) y el .reverseOrder los devuelve
     * de mayor a menor, sino estarían de menor a mayor.
     * ===========================IMPORTANTE!!!!
     * NO HACER SET SOBRE LOS VALORES, MODIFICARÍA LA CANTIDAD SIN PASAR POR agregarObjeto() NI USAR()!!!!
     * LA LISTA ORDENADA VALE HASTA EL SIGUIENTE CAMBIO, LA INTERFAZ DEBERÍA LLAMARLA NUEVAMENTE CUANDO
     * HAYA UN CAMBIO
     * @return el inventario ordenado.
     */
    public List<Map.Entry<Item, Integer>> mostrar()
    {
        Comparator<Map.Entry<Item, Integer>> orden =
                Map.Entry.<Item,Integer>comparingByValue(Comparator.reverseOrder()).
                        thenComparing(e -> e.getKey().getNombre());

        List<Map.Entry<Item, Integer>> listaInventario = new ArrayList<>(inventario.entrySet());

        listaInventario.sort(orden);
        return listaInventario;
    }


    ///Verificacion, para que el Entrenador no pueda usar un objeto que no tiene
    public int contarCantidad(Item item)
    {
        return inventario.getOrDefault(item,0);
    }

    public boolean consumir(Item item)
    {
        int cantidad = contarCantidad(item);

        if (cantidad <= 0)
        {
            return false;
        }

        if (cantidad == 1)
        {
            inventario.remove(item);
        } else
        {
            inventario.put(item,cantidad-1);
        }
        return true;
    }


}
