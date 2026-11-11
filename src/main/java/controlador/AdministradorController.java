package controlador;

import model.*;

import java.util.List;

public class AdministradorController {

    public void registrarEspecie(String nombre, TipoPokemon tipo, double vida, double danio, double defensa, double velocidad)
    {

        DatosEspecie especie = new DatosEspecie(nombre, vida, danio, defensa, velocidad, tipo);

        GeneradorPokemonSalvaje.agregarEspecie(especie);
    }

    public List<DatosEspecie> obtenerEspeciesDisponibles()
    {
        return GeneradorPokemonSalvaje.getESPECIES();
    }

}