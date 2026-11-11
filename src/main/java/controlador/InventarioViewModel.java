package controlador;

import model.*;
import vista.combate.PantallaInventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Adapta los datos del inventario para que la pantalla pueda mostrarlos. */
public class InventarioViewModel {

    private final Inventario inventario;
    private final PantallaInventario pantallaInventario;
    private Predicate<Item> callbackUsarPokebola;

    public InventarioViewModel(Inventario inventario, PantallaInventario pantallaInventario) {
        this.inventario = inventario;
        this.pantallaInventario = pantallaInventario;
    }

    public void setCallbackUsarPokebola(Predicate<Item> callbackUsarPokebola) {
        this.callbackUsarPokebola = callbackUsarPokebola;
    }

    public void actualizarVista() {
        List<String> objetos = new ArrayList<>();
        for (Map.Entry<Item, Integer> entrada : inventario.mostrar()) {
            objetos.add(entrada.getKey().getNombre() + " x" + entrada.getValue());
        }
        pantallaInventario.actualizarInventario(objetos);
    }

    public List<Item> obtenerItemsOrdenados() {
        List<Item> items = new ArrayList<>();
        for (Map.Entry<Item, Integer> entrada : inventario.mostrar()) {
            items.add(entrada.getKey());
        }
        return items;
    }

    public boolean usarItem(Item item, Entrenador entrenador, List<String> logs) {

        if (item instanceof Pocion) {
            Pokemon objetivo = entrenador.getPokemonActivo();
            if (objetivo == null) {
                logs.add("No hay un Pokémon activo.");
                return false;
            }

            // Validar si ya tiene la vida al máximo
            if (objetivo.getVida() >= objetivo.getVidaMaxima()) {
                logs.add("¡" + objetivo.getNombre() + " ya tiene la vida al máximo!");
                return false;
            }

            boolean usado = entrenador.usarItem(item, objetivo, logs);
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