package controlador;

import model.Entrenador;
import model.Inventario;
import model.Pokemon;
import vista.PantallaInventario;
import model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventarioViewModel {

    private Inventario inventario;
    private PantallaInventario pantallaInventario;

    public InventarioViewModel(Inventario inventario, PantallaInventario pantallaInventario) {
        this.inventario = inventario;
        this.pantallaInventario = pantallaInventario;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public PantallaInventario getPantallaInventario() {
        return pantallaInventario;
    }

    public void setPantallaInventario(PantallaInventario pantallaInventario) {
        this.pantallaInventario = pantallaInventario;
    }

    public void actualizarVista (){

        List<Map.Entry<Item, Integer>> listaInventario = inventario.mostrar();
        List<String> objetos = new ArrayList<>();

        for (Map.Entry<Item, Integer> entry : listaInventario){

            String nombre = entry.getKey().getNombre();
            int cantidad = entry.getValue();

            objetos.add(nombre + " x" + cantidad);

        }

        pantallaInventario.actualizarInventario(objetos);

    }

    public void agregarObjeto(Item item) {

        if (inventario.agregarObjeto(item)) {
            actualizarVista();
        }
    }

    public List<Item> obtenerItemsOrdenados() {
        List<Map.Entry<Item, Integer>> listaInventario = inventario.mostrar();
        List<Item> items = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : listaInventario) {
            items.add(entry.getKey());
        }
        return items;
    }

    public boolean usarItem(Item item, Entrenador entrenador, List<String> registroLogs) {
        Pokemon objetivo = entrenador.getPokemonActivo();

        if (objetivo == null) {
            registroLogs.add("No hay un Pokémon activo.");
            return false;
        }

        boolean exito = item.usar(entrenador, objetivo, registroLogs);

        if (exito) {
            consumirObjeto(item);
        }

        return exito;
    }

    public void consumirObjeto (Item item){

       if (inventario.consumir(item)){
           actualizarVista();
    }

}}
