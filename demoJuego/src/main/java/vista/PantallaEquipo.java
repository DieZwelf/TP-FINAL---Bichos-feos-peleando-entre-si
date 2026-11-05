
package vista;

import controlador.CombateViewModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Entrenador;
import model.Pokemon;

import java.util.List;

public class PantallaEquipo extends BorderPane implements IPantalla {

    private final GestorPantallas pantallas;
    private final Entrenador entrenador;

    public PantallaEquipo(GestorPantallas pantallas, Entrenador entrenador) {
        this.pantallas = pantallas;
        this.entrenador = entrenador;

        inicializarPantalla();
    }

    private void inicializarPantalla() {

        getStyleClass().add("pantalla-combate-root");

        // Título
        Label titulo = new Label("MI EQUIPO POKEMON");
        titulo.getStyleClass().add("texto-retro");

        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(30));

        setTop(titulo);

        // Lista de Pokémon
        VBox listaPokemon = new VBox(15);
        listaPokemon.setPadding(new Insets(25));
        listaPokemon.setAlignment(Pos.CENTER);

        List<Pokemon> equipo = entrenador.getEquipo();

        for (int i = 0; i < equipo.size(); i++) {

            Pokemon pokemon = equipo.get(i);

            agregarPokemon(listaPokemon, pokemon, i);
        }

        setCenter(listaPokemon);

        // Botón volver
        Button btnVolver = new Button("VOLVER AL COMBATE");
        btnVolver.getStyleClass().add("boton-retro");

        btnVolver.setOnAction(e ->
                pantallas.volverAlCombate()
        );

        HBox inferior = new HBox(btnVolver);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(25));

        setBottom(inferior);
    }

    private void agregarPokemon(
            VBox lista,
            Pokemon pokemon,
            int posicion
    ) {

        Label nombre = new Label(
                pokemon.getNombre() + "  Nv. " + pokemon.getNivel()
        );

        nombre.getStyleClass().add("texto-retro");

        double vidaMaxima = pokemon.getVidaMaxima();

        double porcentaje = vidaMaxima > 0
                ? pokemon.getVida() / vidaMaxima
                : 0;

        ProgressBar barraVida = new ProgressBar(porcentaje);
        barraVida.setPrefWidth(250);
        barraVida.getStyleClass().add("barra-hp");

        Label vida = new Label(
                pokemon.getVida() + " / " + vidaMaxima + " PS"
        );

        vida.getStyleClass().add("texto-retro");

        VBox informacion = new VBox(
                5,
                nombre,
                barraVida,
                vida
        );

        Button btnSeleccionar = new Button("SELECCIONAR");
        btnSeleccionar.getStyleClass().add("boton-retro");

        if (pokemon == entrenador.getPokemonActivo()) {
            btnSeleccionar.setText("ACTIVO");
            btnSeleccionar.setDisable(true);
        }

        btnSeleccionar.setOnAction(e -> {

            boolean cambiado = entrenador.elegirPokemonActivo(posicion);

            if (cambiado) {
                pantallas.volverAlCombate();
            }
        });

        HBox tarjeta = new HBox(
                25,
                informacion,
                btnSeleccionar
        );

        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(650);

        tarjeta.getStyleClass().add("caja-hud");

        lista.getChildren().add(tarjeta);
    }

    @Override
    public void mostrar(Stage stage) {

        Scene scene = new Scene(this, 1000, 800);

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}

