package controlador;

import model.*;

import java.util.List;

/**
 * La clase 'AdministradorController' permite administrar las especies de Pokémon disponibles.
 * Permite registrar nuevas especies y obtener la lista de especies registradas.
 * Métodos: registrarEspecie(), obtenerEspeciesDisponibles().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class AdministradorController {

    /**
     * Registra una nueva especie de Pokémon con sus características y tipo.
     *
     * @param nombre nombre de la especie
     * @param tipo tipo de Pokémon
     * @param vida cantidad de vida de la especie
     * @param danio cantidad de daño de la especie
     * @param defensa cantidad de defensa de la especie
     * @param velocidad velocidad de la especie
     */
    public void registrarEspecie(String nombre, TipoPokemon tipo, double vida, double danio, double defensa, double velocidad)
    {

        DatosEspecie especie = new DatosEspecie(nombre, vida, danio, defensa, velocidad, tipo);

        GeneradorPokemonSalvaje.agregarEspecie(especie);
    }

    /**
     * Obtiene la lista de especies de Pokémon disponibles.
     *
     * @return lista de especies registradas
     */
    public List<DatosEspecie> obtenerEspeciesDisponibles()
    {
        return GeneradorPokemonSalvaje.getESPECIES();
    }

}