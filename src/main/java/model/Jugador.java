/**
 * Representa al jugador dentro del juego.
 *
 * Administra su movimiento, equipo Pokémon, inventario,
 * Pokémon activo y datos correspondientes a su perfil.
 */

package model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


public class Jugador {

    private double x;
    private double y;

    private static double velocidad = 3;

    private int id;

    private String direccion;
    private int frameActual;
    private boolean enMovimiento;

    private TipoJugador tipo = TipoJugador.USUARIO;

    private LinkedHashSet<String> pokemonesCapturados;
    private Coleccion<Pokemon> equipo;

    private String rango = "Inicial";
    private Pokemon pokemonActivo;
    private Inventario inventario;


    public Jugador(double xInicial, double yInicial, TipoJugador tipo) {

        this.x = xInicial;
        this.y = yInicial;
        this.direccion = "ABAJO";
        this.frameActual = 0;
        this.enMovimiento = false;
        this.tipo = tipo;

        this.pokemonesCapturados = new LinkedHashSet<>();
        this.equipo = new Coleccion<>();

        this.inventario = new Inventario(new HashMap<>());
    }

    public Jugador(
            int id,
            double x,
            double y,
            TipoJugador tipo,
            LinkedHashSet<String> pokemonesCapturados,
            List<Pokemon> equipo,
            String rango,
            Pokemon pokemonActivo,
            Inventario inventario
    ) {

        this.id = id;
        this.x = x;
        this.y = y;
        this.tipo = tipo;

        this.pokemonesCapturados = pokemonesCapturados != null
                ? pokemonesCapturados
                : new LinkedHashSet<>();

        this.equipo = new Coleccion<>();
        this.equipo.agregarTodos(equipo);

        this.rango = rango;

        this.inventario = inventario != null
                ? inventario
                : new Inventario(new HashMap<>());

        if (pokemonActivo != null
                && pokemonActivo.isEnEquipo()
                && this.equipo.contiene(pokemonActivo)) {

            this.pokemonActivo = pokemonActivo;

        } else {

            this.pokemonActivo = obtenerPrimerPokemonActivo();
        }

        this.direccion = "ABAJO";
        this.frameActual = 0;
        this.enMovimiento = false;
    }

    public void moverArriba() {
        this.y -= velocidad;
        this.direccion = "ARRIBA";
    }

    public void moverAbajo() {
        this.y += velocidad;
        this.direccion = "ABAJO";
    }

    public void moverIzquierda() {
        this.x -= velocidad;
        this.direccion = "IZQUIERDA";
    }

    public void moverDerecha() {
        this.x += velocidad;
        this.direccion = "DERECHA";
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public TipoJugador getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getFrameActual() {
        return frameActual;
    }

    public boolean isEnMovimiento() {
        return enMovimiento;
    }

    public LinkedHashSet<String> getPokemonesCapturados() {
        return pokemonesCapturados;
    }

    /**
     * Devuelve únicamente los Pokémon que están en el equipo.
     */
    public List<Pokemon> getEquipo() {

        return equipo.stream()
                .filter(p -> p != null && p.isEnEquipo())
                .collect(Collectors.toList());
    }

    /**
     * Devuelve todos los Pokémon almacenados,
     * incluidos aquellos que están fuera del equipo.
     */
    public List<Pokemon> getEquipoCompleto() {
        return equipo.obtenerTodos();
    }

    public String getRango() {
        return rango;
    }

    public Pokemon getPokemonActivo() {
        return pokemonActivo;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setTipo(TipoJugador tipo) {
        this.tipo = tipo;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setFrameActual(int frameActual) {
        this.frameActual = frameActual;
    }

    public void setEnMovimiento(boolean enMovimiento) {
        this.enMovimiento = enMovimiento;
    }

    public void setPokemonesCapturados(
            LinkedHashSet<String> pokemonesCapturados
    ) {

        this.pokemonesCapturados = pokemonesCapturados != null
                ? pokemonesCapturados
                : new LinkedHashSet<>();
    }

    public void setEquipo(List<Pokemon> equipo) {

        this.equipo = new Coleccion<>();
        this.equipo.agregarTodos(equipo);

        if (pokemonActivo == null
                || !pokemonActivo.isEnEquipo()
                || !this.equipo.contiene(pokemonActivo)) {

            pokemonActivo = obtenerPrimerPokemonActivo();
        }
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public void setPokemonActivo(Pokemon pokemonActivo) {

        if (pokemonActivo != null
                && pokemonActivo.isEnEquipo()
                && equipo.contiene(pokemonActivo)
                && pokemonActivo.getVida() > 0) {

            this.pokemonActivo = pokemonActivo;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean elegirPokemonActivo(int pos) {

        List<Pokemon> equipoActivo = getEquipo();

        if (pos < 0 || pos >= equipoActivo.size()) {
            return false;
        }

        Pokemon elegido = equipoActivo.get(pos);

        if (elegido.getVida() <= 0) {
            return false;
        }

        pokemonActivo = elegido;

        return true;
    }

    private Pokemon obtenerPrimerPokemonActivo() {

        return equipo.stream()
                .filter(p -> p != null && p.isEnEquipo())
                .findFirst()
                .orElse(null);
    }

    public boolean aniadir(Pokemon p) {

        if (p == null || getEquipo().size() >= 5) {
            return false;
        }

        // Si ya estaba guardado, simplemente vuelve al equipo.
        if (equipo.contiene(p)) {

            if (p.isEnEquipo()) {
                return false;
            }

            p.setEnEquipo(true);

        } else {

            p.setEnEquipo(true);
            equipo.agregar(p);
        }

        pokemonesCapturados.add(p.getNombre());

        if (pokemonActivo == null) {
            pokemonActivo = p;
        }

        return true;
    }

    /**
     * Baja lógica: el Pokémon permanece almacenado,
     * pero deja de formar parte del equipo activo.
     */
    public boolean eliminarPokemon(Pokemon pokemon) {

        if (pokemon == null) {
            return false;
        }

        if (!equipo.contiene(pokemon) || !pokemon.isEnEquipo()) {
            return false;
        }

        if (getEquipo().size() <= 1) {
            return false;
        }

        boolean eraActivo = pokemonActivo == pokemon;

        pokemon.setEnEquipo(false);

        if (eraActivo) {
            pokemonActivo = obtenerPrimerPokemonActivo();
        }

        return true;
    }

    public void mostrarEquipo() {

        for (Pokemon pokemon : getEquipo()) {
            System.out.println(pokemon);
        }
    }

    public boolean usarItem(
            Item item,
            Pokemon objetivo,
            List<String> registro
    ) {

        if (item == null
                || objetivo == null
                || inventario.contarCantidad(item) <= 0) {

            registro.add(
                    "No tienes suficiente cantidad de ese item o el objetivo no es válido."
            );

            return false;
        }

        boolean aplicado = item.usar(this, objetivo, registro);

        if (aplicado) {
            return inventario.consumir(item);
        }

        return false;
    }
}
