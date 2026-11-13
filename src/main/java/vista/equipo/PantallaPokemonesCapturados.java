
package vista.equipo;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import model.Jugador;
import model.Pokemon;
import vista.gestor.GestorPantallas;
import vista.base.PantallaBase;

import java.io.InputStream;
import java.util.List;

public class PantallaPokemonesCapturados extends PantallaBase {

    private final Jugador jugador;

    private ListView<Pokemon> listaPokemon;

    private Label nombrePokemon;
    private Label tipoPokemon;
    private Label nivelPokemon;
    private Label experienciaPokemon;

    private Label vidaPokemon;
    private Label danioPokemon;
    private Label defensaPokemon;

    private ImageView imagenPokemon;

    public PantallaPokemonesCapturados(
            GestorPantallas pantallas,
            Jugador jugador
    ) {
        super(pantallas);
        this.jugador = jugador;
    }

    @Override
    protected Parent crearContenido() {

        BorderPane root = new BorderPane();

        root.getStyleClass().add("pantalla-combate-root");
        root.setPadding(new Insets(12, 22, 8, 22));

        // =========================
        // ENCABEZADO
        // =========================

        Label titulo = crearEtiqueta("  POKÉMONES CAPTURADOS  ");
        titulo.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #F4D58D;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 4, 0.3, 2, 2);"
        );

        VBox encabezado = new VBox(titulo);
        encabezado.setAlignment(Pos.CENTER);
        encabezado.setPadding(new Insets(0, 0, 8, 0));

        root.setTop(encabezado);

        // =========================
        // LISTA DE POKEMON
        // =========================

        Label tituloLista = crearEtiqueta("MI COLECCIÓN");

        listaPokemon = new ListView<>();

        List<Pokemon> capturados = jugador.getEquipoCompleto();

        listaPokemon.setItems(
                FXCollections.observableArrayList(capturados)
        );

        listaPokemon.setFixedCellSize(66);

        listaPokemon.setCellFactory(list -> new ListCell<>() {

            private final Button boton = new Button();

            private final Label nombre = crearEtiqueta("");
            private final Label nivel = crearEtiqueta("");

            private final VBox contenido = new VBox(3, nombre, nivel);

            {
                boton.getStyleClass().add("boton-retro");
                boton.setMaxWidth(Double.MAX_VALUE);
                boton.setMinHeight(58);
                boton.setPrefHeight(58);
                boton.setAlignment(Pos.CENTER_LEFT);
                boton.setPadding(new Insets(6, 12, 6, 12));

                contenido.setAlignment(Pos.CENTER_LEFT);
                boton.setGraphic(contenido);

                boton.setOnAction(e -> {
                    Pokemon seleccionado = getItem();

                    if (seleccionado != null) {
                        getListView()
                                .getSelectionModel()
                                .select(seleccionado);
                    }
                });
            }

            @Override
            protected void updateItem(Pokemon pokemon, boolean empty) {

                super.updateItem(pokemon, empty);

                if (empty || pokemon == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nombre.setText(pokemon.getNombre().toUpperCase());
                    nivel.setText("NIVEL " + pokemon.getNivel());

                    setGraphic(boton);
                    setText(null);
                }
            }
        });

        listaPokemon.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-padding: 3;"
        );

        VBox panelLista = new VBox(
                10,
                tituloLista,
                listaPokemon
        );

        panelLista.getStyleClass().add("caja-hud");
        panelLista.setPadding(new Insets(12));
        panelLista.setPrefWidth(300);
        panelLista.setMinWidth(280);
        panelLista.setMaxWidth(300);

        VBox.setVgrow(listaPokemon, Priority.ALWAYS);

        // =========================
        // IMAGEN DEL POKEMON
        // =========================

        imagenPokemon = new ImageView();

        imagenPokemon.setFitWidth(510);
        imagenPokemon.setFitHeight(570);
        imagenPokemon.setPreserveRatio(true);
        imagenPokemon.setSmooth(false);

        VBox contenedorImagen = new VBox(imagenPokemon);

        contenedorImagen.setAlignment(Pos.CENTER);
        contenedorImagen.setPadding(new Insets(8));
        contenedorImagen.setMinWidth(450);
        contenedorImagen.setMaxWidth(530);

        HBox.setHgrow(contenedorImagen, Priority.ALWAYS);

        // =========================
        // INFORMACION GENERAL
        // =========================

        nombrePokemon = crearEtiqueta("SELECCIONÁ UN POKÉMON");
        tipoPokemon = crearEtiqueta("");
        nivelPokemon = crearEtiqueta("");
        experienciaPokemon = crearEtiqueta("");

        VBox informacionGeneral = new VBox(
                10,
                nombrePokemon,
                tipoPokemon,
                nivelPokemon,
                experienciaPokemon
        );

        informacionGeneral.getStyleClass().add("caja-hud");
        informacionGeneral.setAlignment(Pos.CENTER);
        informacionGeneral.setPadding(new Insets(18));
        informacionGeneral.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // ESTADISTICAS
        // =========================

        Label tituloEstadisticas = crearEtiqueta("ESTADÍSTICAS");

        vidaPokemon = crearEtiqueta("VIDA: -");
        danioPokemon = crearEtiqueta("DAÑO: -");
        defensaPokemon = crearEtiqueta("DEFENSA: -");

        GridPane grillaEstadisticas = new GridPane();

        grillaEstadisticas.setHgap(10);
        grillaEstadisticas.setVgap(10);
        grillaEstadisticas.setAlignment(Pos.CENTER);
        grillaEstadisticas.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints columna1 = new ColumnConstraints();
        columna1.setPercentWidth(50);
        columna1.setHgrow(Priority.ALWAYS);

        ColumnConstraints columna2 = new ColumnConstraints();
        columna2.setPercentWidth(50);
        columna2.setHgrow(Priority.ALWAYS);

        grillaEstadisticas.getColumnConstraints().addAll(
                columna1,
                columna2
        );

        VBox cajaVida = crearCajaEstadistica(vidaPokemon);
        VBox cajaDanio = crearCajaEstadistica(danioPokemon);
        VBox cajaDefensa = crearCajaEstadistica(defensaPokemon);

        grillaEstadisticas.add(cajaVida, 0, 0, 2, 1);
        grillaEstadisticas.add(cajaDanio, 0, 1);
        grillaEstadisticas.add(cajaDefensa, 1, 1);

        VBox panelEstadisticas = new VBox(
                12,
                tituloEstadisticas,
                grillaEstadisticas
        );

        panelEstadisticas.getStyleClass().add("caja-hud");
        panelEstadisticas.setPadding(new Insets(16));
        panelEstadisticas.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // PANEL DE INFORMACION
        // =========================

        VBox informacionPokemon = new VBox(
                18,
                informacionGeneral,
                panelEstadisticas
        );

        informacionPokemon.setAlignment(Pos.CENTER);
        informacionPokemon.setPadding(new Insets(8));
        informacionPokemon.setMinWidth(380);
        informacionPokemon.setMaxWidth(480);

        HBox.setHgrow(informacionPokemon, Priority.ALWAYS);

        // =========================
        // PANEL DE DETALLES
        // =========================

        HBox panelDetalles = new HBox(
                15,
                contenedorImagen,
                informacionPokemon
        );

        panelDetalles.setAlignment(Pos.CENTER);
        panelDetalles.setPadding(new Insets(8));

        HBox.setHgrow(panelDetalles, Priority.ALWAYS);

        // =========================
        // CONTENIDO PRINCIPAL
        // =========================

        HBox contenido = new HBox(
                15,
                panelLista,
                panelDetalles
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox.setHgrow(panelDetalles, Priority.ALWAYS);

        root.setCenter(contenido);

        // =========================
        // BOTON VOLVER
        // =========================

        Button volver = new Button("VOLVER");

        volver.getStyleClass().add("boton-retro");

        volver.setOnAction(e ->
                pantallas.volverDesdePokemonesCapturados()
        );

        HBox pie = new HBox(volver);

        pie.setAlignment(Pos.CENTER);
        pie.setPadding(new Insets(8, 0, 5, 0));

        root.setBottom(pie);

        // =========================
        // SELECCION DE POKEMON
        // =========================

        listaPokemon.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, seleccionado) ->
                        mostrarDetalles(seleccionado)
                );

        if (!capturados.isEmpty()) {
            listaPokemon.getSelectionModel().selectFirst();
        } else {
            nombrePokemon.setText("NO TENÉS POKÉMON CAPTURADOS");
        }

        return root;
    }

    // =========================
    // CREAR ETIQUETAS
    // =========================

    private Label crearEtiqueta(String texto) {

        Label label = new Label(texto);
        label.getStyleClass().add("texto-retro");

        return label;
    }

    // =========================
    // CAJAS DE ESTADISTICAS
    // =========================

    private VBox crearCajaEstadistica(Label etiqueta) {

        VBox caja = new VBox(etiqueta);

        caja.getStyleClass().add("caja-hud");
        caja.setAlignment(Pos.CENTER);
        caja.setPadding(new Insets(15));
        caja.setMinHeight(75);
        caja.setMaxWidth(Double.MAX_VALUE);

        return caja;
    }

    // =========================
    // MOSTRAR DETALLES
    // =========================

    private void mostrarDetalles(Pokemon pokemon) {

        if (pokemon == null) {
            return;
        }

        nombrePokemon.setText(
                pokemon.getNombre().toUpperCase()
        );

        tipoPokemon.setText(
                "TIPO: " + pokemon.getTipo()
        );

        nivelPokemon.setText(
                "NIVEL: " + pokemon.getNivel()
        );

        experienciaPokemon.setText(
                "EXPERIENCIA: " + pokemon.getExperiencia()
        );

        vidaPokemon.setText(
                "VIDA: " +
                        (int) pokemon.getVida() +
                        " / " +
                        (int) pokemon.getVidaMaxima()
        );

        danioPokemon.setText(
                "DAÑO: " + (int) pokemon.getDanio()
        );

        defensaPokemon.setText(
                "DEFENSA: " + (int) pokemon.getDefensa()
        );

        cargarImagen(pokemon);
    }

    // =========================
    // CARGAR IMAGEN
    // =========================

    private void cargarImagen(Pokemon pokemon) {

        String ruta = "/images/pokemones/frente/" +
                pokemon.getNombre() +
                ".png";

        InputStream recurso = getClass().getResourceAsStream(ruta);

        if (recurso == null) {

            imagenPokemon.setImage(null);

            System.err.println(
                    "No se encontró la imagen: " + ruta
            );

            return;
        }

        imagenPokemon.setImage(new Image(recurso));
    }

    // =========================
    // CSS
    // =========================

    @Override
    protected void configurarEscena(Scene escena) {

        agregarCSS(
                escena,
                "/images/pantallaMenu/estilo.css"
        );
    }
}
