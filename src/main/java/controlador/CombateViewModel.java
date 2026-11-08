package controlador;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AccionJugador;
import model.Combate;
import model.Entrenador;
import model.Pokemon;

import java.util.List;

public class CombateViewModel {

    private final Combate modeloCombate;

    private final DoubleProperty hpJugador = new SimpleDoubleProperty(1.0);
    private final DoubleProperty hpEnemigo = new SimpleDoubleProperty(1.0);

    // Nuevas propiedades observables para el mini-HUD del jugador
    private final StringProperty nombreJugador = new SimpleStringProperty("");
    private final StringProperty nivelJugador = new SimpleStringProperty("");

    private final ObservableList<String> logsCombate = FXCollections.observableArrayList();
    private Runnable onTerminarCombateCallback;

    public CombateViewModel(Entrenador entrenador, Pokemon pokemonSalvaje) {
        this.modeloCombate = new Combate(entrenador, pokemonSalvaje);
        sincronizarEstado();

        logsCombate.add("¡Un " + pokemonSalvaje.getNombre() + " salvaje apareció!");
    }

    public void ejecutarAccion(AccionJugador accion) {
        if (modeloCombate.getEstado() != model.EstadoCombate.ACTIVO) return;

        java.util.List<String> nuevosLogs = modeloCombate.turno(accion);
        logsCombate.setAll(nuevosLogs);

        sincronizarEstado();

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

    public List<Pokemon> getEquipoJugador() {
        return modeloCombate.getEntrenador().getEquipo();
    }

    public void setOnTerminarCombateCallback(Runnable callback) {
        this.onTerminarCombateCallback = callback;
    }

    // Getters para los Bindings de la Vista
    public DoubleProperty hpJugadorProperty() { return hpJugador; }
    public DoubleProperty hpEnemigoProperty() { return hpEnemigo; }
    public StringProperty nombreJugadorProperty() { return nombreJugador; }
    public StringProperty nivelJugadorProperty() { return nivelJugador; }
    public ObservableList<String> getLogsCombate() { return logsCombate; }
}