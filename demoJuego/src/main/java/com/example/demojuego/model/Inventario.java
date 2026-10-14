package com.example.demojuego.model;

import java.util.*;

public class Inventario {
    private int capacidad = 50;
    private HashMap<Item, Integer> inventario;

    public Inventario(HashMap<Item, Integer> inventario) {
        this.inventario = inventario;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean agregarObjeto(Item item)
    {
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
        int cantidad = inventario.getOrDefault(item,0);
        return cantidad;
    }
}
