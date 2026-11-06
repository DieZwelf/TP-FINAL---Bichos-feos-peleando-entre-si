package controlador;

import model.Inventario;
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

    public void actualizarVista (){

        List<Map.Entry<Item, Integer>> listaInventario = inventario.mostrar();
        List<String> objetos = new ArrayList<>();

        for (Map.Entry<Item, Integer> entry : listaInventario){

            String nombre = entry.getKey().getNombre();
            int cantidad = entry.getValue();

            objetos.add(cantidad, nombre);

        }

        pantallaInventario.actualizarInventario(objetos);

    }

    public void agregarObjeto(Item item) {

        if (inventario.agregarObjeto(item)) {
            actualizarVista();
        }
    }

    public void consumirObjeto (Item item){

       if (inventario.consumir(item)){
           actualizarVista();
    }

}}
