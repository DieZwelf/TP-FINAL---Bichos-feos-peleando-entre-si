package controlador;

import model.Entrenador;
import model.Inventario;
import model.Item;
import model.Pokemon;
import vista.combate.PantallaInventario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adapta los datos del inventario para que la pantalla pueda mostrarlos. */
public class InventarioViewModel {

    private final Inventario inventario;
    private final PantallaInventario pantallaInventario;

    public InventarioViewModel(Inventario inventario, PantallaInventario pantallaInventario) {
        this.inventario = inventario;
        this.pantallaInventario = pantallaInventario;
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
        Pokemon objetivo = entrenador.getPokemonActivo();
        if (objetivo == null) {
            logs.add("No hay un Pokémon activo.");
            return false;
        }

        boolean usado = entrenador.usarItem(item, objetivo, logs);
        if (usado) actualizarVista();
        return usado;
    }
}
