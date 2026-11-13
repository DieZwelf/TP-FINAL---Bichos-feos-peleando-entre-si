package controlador;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La clase 'CombateViewModel' adapta los datos del combate para que puedan ser utilizados por la vista.
 * Gestiona las propiedades observables de los Pokémon, las acciones del jugador, los registros del combate,
 * la experiencia obtenida y las recompensas.
 * Métodos: getModeloCombate(), sincronizarEstado(), ejecutarAccion(), refrescarEstado(),
 * cambiarPokemonActivo(), intentarUsarPokebola(), otorgarRecompensa(), getEquipoJugador(),
 * setOnTerminarCombateCallback(), hpJugadorProperty(), hpEnemigoProperty(), nombreJugadorProperty(),
 * nivelJugadorProperty(), getLogsCombate(), getNombreEnemigo(), nombreEnemigoProperty(),
 * getNivelEnemigo(), nivelEnemigoProperty(), repartirExperiencia().
 *
 * @author Uriel
 * @since 2026
 * @version 1.0
 */
public class CombateViewModel {

    private final Combate modeloCombate;

    private final DoubleProperty hpJugador = new SimpleDoubleProperty(1.0);
    private final DoubleProperty hpEnemigo = new SimpleDoubleProperty(1.0);

    // Nuevas propiedades observables para el mini-HUD del jugador
    private final StringProperty nombreJugador = new SimpleStringProperty("");
    private final StringProperty nivelJugador = new SimpleStringProperty("");

    private final StringProperty nombreEnemigo = new SimpleStringProperty("");
    private final StringProperty nivelEnemigo = new SimpleStringProperty("");

    private final ObservableList<String> logsCombate = FXCollections.observableArrayList();
    private Runnable onTerminarCombateCallback;

    /**
     * Obtiene el modelo de combate asociado al ViewModel.
     *
     * @return modelo de combate
     */
    public Combate getModeloCombate() {
        return modeloCombate;
    }

    /**
     * Construye el ViewModel e inicializa un combate entre el jugador y un Pokémon salvaje.
     * Sincroniza el estado inicial y registra la aparición del enemigo.
     *
     * @param jugador jugador que participa en el combate
     * @param pokemonSalvaje Pokémon salvaje contra el que se combate
     */
    public CombateViewModel(Jugador jugador, Pokemon pokemonSalvaje) {
        this.modeloCombate = new Combate(jugador, pokemonSalvaje);
        sincronizarEstado();

        logsCombate.add("¡Un " + pokemonSalvaje.getNombre() + " salvaje apareció!");
    }

    /**
     * Reparte experiencia a todos los Pokémon del equipo del jugador después de una victoria.
     * Registra en los logs la experiencia obtenida y los niveles subidos.
     */
    private void repartirExperiencia() {
        Pokemon salvaje = modeloCombate.getPokemonSalvaje();

        int experienciaGanada = salvaje.getNivel() * 20;

        for (Pokemon pokemon : modeloCombate.getJugador().getEquipo()) {
            int nivelesSubidos = pokemon.ganarExperiencia(experienciaGanada);

            logsCombate.add(pokemon.getNombre() + " recibió " + experienciaGanada + " XP.");

            if (nivelesSubidos > 0) {
                logsCombate.add(pokemon.getNombre() + " subió " + nivelesSubidos + " nivel(es)!");
            }
        }
    }

    /**
     * Ejecuta una acción seleccionada por el jugador durante el combate.
     * Actualiza los registros y sincroniza el estado del combate.
     * Si el jugador obtiene la victoria, reparte experiencia y otorga una recompensa.
     *
     * @param accion acción que realizará el jugador
     */
    public void ejecutarAccion(AccionJugador accion) {
        if (modeloCombate.getEstado() != model.EstadoCombate.ACTIVO) return;

        List<String> nuevosLogs = modeloCombate.turno(accion);
        logsCombate.addAll(nuevosLogs);

        sincronizarEstado();

        if (modeloCombate.getEstado() == model.EstadoCombate.VICTORIA) {
            repartirExperiencia();
            otorgarRecompensa();
        }

        if (modeloCombate.getEstado() != model.EstadoCombate.ACTIVO && onTerminarCombateCallback != null) {
            onTerminarCombateCallback.run();
        }
    }

    /**
     * Actualiza las propiedades observables utilizando el estado actual del modelo de combate.
     */
    public void refrescarEstado() {
        sincronizarEstado();
    }

    /**
     * Sincroniza la información del Pokémon activo y del enemigo con las propiedades observables.
     * Actualiza sus puntos de vida, nombres y niveles.
     */
    private void sincronizarEstado() {
        Pokemon jugadorActivo = modeloCombate.getJugador().getPokemonActivo();
        Pokemon enemigo = modeloCombate.getPokemonSalvaje();

        if (jugadorActivo != null) {
            if (jugadorActivo.getVidaMaxima() > 0) {
                hpJugador.set(jugadorActivo.getVida() / jugadorActivo.getVidaMaxima());
            }
            nombreJugador.set(jugadorActivo.getNombre());
            nivelJugador.set("Nivel: " + jugadorActivo.getNivel());
        }

        if (enemigo != null && enemigo.getVidaMaxima() > 0) {
            hpEnemigo.set(enemigo.getVida() / enemigo.getVidaMaxima());
            nombreEnemigo.set(enemigo.getNombre());
            nivelEnemigo.set("Nivel: " + enemigo.getNivel());
        }


    }

    /**
     * Intenta cambiar el Pokémon activo del jugador por el que se encuentra en la posición indicada.
     * Si el cambio es válido, actualiza el HUD y registra el cambio en los logs.
     *
     * @param posicion posición del Pokémon dentro del equipo
     * @return true si el cambio se realizó correctamente; false en caso contrario
     */
    public boolean cambiarPokemonActivo(int posicion) {

        boolean cambiado = modeloCombate.getJugador().elegirPokemonActivo(posicion);

        if (!cambiado) {
            logsCombate.add("No puedes seleccionar ese Pokémon.");
            return false;
        }

        // Actualizar nombre, nivel y vida del HUD.
        sincronizarEstado();

        Pokemon nuevoActivo = modeloCombate.getJugador().getPokemonActivo();

        logsCombate.add("¡Adelante, " + nuevoActivo.getNombre() + "!");

        return true;
    }

    /**
     * Intenta utilizar una Pokébola durante el combate.
     * Comprueba que el objeto sea una Pokébola y que el combate se encuentre activo.
     *
     * @param item objeto que se intenta utilizar
     * @return true si la Pokébola fue utilizada; false en caso contrario
     */
    public boolean intentarUsarPokebola(Item item)
    {
        if (!(item instanceof Pokebola)) return false;

        if (modeloCombate.getEstado() != EstadoCombate.ACTIVO) return false;

        Jugador jugador = modeloCombate.getJugador();
        int cantidadAntes = jugador.getInventario().contarCantidad(item);

        List<String> logs = modeloCombate.turno(item,modeloCombate.getPokemonSalvaje());

        logsCombate.addAll(logs);
        sincronizarEstado();

        int cantidadDespues = jugador.getInventario().contarCantidad(item);
        boolean usado = cantidadDespues < cantidadAntes;

        if(modeloCombate.getEstado() != EstadoCombate.ACTIVO && onTerminarCombateCallback != null)
        {
            onTerminarCombateCallback.run();
        }

        return usado;
    }

    /**
     * Genera una recompensa aleatoria para el jugador.
     * La recompensa puede ser una poción o una Pokébola y se agrega al inventario.
     */
    public void otorgarRecompensa()
    {
        Random random = new Random();
        boolean esPocion = random.nextBoolean();

        Item itemObtenido;
        if (esPocion)
        {
            int nivel = random.nextInt(1,4);
            itemObtenido = new Pocion((nivel*15));
        } else
        {
            itemObtenido = new Pokebola("Pokebola","Captura pokemones",false);
        }

        modeloCombate.getJugador().getInventario().agregarObjeto(itemObtenido);
        logsCombate.add("¡Has obtenido " + itemObtenido.getNombre() + "!");
    }

    /**
     * Obtiene el equipo de Pokémon del jugador.
     *
     * @return lista de Pokémon del equipo
     */
    public List<Pokemon> getEquipoJugador() {
        return modeloCombate.getJugador().getEquipo();
    }

    /**
     * Establece la función que se ejecutará cuando el combate termine.
     *
     * @param callback función que se ejecutará al finalizar el combate
     */
    public void setOnTerminarCombateCallback(Runnable callback) {
        this.onTerminarCombateCallback = callback;
    }

    /**
     * Obtiene la propiedad observable correspondiente a la vida del Pokémon del jugador.
     *
     * @return propiedad observable de la vida del jugador
     */
    public DoubleProperty hpJugadorProperty() {
        return hpJugador;
    }

    /**
     * Obtiene la propiedad observable correspondiente a la vida del Pokémon enemigo.
     *
     * @return propiedad observable de la vida del enemigo
     */
    public DoubleProperty hpEnemigoProperty() {
        return hpEnemigo;
    }

    /**
     * Obtiene la propiedad observable correspondiente al nombre del Pokémon del jugador.
     *
     * @return propiedad observable del nombre del jugador
     */
    public StringProperty nombreJugadorProperty() {
        return nombreJugador;
    }

    /**
     * Obtiene la propiedad observable correspondiente al nivel del Pokémon del jugador.
     *
     * @return propiedad observable del nivel del jugador
     */
    public StringProperty nivelJugadorProperty() {
        return nivelJugador;
    }

    /**
     * Obtiene los registros observables del combate.
     *
     * @return lista observable de mensajes del combate
     */
    public ObservableList<String> getLogsCombate() { return logsCombate; }

    /**
     * Obtiene el nombre actual del Pokémon enemigo.
     *
     * @return nombre del enemigo
     */
    public String getNombreEnemigo() {
        return nombreEnemigo.get();
    }

    /**
     * Obtiene la propiedad observable correspondiente al nombre del enemigo.
     *
     * @return propiedad observable del nombre del enemigo
     */
    public StringProperty nombreEnemigoProperty() {
        return nombreEnemigo;
    }

    /**
     * Obtiene el nivel actual del Pokémon enemigo.
     *
     * @return nivel del enemigo
     */
    public String getNivelEnemigo() {
        return nivelEnemigo.get();
    }

    /**
     * Obtiene la propiedad observable correspondiente al nivel del enemigo.
     *
     * @return propiedad observable del nivel del enemigo
     */
    public StringProperty nivelEnemigoProperty() {
        return nivelEnemigo;
    }


}