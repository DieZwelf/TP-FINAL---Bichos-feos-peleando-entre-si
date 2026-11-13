package vista.equipo;

import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Jugador;
import model.Pokemon;

public class PantallaEquipo extends PantallaBase {

    private final Jugador jugador;

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

        VBox listaPokemon = new VBox(15);
        listaPokemon.setPadding(new Insets(25));
        listaPokemon.setAlignment(Pos.CENTER);
        for (int i = 0; i < jugador.getEquipo().size(); i++) {
            listaPokemon.getChildren().add(crearTarjetaPokemon(jugador.getEquipo().get(i), i));
        }
        root.setCenter(listaPokemon);

        Button volver = new Button("VOLVER");
        volver.getStyleClass().add("boton-retro");
        volver.setOnAction(e -> pantallas.volverDesdeEquipo());
        HBox inferior = new HBox(volver);
        inferior.setAlignment(Pos.CENTER);
        inferior.setPadding(new Insets(25));
        root.setBottom(inferior);
        return root;
    }

    private HBox crearTarjetaPokemon(Pokemon pokemon, int posicion) {
        Label nombre = new Label(pokemon.getNombre() + "  Nv. " + pokemon.getNivel());
        nombre.getStyleClass().add("texto-retro");

        double vidaMaxima = pokemon.getVidaMaxima();
        double porcentaje = vidaMaxima > 0 ? pokemon.getVida() / vidaMaxima : 0;
        ProgressBar barraVida = new ProgressBar(porcentaje);
        barraVida.setPrefWidth(250);
        barraVida.getStyleClass().add("barra-hp");

        Label vida = new Label(pokemon.getVida() + " / " + vidaMaxima + " PS");
        vida.getStyleClass().add("texto-retro");
        VBox informacion = new VBox(5, nombre, barraVida, vida);

        Button seleccionar = new Button("SELECCIONAR");
        seleccionar.getStyleClass().add("boton-equipo");
        if (pokemon == jugador.getPokemonActivo()) {
            seleccionar.setText("ACTIVO");
            seleccionar.setDisable(true);
        }
        seleccionar.setOnAction(e -> {
            if (jugador.elegirPokemonActivo(posicion)) {
                pantallas.volverDesdeEquipo();
            }
        });

        HBox tarjeta = new HBox(25, informacion, seleccionar);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(15));
        tarjeta.setMaxWidth(650);
        tarjeta.getStyleClass().add("caja-hud");
        return tarjeta;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");
    }
}
