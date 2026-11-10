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

    public Combate getModeloCombate() {
        return modeloCombate;
    }

    public CombateViewModel(Entrenador entrenador, Pokemon pokemonSalvaje) {
        this.modeloCombate = new Combate(entrenador, pokemonSalvaje);
        sincronizarEstado();

        logsCombate.add("¡Un " + pokemonSalvaje.getNombre() + " salvaje apareció!");
    }

    private void repartirExperiencia() {
        Pokemon salvaje = modeloCombate.getPokemonSalvaje();

        int experienciaGanada = salvaje.getNivel() * 20;

        for (Pokemon pokemon : modeloCombate.getEntrenador().getEquipo()) {
            int nivelesSubidos = pokemon.ganarExperiencia(experienciaGanada);

            logsCombate.add(pokemon.getNombre() + " recibió " + experienciaGanada + " XP.");

            if (nivelesSubidos > 0) {
                logsCombate.add(pokemon.getNombre() + " subió " + nivelesSubidos + " nivel(es)!");
            }
        }
    }

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

    public void refrescarEstado() {
        sincronizarEstado();
    }

    private void sincronizarEstado() {
        Pokemon jugadorActivo = modeloCombate.getEntrenador().getPokemonActivo();
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

    public boolean cambiarPokemonActivo(int posicion) {

        boolean cambiado = modeloCombate.getEntrenador().elegirPokemonActivo(posicion);

        if (!cambiado) {
            logsCombate.add("No puedes seleccionar ese Pokémon.");
            return false;
        }

        // Actualizar nombre, nivel y vida del HUD.
        sincronizarEstado();

        Pokemon nuevoActivo = modeloCombate.getEntrenador().getPokemonActivo();

        logsCombate.add("¡Adelante, " + nuevoActivo.getNombre() + "!");

        return true;
    }

    public boolean intentarUsarPokebola(Item item)
    {
        if (!(item instanceof Pokebola)) return false;

        if (modeloCombate.getEstado() != EstadoCombate.ACTIVO) return false;

        Entrenador entrenador = modeloCombate.getEntrenador();
        int cantidadAntes = entrenador.getInventario().contarCantidad(item);

        List<String> logs = modeloCombate.turno(item,modeloCombate.getPokemonSalvaje());

        logsCombate.addAll(logs);
        sincronizarEstado();

        int cantidadDespues = entrenador.getInventario().contarCantidad(item);
        boolean usado = cantidadDespues < cantidadAntes;

        if(modeloCombate.getEstado() != EstadoCombate.ACTIVO && onTerminarCombateCallback != null)
        {
            onTerminarCombateCallback.run();
        }

        return usado;
    }

    public void otorgarRecompensa()
    {
        Random random = new Random();
        boolean esPocion = random.nextBoolean();

        Item itemObtenido;
        if (esPocion)
        {
            int nivel = random.nextInt(1,4);
            itemObtenido = new Pocion("Pocion","Pocion de curacion",nivel, (int) random.nextDouble(1,11),(nivel*15));
        } else
        {
            itemObtenido = new Pokebola("Pokebola","Captura pokemones",false);
        }

        modeloCombate.getEntrenador().getInventario().agregarObjeto(itemObtenido);
        logsCombate.add("¡Has obtenido " + itemObtenido.getNombre() + "!");
    }

    public List<Pokemon> getEquipoJugador() {
        return modeloCombate.getEntrenador().getEquipo();
    }

    public void setOnTerminarCombateCallback(Runnable callback) {
        this.onTerminarCombateCallback = callback;
    }

    // Getters para los Bindings de la Vista
    public DoubleProperty hpJugadorProperty() {
        return hpJugador;
    }

    public DoubleProperty hpEnemigoProperty() {
        return hpEnemigo;
    }

    public StringProperty nombreJugadorProperty() {
        return nombreJugador;
    }

    public StringProperty nivelJugadorProperty() {
        return nivelJugador;
    }

    public ObservableList<String> getLogsCombate() { return logsCombate; }

    public String getNombreEnemigo() {
        return nombreEnemigo.get();
    }

    public StringProperty nombreEnemigoProperty() {
        return nombreEnemigo;
    }

    public String getNivelEnemigo() {
        return nivelEnemigo.get();
    }

    public StringProperty nivelEnemigoProperty() {
        return nivelEnemigo;
    }


}