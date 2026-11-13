
package vista.juego;

import controlador.AdministradorController;
import controlador.JuegoController;
import excepciones.PokemonInexistenteException;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import model.*;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla de exploración: compone el mapa y al jugador.
 */
public class VistaJuego extends PantallaBase {

    // --------------------------------------------------
    // CONSTANTES
    // --------------------------------------------------

    private static final double ZOOM = 1.4;

    // --------------------------------------------------
    // ATRIBUTOS
    // --------------------------------------------------

    private final Jugador jugador;
    private final VistaJugador vistaJugador;

    private VistaMapa vistaMapa;

    private final Pane mundo = new Pane();
    private final Affine transformacionCamara = new Affine();
    private final Rectangle recorte = new Rectangle();

    private StackPane root;
    private Scene escena;

    private JuegoController controlador;

    private List<TransicionMapa> transiciones = new ArrayList<>();

    private final AdministradorController adminController =
            new AdministradorController();

    private boolean cambiandoMapa = false;
    private boolean encuentroEnCurso = false;

    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------

    public VistaJuego(Jugador jugador, GestorPantallas pantallas) {
        super(pantallas);

        this.jugador = jugador;

        MapaCasa mapaInicial = new MapaCasa();

        jugador.setX(254);
        jugador.setY(177);

        this.vistaMapa = new VistaMapa(mapaInicial);
        this.vistaJugador = new VistaJugador(jugador);

        actualizarTransiciones(mapaInicial);
    }

    // --------------------------------------------------
    // CREACIÓN DE PANTALLA
    // --------------------------------------------------

    @Override
    protected Parent crearContenido() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #514638;");

        StackPane.setAlignment(mundo, Pos.TOP_LEFT);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);
        mundo.setPrefSize(
                vistaMapa.getPrefWidth(),
                vistaMapa.getPrefHeight()
        );

        mundo.getTransforms().setAll(transformacionCamara);

        root.getChildren().setAll(mundo);
        root.setClip(recorte);

        // PANEL DEL JUGADOR
        VBox panelJugador = crearPanelJugador();

        root.getChildren().add(panelJugador);

        StackPane.setAlignment(panelJugador, Pos.TOP_LEFT);
        StackPane.setMargin(panelJugador, new Insets(18));

        // PANEL DEL ADMINISTRADOR
        if (jugador.getTipo() == TipoJugador.ADMINISTRADOR) {
            VBox panelAdmin = crearPanelAdministrador();

            root.getChildren().add(panelAdmin);

            StackPane.setAlignment(panelAdmin, Pos.TOP_RIGHT);
            StackPane.setMargin(panelAdmin, new Insets(18));
        }

        root.widthProperty().addListener((obs, antes, ahora) -> {
            recorte.setWidth(ahora.doubleValue());
            actualizarCamara();
        });

        root.heightProperty().addListener((obs, antes, ahora) -> {
            recorte.setHeight(ahora.doubleValue());
            actualizarCamara();
        });

        return root;
    }

    @Override
    protected void configurarEscena(Scene escena) {
        agregarCSS(escena, "/images/pantallaMenu/estilo.css");

        this.escena = escena;

        controlador = new JuegoController(
                jugador,
                vistaJugador,
                this,
                vistaMapa.getColisiones()
        );

        controlador.setOnEncuentroGenerado(this::generarEncuentro);

        configurarTeclas(escena);
    }

    @Override
    public void alMostrar() {
        if (controlador != null) {
            controlador.reanudarGameLoop();
        }

        actualizarCamara();
        root.requestFocus();
    }

    @Override
    public void alOcultar() {
        if (controlador != null) {
            controlador.detenerGameLoop();
        }
    }

    // --------------------------------------------------
    // PANEL DEL JUGADOR
    // --------------------------------------------------

    private VBox crearPanelJugador() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_LEFT);

        Button guardar = new Button("GUARDAR PARTIDA");
        guardar.getStyleClass().add("boton-retro");

        guardar.setOnAction(e -> {
            pantallas.guardarPartida(jugador);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Partida guardada");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "¡Tu partida se guardó correctamente!"
            );

            alerta.showAndWait();
        });

        panel.getChildren().add(guardar);

        panel.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        return panel;
    }


    // --------------------------------------------------
    // PANEL DE CONTROLES
    // --------------------------------------------------




    // --------------------------------------------------
    // PANEL DE ADMINISTRADOR
    // --------------------------------------------------

    private VBox crearPanelAdministrador() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("panel-admin");

        Label titulo = new Label("MODO DESARROLLADOR");
        titulo.getStyleClass().add("titulo-admin");

        Button encuentro = new Button("Forzar encuentro");
        encuentro.getStyleClass().add("boton-admin");
        encuentro.setOnAction(e -> generarEncuentro());

        Button agregarPokemon = new Button("Agregar Pokémon");
        agregarPokemon.getStyleClass().add("boton-admin");
        agregarPokemon.setOnAction(e -> mostrarFormularioPokemon());

        Button verPokemon = new Button("Ver Pokémon disponibles");
        verPokemon.getStyleClass().add("boton-admin");
        verPokemon.setOnAction(e -> mostrarPokemonesDisponibles());

        Button cambiarMapa = new Button("Cambiar mapa");
        cambiarMapa.getStyleClass().add("boton-admin");
        cambiarMapa.setOnAction(e -> mostrarSelectorMapa());

        panel.getChildren().addAll(
                titulo,
                encuentro,
                agregarPokemon,
                verPokemon,
                cambiarMapa
        );

        panel.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        return panel;
    }

    private void mostrarPokemonesDisponibles() {
        List<DatosEspecie> especies =
                adminController.obtenerEspeciesDisponibles();

        StringBuilder contenido = new StringBuilder();

        for (DatosEspecie especie : especies) {
            contenido.append(especie.getNombre())
                    .append(" | Tipo: ").append(especie.getTipo())
                    .append(" | Vida: ").append(especie.getVida())
                    .append(" | Daño: ").append(especie.getDanio())
                    .append(" | Defensa: ").append(especie.getDefensa())
                    .append(" | Velocidad: ").append(especie.getVelocidad())
                    .append("\n");
        }

        TextArea texto = new TextArea(contenido.toString());
        texto.setEditable(false);
        texto.setWrapText(true);
        texto.setPrefSize(450, 350);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Administrador");
        alerta.setHeaderText(
                "Pokémon disponibles (" + especies.size() + ")"
        );
        alerta.getDialogPane().setContent(texto);
        alerta.showAndWait();
    }

    private void mostrarSelectorMapa() {
        ChoiceDialog<String> dialog =
                new ChoiceDialog<>("Pueblo", "Pueblo", "Bosque");

        dialog.setTitle("Administrador");
        dialog.setHeaderText("Cambiar mapa");
        dialog.setContentText("Seleccioná el destino:");

        dialog.showAndWait().ifPresent(mapa -> {
            switch (mapa) {
                case "Pueblo" ->
                        cambiarMapa(new MapaPueblo(), 450, 150);

                case "Bosque" ->
                        cambiarMapa(new MapaCasa(), 100, 500);
            }
        });
    }

    // --------------------------------------------------
    // ENCUENTROS
    // --------------------------------------------------

    private void generarEncuentro() {

        // Evitar encuentros dentro de la casa.
        if (vistaMapa.getCargador() instanceof MapaCasa) {
            return;
        }

        if (encuentroEnCurso) {
            return;
        }

        Pokemon pokemonActivo = jugador.getPokemonActivo();

        if (pokemonActivo == null) {
            return;
        }

        double probabilidad =
                CalculadoraCaptura.calcularProbabilidad(pokemonActivo);

        System.out.println(
                "Vida: " + pokemonActivo.getVida()
                        + "/" + pokemonActivo.getVidaMaxima()
        );

        System.out.println(
                "Probabilidad: " + (probabilidad * 100) + "%"
        );

        int nivelJugador = pokemonActivo.getNivel();

        Pokemon salvaje =
                GeneradorPokemonSalvaje.generarPokemonSegunNivel(
                        nivelJugador
                );

        encuentroEnCurso = true;

        // El GestorPantallas se encarga de ejecutar la animación.
        pantallas.iniciarCombate(jugador, salvaje);
    }

    /**
     * Transición de encuentro:
     * parpadeo blanco y aparición del mensaje retro.
     */
    public void animarEncuentro(Runnable alTerminar) {
        if (controlador != null) {
            controlador.detenerGameLoop();
        }

        double ancho = root.getWidth();
        double alto = root.getHeight();

        Pane capaTransicion = new Pane();
        capaTransicion.setPrefSize(ancho, alto);
        capaTransicion.setMouseTransparent(true);

        root.getChildren().add(capaTransicion);

        // Parpadeo blanco inicial.
        Rectangle flash = new Rectangle(
                ancho,
                alto,
                Color.WHITE
        );

        flash.setOpacity(0);
        capaTransicion.getChildren().add(flash);

        Timeline parpadeo = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(flash.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(50),
                        new KeyValue(flash.opacityProperty(), 0.95)
                ),
                new KeyFrame(
                        Duration.millis(110),
                        new KeyValue(flash.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.millis(160),
                        new KeyValue(flash.opacityProperty(), 0.9)
                ),
                new KeyFrame(
                        Duration.millis(230),
                        new KeyValue(flash.opacityProperty(), 0)
                )
        );

        parpadeo.setOnFinished(evento -> {
            capaTransicion.getChildren().remove(flash);

            mostrarMensajeEncuentro(
                    capaTransicion,
                    ancho,
                    alto,
                    alTerminar
            );
        });

        parpadeo.play();
    }

    /**
     * Muestra el mensaje sobre el mapa, sin fondo negro.
     */
    private void mostrarMensajeEncuentro(
            Pane capaTransicion,
            double ancho,
            double alto,
            Runnable alTerminar
    ) {
        StackPane cartel = new StackPane();
        cartel.setMaxSize(580, 110);

        cartel.setStyle(
                "-fx-background-color: #F8F8F8;" +
                        "-fx-border-color: #202020;" +
                        "-fx-border-width: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 4);"
        );

        Label mensaje = new Label(
                "¡Un Pokémon salvaje apareció!"
        );

        mensaje.setFont(
                Font.font("Monospaced", FontWeight.BOLD, 22)
        );

        mensaje.setTextFill(Color.web("#202020"));

        cartel.getChildren().add(mensaje);

        StackPane contenedor = new StackPane(cartel);
        contenedor.setPrefSize(ancho, alto);

        StackPane.setAlignment(cartel, Pos.BOTTOM_CENTER);

        StackPane.setMargin(
                cartel,
                new Insets(0, 0, 50, 0)
        );

        capaTransicion.getChildren().setAll(contenedor);

        FadeTransition aparecer = new FadeTransition(
                Duration.millis(250),
                cartel
        );

        aparecer.setFromValue(0);
        aparecer.setToValue(1);

        aparecer.setOnFinished(evento -> {
            PauseTransition pausa = new PauseTransition(
                    Duration.millis(1200)
            );

            pausa.setOnFinished(fin -> {
                root.getChildren().remove(capaTransicion);
                alTerminar.run();
            });

            pausa.play();
        });

        aparecer.play();
    }

    // --------------------------------------------------
    // CÁMARA
    // --------------------------------------------------

    public void actualizarCamara() {
        if (escena == null || root == null) {
            return;
        }

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();

        if (anchoVentana <= 0 || altoVentana <= 0) {
            return;
        }

        double anchoEscalado = vistaMapa.getPrefWidth() * ZOOM;
        double altoEscalado = vistaMapa.getPrefHeight() * ZOOM;

        double centroX = jugador.getX() + 24;
        double centroY = jugador.getY() + 24;

        double x = anchoVentana / 2 - centroX * ZOOM;
        double y = altoVentana / 2 - centroY * ZOOM;

        x = limitarCamara(x, anchoEscalado, anchoVentana);
        y = limitarCamara(y, altoEscalado, altoVentana);

        transformacionCamara.setToTransform(
                ZOOM, 0, x,
                0, ZOOM, y
        );
    }

    private double limitarCamara(
            double posicion,
            double tamanoMapa,
            double tamanoVentana
    ) {
        if (tamanoMapa >= tamanoVentana) {
            return Math.max(
                    tamanoVentana - tamanoMapa,
                    Math.min(0, posicion)
            );
        }

        return (tamanoVentana - tamanoMapa) / 2;
    }

    // --------------------------------------------------
    // CONTROLES
    // --------------------------------------------------

    private void configurarTeclas(Scene escena) {
        escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            controlador.manejarTeclaPresionada(evento);

            if (evento.getCode() == KeyCode.TAB) {
                pantallas.mostrarEquipo();
                evento.consume();

            } else if (evento.getCode() == KeyCode.E) {
                pantallas.mostrarInventario();
                evento.consume();
            }
        });

        escena.addEventFilter(
                KeyEvent.KEY_RELEASED,
                controlador::manejarTeclaLiberada
        );
    }

    // --------------------------------------------------
    // CAMBIO DE MAPA
    // --------------------------------------------------

    public void cambiarMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY
    ) {
        if (cambiandoMapa) {
            return;
        }

        cambiandoMapa = true;

        if (controlador != null) {
            controlador.detenerGameLoop();
        }

        Rectangle pantallaNegra = new Rectangle();

        pantallaNegra.setFill(Color.BLACK);
        pantallaNegra.setMouseTransparent(true);
        pantallaNegra.setOpacity(0);

        pantallaNegra.widthProperty().bind(root.widthProperty());
        pantallaNegra.heightProperty().bind(root.heightProperty());

        root.getChildren().add(pantallaNegra);

        FadeTransition oscurecer = new FadeTransition(
                Duration.millis(250),
                pantallaNegra
        );

        oscurecer.setFromValue(0);
        oscurecer.setToValue(1);

        oscurecer.setOnFinished(evento -> {
            aplicarCambioMapa(nuevoMapa, nuevaX, nuevaY);

            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(250),
                    pantallaNegra
            );

            aparecer.setFromValue(1);
            aparecer.setToValue(0);

            aparecer.setOnFinished(fin -> {
                root.getChildren().remove(pantallaNegra);

                pantallaNegra.widthProperty().unbind();
                pantallaNegra.heightProperty().unbind();

                cambiandoMapa = false;

                if (controlador != null) {
                    controlador.reanudarGameLoop();
                }

                root.requestFocus();
            });

            aparecer.play();
        });

        oscurecer.play();
    }

    private void aplicarCambioMapa(
            CargadorMapaTiled nuevoMapa,
            double nuevaX,
            double nuevaY
    ) {
        VistaMapa nuevoVistaMapa = new VistaMapa(nuevoMapa);

        this.vistaMapa = nuevoVistaMapa;

        jugador.setX(nuevaX);
        jugador.setY(nuevaY);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);

        mundo.setPrefSize(
                vistaMapa.getPrefWidth(),
                vistaMapa.getPrefHeight()
        );

        actualizarTransiciones(nuevoMapa);

        if (controlador != null) {
            controlador.setColisiones(vistaMapa.getColisiones());
        }

        actualizarCamara();
    }

    private void actualizarTransiciones(CargadorMapaTiled mapa) {
        transiciones = mapa.obtenerTransiciones();
    }

    public void verificarTransicionMapa() {
        for (TransicionMapa transicion : transiciones) {
            if (transicion.jugadorEntro(
                    jugador.getX(),
                    jugador.getY()
            )) {
                cambiarMapa(
                        transicion.getMapaDestino(),
                        transicion.getSpawnX(),
                        transicion.getSpawnY()
                );

                return;
            }
        }
    }

    // --------------------------------------------------
    // ADMINISTRADOR: REGISTRAR POKÉMON
    // --------------------------------------------------

    private void mostrarFormularioPokemon() {
        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Administrador");
        dialog.setHeaderText("Registrar nueva especie");

        ButtonType crear = new ButtonType(
                "Registrar",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                crear,
                ButtonType.CANCEL
        );

        TextField nombre = new TextField();
        nombre.setPromptText("Nombre");

        ComboBox<TipoPokemon> tipo = new ComboBox<>();
        tipo.getItems().setAll(TipoPokemon.values());
        tipo.getSelectionModel().selectFirst();

        TextField vida = new TextField("50");
        TextField danio = new TextField("12");
        TextField defensa = new TextField("5");
        TextField velocidad = new TextField("10");

        GridPane formulario = new GridPane();

        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(15));

        formulario.addRow(0, new Label("Nombre:"), nombre);
        formulario.addRow(1, new Label("Tipo:"), tipo);
        formulario.addRow(2, new Label("Vida:"), vida);
        formulario.addRow(3, new Label("Daño:"), danio);
        formulario.addRow(4, new Label("Defensa:"), defensa);
        formulario.addRow(5, new Label("Velocidad:"), velocidad);

        dialog.getDialogPane().setContent(formulario);

        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado != crear) {
                return;
            }

            try {
                if (nombre.getText().isBlank()) {
                    throw new IllegalArgumentException(
                            "Ingresá un nombre."
                    );
                }

                boolean existe = GeneradorPokemonSalvaje
                        .getESPECIES()
                        .stream()
                        .anyMatch(p ->
                                p.getNombre().equalsIgnoreCase(
                                        nombre.getText().trim()
                                )
                        );

                if (!existe) {
                    throw new PokemonInexistenteException(
                            "El nombre no se encuentra en el sistema."
                    );
                }

                adminController.registrarEspecie(
                        nombre.getText().trim(),
                        tipo.getValue(),
                        Double.parseDouble(vida.getText()),
                        Double.parseDouble(danio.getText()),
                        Double.parseDouble(defensa.getText()),
                        Double.parseDouble(velocidad.getText())
                );

                Alert confirmacion = new Alert(
                        Alert.AlertType.INFORMATION
                );

                confirmacion.setTitle("Administrador");
                confirmacion.setHeaderText(null);
                confirmacion.setContentText(
                        "¡Especie registrada correctamente!"
                );

                confirmacion.showAndWait();

            } catch (PokemonInexistenteException ex) {
                System.err.println(
                        "Ha ocurrido un error: " + ex.getMessage()
                );
                ex.printStackTrace();

            } catch (NumberFormatException ex) {
                mostrarErrorAdmin(
                        "Las estadísticas deben ser números válidos."
                );

            } catch (IllegalArgumentException ex) {
                mostrarErrorAdmin(ex.getMessage());
            }
        });
    }

    private void mostrarErrorAdmin(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle("Error");
        alerta.setHeaderText(
                "No se pudo registrar la especie"
        );
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
