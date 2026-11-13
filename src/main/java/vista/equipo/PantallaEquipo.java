
package vista.equipo;

import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import model.Jugador;
import model.Pokemon;

public class PantallaEquipo extends PantallaBase {

    private final Jugador jugador;

    private VBox listaPokemon;
    private Label mensaje;

    public PantallaEquipo(GestorPantallas pantallas, Jugador jugador) {
        super(pantallas);
        this.jugador = jugador;
    }

    @Override
    protected Parent crearContenido() {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("pantalla-combate-root");

        Label titulo = new Label("MI EQUIPO POKEMON");
        titulo.getStyleClass().add("texto-retro");

        BorderPane.setAlignment(titulo, Pos.CENTER);
        BorderPane.setMargin(titulo, new Insets(30));

        root.setTop(titulo);

        listaPokemon = new VBox(15);
        listaPokemon.setPadding(new Insets(25));
        listaPokemon.setAlignment(Pos.CENTER);

        ScrollPane scroll = new ScrollPane(listaPokemon);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;"
        );

        root.setCenter(scroll);

        mensaje = new Label("");
        mensaje.getStyleClass().add("texto-retro");

        Button volver = new Button("VOLVER");
        volver.getStyleClass().add("boton-retro");

        volver.setOnAction(e ->
                pantallas.volverDesdeEquipo()
        );

        VBox inferior = new VBox(12, mensaje, volver);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(25));

        root.setBottom(inferior);

        actualizarLista();

        return root;
    }

    private void actualizarLista() {

        listaPokemon.getChildren().clear();

        for (Pokemon pokemon : jugador.getEquipo()) {

            if (pokemon != null) {
                listaPokemon.getChildren().add(
                        crearTarjetaPokemon(pokemon)
                );
            }
        }

        if (jugador.getEquipo().isEmpty()) {
            listaPokemon.getChildren().add(
                    new Label("No hay Pokémon en el equipo.")
            );
        }
    }

    private HBox crearTarjetaPokemon(Pokemon pokemon) {

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

        Button seleccionar = new Button("SELECCIONAR");
        seleccionar.getStyleClass().add("boton-equipo");

        if (pokemon == jugador.getPokemonActivo()) {
            seleccionar.setText("ACTIVO");
            seleccionar.setDisable(true);
        }

        seleccionar.setOnAction(e -> {

            int posicion = jugador.getEquipo().indexOf(pokemon);

            if (jugador.elegirPokemonActivo(posicion)) {
                pantallas.volverDesdeEquipo();
            } else {
                mensaje.setText(
                        "No podés seleccionar un Pokémon debilitado."
                );
            }
        });

        Button eliminar = new Button("ELIMINAR");
        eliminar.getStyleClass().add("boton-equipo");

        eliminar.setOnAction(e -> {

            boolean eliminado = jugador.eliminarPokemon(pokemon);

            if (eliminado) {

                mensaje.setText(
                        pokemon.getNombre() + " fue eliminado del equipo."
                );

                actualizarLista();

            } else {

                mensaje.setText(
                        "No podés eliminar al último Pokémon del equipo."
                );
            }
        });

        HBox botones = new HBox(8, seleccionar, eliminar);
        botones.setAlignment(Pos.CENTER);

        HBox tarjeta = new HBox(
                25,
                informacion,
                botones
        );

        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(750);
        tarjeta.getStyleClass().add("caja-hud");

        return tarjeta;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
