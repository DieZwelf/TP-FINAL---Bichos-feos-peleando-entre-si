package controlador;

import model.*;
import vista.inventario.PantallaInventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * La clase 'InventarioViewModel' adapta los datos del inventario para que la pantalla pueda mostrarlos.
 * Gestiona la actualización de los objetos visibles, su ordenamiento y el uso de los diferentes ítems.
 * Métodos: setCallbackUsarPokebola(), actualizarVista(), obtenerItemsOrdenados(), usarItem().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class InventarioViewModel {

    private final Inventario inventario;
    private final PantallaInventario pantallaInventario;
    private Predicate<Item> callbackUsarPokebola;

    /**
     * Construye el ViewModel asociándolo con un inventario y una pantalla.
     *
     * @param inventario inventario cuyos objetos serán administrados
     * @param pantallaInventario pantalla donde se mostrarán los objetos
     */
    public InventarioViewModel(Inventario inventario, PantallaInventario pantallaInventario) {
        this.inventario = inventario;
        this.pantallaInventario = pantallaInventario;
    }

    /**
     * Establece la función que permite intentar utilizar una Pokébola.
     *
     * @param callbackUsarPokebola función que recibe un objeto y devuelve si pudo utilizarse
     */
    public void setCallbackUsarPokebola(Predicate<Item> callbackUsarPokebola) {
        this.callbackUsarPokebola = callbackUsarPokebola;
    }

    /**
     * Actualiza los elementos mostrados en la pantalla del inventario.
     * Obtiene los objetos y sus cantidades y los transforma en textos para la vista.
     */
    public void actualizarVista() {
        List<String> objetos = new ArrayList<>();
        for (Map.Entry<Item, Integer> entrada : inventario.mostrar()) {
            objetos.add(entrada.getKey().getNombre() + " x" + entrada.getValue());
        }
        pantallaInventario.actualizarInventario(objetos);
    }

    /**
     * Obtiene los objetos del inventario en una lista.
     *
     * @return lista de objetos ordenados
     */
    public List<Item> obtenerItemsOrdenados() {
        List<Item> items = new ArrayList<>();
        for (Map.Entry<Item, Integer> entrada : inventario.mostrar()) {
            items.add(entrada.getKey());
        }
        return items;
    }

    /**
     * Intenta utilizar un objeto del inventario.
     * Gestiona el uso de pociones y Pokébolas, registra los mensajes correspondientes
     * y actualiza la vista cuando el objeto se utiliza correctamente.
     *
     * @param item objeto que se desea utilizar
     * @param jugador jugador propietario del inventario
     * @param logs lista donde se registran los mensajes de la operación
     * @return true si el objeto fue utilizado correctamente; false en caso contrario
     */
    public boolean usarItem(Item item, Jugador jugador, List<String> logs) {

        if (item instanceof Pocion) {
            Pokemon objetivo = jugador.getPokemonActivo();
            if (objetivo == null) {
                logs.add("No hay un Pokémon activo.");
                return false;
            }

            // Validar si ya tiene la vida al máximo
            if (objetivo.getVida() >= objetivo.getVidaMaxima()) {
                logs.add("¡" + objetivo.getNombre() + " ya tiene la vida al máximo!");
                return false;
            }

            boolean usado = jugador.usarItem(item, objetivo, logs);
            if (usado) actualizarVista();
            return usado;
        }

        if (item instanceof Pokebola) {
            // Validar si se intenta usar fuera de combate
            if (callbackUsarPokebola == null) {
                logs.add("¡La Pokébola no se puede usar directamente aquí, solo en combate!");
                return false;
            }
        }

        if (callbackUsarPokebola == null) {
            logs.add("No se puede utilizar este objeto.");
            return false;
        }

        boolean usado = callbackUsarPokebola.test(item);
        if (usado) actualizarVista();

        return usado;
    }
}