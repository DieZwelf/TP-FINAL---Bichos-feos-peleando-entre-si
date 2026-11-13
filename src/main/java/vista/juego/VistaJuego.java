
package vista.juego;

import controlador.AdministradorController;
import controlador.JuegoController;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    private static final double ZOOM = 1.4;

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

    private Rectangle transicionInicial;

    // MENÚ DE PAUSA
    private StackPane menuPausa;
    private boolean menuAbierto = false;

    private Label mensajeMenu;
    private Button botonConfirmarSalida;
    private Button botonCancelarSalida;

    // ANIMACIONES DE MENSAJES
    private Timeline temporizadorMensaje;
    private FadeTransition animacionMensaje;

    // PANEL DE ADMINISTRADOR
    private VBox panelAdmin;
    private VBox contenidoAdmin;
    private Label mensajeAdmin;

    // --------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------

    public VistaJuego(Jugador jugador, GestorPantallas pantallas) {
        super(pantallas);

        this.jugador = jugador;

        MapaCasa mapaInicial = new MapaCasa();

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
        root.setFocusTraversable(true);

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
        if (pantallas.isModoAdministrador()) {

            VBox panel = crearPanelAdministrador();

            root.getChildren().add(panel);

            StackPane.setAlignment(panel, Pos.TOP_RIGHT);
            StackPane.setMargin(panel, new Insets(18));
        }

        // MENÚ DE PAUSA
        menuPausa = crearMenuPausa();
        root.getChildren().add(menuPausa);

        root.widthProperty().addListener((obs, antes, ahora) -> {
            recorte.setWidth(ahora.doubleValue());
            actualizarCamara();
        });

        root.heightProperty().addListener((obs, antes, ahora) -> {
            recorte.setHeight(ahora.doubleValue());
            actualizarCamara();
        });

        // CAPA NEGRA PARA LA TRANSICIÓN INICIAL
        transicionInicial = new Rectangle();

        transicionInicial.setFill(Color.BLACK);
        transicionInicial.setMouseTransparent(true);
        transicionInicial.setOpacity(1);

        transicionInicial.widthProperty().bind(root.widthProperty());
        transicionInicial.heightProperty().bind(root.heightProperty());

        root.getChildren().add(transicionInicial);

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

        if (controlador != null && !menuAbierto) {
            controlador.reanudarGameLoop();
        }

        actualizarCamara();
        root.requestFocus();

        if (transicionInicial != null) {

            FadeTransition aparecer = new FadeTransition(
                    Duration.millis(700),
                    transicionInicial
            );

            aparecer.setFromValue(1);
            aparecer.setToValue(0);

            aparecer.setOnFinished(evento -> {

                root.getChildren().remove(transicionInicial);

                transicionInicial.widthProperty().unbind();
                transicionInicial.heightProperty().unbind();

                transicionInicial = null;
                root.requestFocus();
            });

            aparecer.play();
        }
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

        Button menu = new Button("MENÚ");
        menu.getStyleClass().add("boton-retro");

        menu.setOnAction(e -> alternarMenuPausa());

        panel.getChildren().add(menu);

        panel.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        return panel;
    }

    // --------------------------------------------------
    // MENÚ DE PAUSA
    // --------------------------------------------------

    private StackPane crearMenuPausa() {

        StackPane overlay = new StackPane();

        overlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.78);"
        );

        overlay.setPickOnBounds(true);

        VBox contenido = new VBox(20);
        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(35));
        contenido.setMaxWidth(420);

        contenido.setStyle(
                "-fx-background-color: #182329;" +
                        "-fx-border-color: #8fbc45;" +
                        "-fx-border-width: 4;" +
                        "-fx-effect: dropshadow(gaussian, black, 20, 0.4, 0, 5);"
        );

        Label titulo = new Label("MENÚ DE PAUSA");
        titulo.getStyleClass().add("texto-retro");

        titulo.setStyle(
                "-fx-font-size: 25px;" +
                        "-fx-text-fill: #a8d957;"
        );

        Button continuar = new Button("VOLVER AL JUEGO");
        Button guardar = new Button("GUARDAR PARTIDA");
        Button volverMenu = new Button("VOLVER AL MENÚ");

        mensajeMenu = new Label("");
        mensajeMenu.setWrapText(true);
        mensajeMenu.setMaxWidth(350);
        mensajeMenu.setMinHeight(35);
        mensajeMenu.setAlignment(Pos.CENTER);

        mensajeMenu.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #a8d957;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-weight: bold;"
        );

        botonConfirmarSalida = new Button("SÍ, SALIR");
        botonCancelarSalida = new Button("CANCELAR");

        for (Button boton : List.of(
                continuar,
                guardar,
                volverMenu,
                botonConfirmarSalida,
                botonCancelarSalida
        )) {
            boton.getStyleClass().add("boton-retro");
            boton.setMaxWidth(Double.MAX_VALUE);
        }

        continuar.setOnAction(e -> cerrarMenuPausa());

        guardar.setOnAction(e -> guardarPartida());

        volverMenu.setOnAction(e -> confirmarVolverAlMenu());

        botonConfirmarSalida.setOnAction(e -> {
            detenerAnimacionesMensaje();
            pantallas.mostrarInicio();
        });

        botonCancelarSalida.setOnAction(e -> {

            detenerAnimacionesMensaje();

            mensajeMenu.setText("");
            mensajeMenu.setOpacity(1);

            botonConfirmarSalida.setVisible(false);
            botonConfirmarSalida.setManaged(false);

            botonCancelarSalida.setVisible(false);
            botonCancelarSalida.setManaged(false);

            root.requestFocus();
        });

        botonConfirmarSalida.setVisible(false);
        botonConfirmarSalida.setManaged(false);

        botonCancelarSalida.setVisible(false);
        botonCancelarSalida.setManaged(false);

        contenido.getChildren().addAll(
                titulo,
                continuar,
                guardar,
                volverMenu,
                mensajeMenu,
                botonConfirmarSalida,
                botonCancelarSalida
        );

        overlay.getChildren().add(contenido);

        overlay.setVisible(false);
        overlay.setManaged(false);

        return overlay;
    }

    private void alternarMenuPausa() {

        if (menuAbierto) {
            cerrarMenuPausa();
        } else {
            abrirMenuPausa();
        }
    }

    private void abrirMenuPausa() {

        if (menuAbierto || cambiandoMapa || menuPausa == null) {
            return;
        }

        menuAbierto = true;

        if (controlador != null) {
            controlador.detenerGameLoop();
        }

        menuPausa.setManaged(true);
        menuPausa.setVisible(true);

        root.requestFocus();
    }

    private void cerrarMenuPausa() {

        if (!menuAbierto) {
            return;
        }

        menuAbierto = false;

        detenerAnimacionesMensaje();

        mensajeMenu.setText("");
        mensajeMenu.setOpacity(1);

        botonConfirmarSalida.setVisible(false);
        botonConfirmarSalida.setManaged(false);

        botonCancelarSalida.setVisible(false);
        botonCancelarSalida.setManaged(false);

        menuPausa.setVisible(false);
        menuPausa.setManaged(false);

        if (controlador != null) {
            controlador.reanudarGameLoop();
        }

        root.requestFocus();
    }

    // --------------------------------------------------
    // MENSAJES DEL MENÚ
    // --------------------------------------------------

    private void mostrarMensaje(String texto, boolean error) {

        detenerAnimacionesMensaje();

        mensajeMenu.setText(texto);
        mensajeMenu.setOpacity(0);

        mensajeMenu.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " +
                        (error ? "#ff7777" : "#a8d957") + ";" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-weight: bold;"
        );

        animacionMensaje = new FadeTransition(
                Duration.millis(250),
                mensajeMenu
        );

        animacionMensaje.setFromValue(0);
        animacionMensaje.setToValue(1);
        animacionMensaje.play();

        temporizadorMensaje = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {

                    animacionMensaje = new FadeTransition(
                            Duration.millis(350),
                            mensajeMenu
                    );

                    animacionMensaje.setFromValue(1);
                    animacionMensaje.setToValue(0);

                    animacionMensaje.setOnFinished(evento -> {
                        mensajeMenu.setText("");
                        mensajeMenu.setOpacity(1);
                    });

                    animacionMensaje.play();
                })
        );

        temporizadorMensaje.play();
    }

    private void detenerAnimacionesMensaje() {

        if (temporizadorMensaje != null) {
            temporizadorMensaje.stop();
            temporizadorMensaje = null;
        }

        if (animacionMensaje != null) {
            animacionMensaje.stop();
            animacionMensaje = null;
        }
    }

    private void guardarPartida() {

        detenerAnimacionesMensaje();

        try {

            pantallas.guardarPartida(jugador);

            mostrarMensaje(
                    "¡PARTIDA GUARDADA CORRECTAMENTE!",
                    false
            );

        } catch (Exception ex) {

            mostrarMensaje(
                    "ERROR AL GUARDAR LA PARTIDA.",
                    true
            );

            ex.printStackTrace();
        }

        root.requestFocus();
    }

    private void confirmarVolverAlMenu() {

        detenerAnimacionesMensaje();

        mensajeMenu.setText("¿QUERÉS ABANDONAR LA PARTIDA?");
        mensajeMenu.setOpacity(0);

        mensajeMenu.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #e8c66a;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-weight: bold;"
        );

        animacionMensaje = new FadeTransition(
                Duration.millis(300),
                mensajeMenu
        );

        animacionMensaje.setFromValue(0);
        animacionMensaje.setToValue(1);
        animacionMensaje.play();

        botonConfirmarSalida.setVisible(true);
        botonConfirmarSalida.setManaged(true);

        botonCancelarSalida.setVisible(true);
        botonCancelarSalida.setManaged(true);
    }

    // --------------------------------------------------
    // PANEL DE ADMINISTRADOR
    // --------------------------------------------------

    private VBox crearPanelAdministrador() {

        panelAdmin = new VBox(12);

        panelAdmin.setPrefWidth(230);
        panelAdmin.setMinWidth(230);
        panelAdmin.setMaxWidth(230);
        panelAdmin.setPadding(new Insets(12));
        panelAdmin.setSpacing(8);

        panelAdmin.setStyle(
                "-fx-background-color: rgba(24, 35, 41, 0.96);" +
                        "-fx-border-color: #8fbc45;" +
                        "-fx-border-width: 3;"
        );

        Label titulo = new Label("MODO DESARROLLADOR");

        titulo.setStyle(
                "-fx-text-fill: #a8d957;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );

        Button encuentro = crearBotonAdmin("FORZAR ENCUENTRO");
        encuentro.setOnAction(e -> generarEncuentro());

        Button agregarPokemon = crearBotonAdmin("AGREGAR POKÉMON");
        agregarPokemon.setOnAction(e -> mostrarFormularioPokemon());

        Button agregarObjeto = crearBotonAdmin("AGREGAR OBJETO");
        agregarObjeto.setOnAction(e -> mostrarFormularioObjeto());

        Button verPokemon = crearBotonAdmin("VER POKÉMON DISPONIBLES");
        verPokemon.setOnAction(e -> mostrarPokemonesDisponibles());

        Button cambiarMapa = crearBotonAdmin("CAMBIAR MAPA");
        cambiarMapa.setOnAction(e -> mostrarSelectorMapa());

        contenidoAdmin = new VBox(10);
        contenidoAdmin.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(contenidoAdmin);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(260);
        scroll.setMaxHeight(260);
        scroll.setStyle(
                "-fx-background: transparent;" +
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
        );

        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mensajeAdmin = new Label("");
        mensajeAdmin.setWrapText(true);
        mensajeAdmin.setMinHeight(40);
        mensajeAdmin.setMaxWidth(Double.MAX_VALUE);

        mensajeAdmin.setStyle(
                "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #a8d957;"
        );

        panelAdmin.getChildren().addAll(
                titulo,
                new Separator(),
                encuentro,
                agregarPokemon,
                agregarObjeto,
                verPokemon,
                cambiarMapa,
                new Separator(),
                scroll,
                mensajeAdmin
        );

        panelAdmin.setMaxSize(
                Region.USE_PREF_SIZE,
                Region.USE_PREF_SIZE
        );

        return panelAdmin;
    }


    private Button crearBotonAdmin(String texto) {

        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-admin");
        boton.setFocusTraversable(false);

        return boton;
    }


    // --------------------------------------------------
    // ADMINISTRADOR: AGREGAR POKÉMON AL EQUIPO
    // --------------------------------------------------

    private void mostrarFormularioPokemon() {

        contenidoAdmin.getChildren().clear();

        Label titulo = crearTituloAdmin("AGREGAR POKÉMON");

        List<DatosEspecie> especies =
                adminController.obtenerEspeciesDisponibles();

        if (especies == null || especies.isEmpty()) {
            mostrarErrorAdmin("No hay especies disponibles.");
            return;
        }

        ComboBox<DatosEspecie> selector = new ComboBox<>();

        selector.getItems().addAll(especies);
        selector.setMaxWidth(Double.MAX_VALUE);

        selector.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(DatosEspecie especie, boolean vacio) {
                super.updateItem(especie, vacio);

                setText(vacio || especie == null
                        ? null
                        : especie.getNombre());
            }
        });

        selector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(DatosEspecie especie, boolean vacio) {
                super.updateItem(especie, vacio);

                setText(vacio || especie == null
                        ? "Seleccionar Pokémon"
                        : especie.getNombre());
            }
        });

        selector.getSelectionModel().selectFirst();

        Label informacion = new Label(
                "El Pokémon se incorporará directamente a tu equipo."
        );

        informacion.setWrapText(true);
        informacion.setStyle(estiloTextoAdmin());

        Button agregar = crearBotonAdmin("INCORPORAR AL EQUIPO");

        agregar.setOnAction(e -> {

            DatosEspecie especie = selector.getValue();

            if (especie == null) {
                mostrarErrorAdmin("Seleccioná una especie.");
                return;
            }

            List<Pokemon> equipoCompleto = jugador.getEquipoCompleto();

            if (jugador.getEquipo().size() >= 5) {
                mostrarErrorAdmin("El equipo ya tiene cinco Pokémon.");
                return;
            }

            boolean duplicado = equipoCompleto.stream()
                    .anyMatch(p ->
                            p != null &&
                                    p.getNombre().equalsIgnoreCase(especie.getNombre())
                    );

            if (duplicado) {
                mostrarErrorAdmin(
                        "Ese Pokémon ya existe en tu equipo."
                );
                return;
            }

            try {

                Pokemon nuevo = new Pokemon(
                        especie.getNombre(),
                        especie.getVida(),
                        especie.getVida(),
                        especie.getDanio(),
                        especie.getDefensa(),
                        especie.getTipo(),
                        1,
                        0,
                        (int) especie.getDanio(),
                        (int) especie.getDefensa()
                );

                nuevo.setEnEquipo(true);

                // No se utiliza jugador.aniadir(),
                // porque ese método también registra capturados.
                equipoCompleto.add(nuevo);

                if (jugador.getPokemonActivo() == null) {
                    jugador.setPokemonActivo(nuevo);
                }

                mostrarConfirmacionAdmin(
                        nuevo.getNombre() + " se agregó al equipo."
                );

            } catch (Exception ex) {

                mostrarErrorAdmin("No se pudo agregar el Pokémon.");
                ex.printStackTrace();
            }
        });

        contenidoAdmin.getChildren().addAll(
                titulo,
                selector,
                informacion,
                agregar
        );
    }

    // --------------------------------------------------
    // ADMINISTRADOR: AGREGAR OBJETOS
    // --------------------------------------------------

    private void mostrarFormularioObjeto() {

        contenidoAdmin.getChildren().clear();

        Label titulo = crearTituloAdmin("AGREGAR OBJETOS");

        ComboBox<String> selector = new ComboBox<>();

        selector.getItems().addAll("Poción", "Pokébola");
        selector.getSelectionModel().selectFirst();
        selector.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> cantidad = new Spinner<>(1, 99, 1);
        cantidad.setEditable(true);
        cantidad.setMaxWidth(Double.MAX_VALUE);

        Button agregar = crearBotonAdmin("AGREGAR AL INVENTARIO");

        agregar.setOnAction(e -> {

            Inventario inventario = jugador.getInventario();

            if (inventario == null) {
                mostrarErrorAdmin("Inventario no disponible.");
                return;
            }

            int agregados = 0;

            for (int i = 0; i < cantidad.getValue(); i++) {

                boolean resultado;

                if ("Poción".equals(selector.getValue())) {

                    resultado = inventario.agregarObjeto(
                            new Pocion(30)
                    );

                } else {

                    resultado = inventario.agregarObjeto(
                            new Pokebola(
                                    "Pokebola",
                                    "Permite capturar Pokémon salvajes.",
                                    false
                            )
                    );
                }

                if (!resultado) {
                    break;
                }

                agregados++;
            }

            if (agregados == 0) {

                mostrarErrorAdmin(
                        "No se pudieron agregar objetos. Inventario lleno."
                );

            } else {

                mostrarConfirmacionAdmin(
                        "Se agregaron " + agregados + " objetos."
                );
            }
        });

        Label tipo = crearTextoAdmin("TIPO DE OBJETO");
        Label cantidadLabel = crearTextoAdmin("CANTIDAD");

        contenidoAdmin.getChildren().addAll(
                titulo,
                tipo,
                selector,
                cantidadLabel,
                cantidad,
                agregar
        );
    }

    // --------------------------------------------------
    // ADMINISTRADOR: VER ESPECIES
    // --------------------------------------------------

    private void mostrarPokemonesDisponibles() {

        contenidoAdmin.getChildren().clear();

        Label titulo = crearTituloAdmin("POKÉMON DISPONIBLES");

        List<DatosEspecie> especies =
                adminController.obtenerEspeciesDisponibles();

        if (especies == null || especies.isEmpty()) {
            mostrarErrorAdmin("No hay Pokémon disponibles.");
            return;
        }

        VBox lista = new VBox(8);
        lista.setPadding(new Insets(4));

        for (DatosEspecie especie : especies) {

            Label nombre = new Label(especie.getNombre());

            nombre.setStyle(
                    "-fx-text-fill: #a8d957;" +
                            "-fx-font-family: Monospaced;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 12px;"
            );

            Label datos = new Label(
                    "Tipo: " + especie.getTipo() +
                            "\nVida: " + especie.getVida() +
                            " | Daño: " + especie.getDanio() +
                            "\nDefensa: " + especie.getDefensa() +
                            " | Velocidad: " + especie.getVelocidad()
            );

            datos.setWrapText(true);
            datos.setStyle(estiloTextoAdmin());

            VBox tarjeta = new VBox(5, nombre, datos);

            tarjeta.setPadding(new Insets(9));

            tarjeta.setStyle(
                    "-fx-background-color: #263b35;" +
                            "-fx-border-color: #526c45;" +
                            "-fx-border-width: 1;"
            );

            lista.getChildren().add(tarjeta);
        }

        ScrollPane scroll = new ScrollPane(lista);

        scroll.setFitToWidth(true);
        scroll.setPrefHeight(250);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contenidoAdmin.getChildren().addAll(
                titulo,
                new Label("Total: " + especies.size()),
                scroll
        );
    }

    // --------------------------------------------------
    // ADMINISTRADOR: CAMBIAR MAPA
    // --------------------------------------------------

    private void mostrarSelectorMapa() {

        contenidoAdmin.getChildren().clear();

        Label titulo = crearTituloAdmin("SELECCIONAR MAPA");

        Button pueblo = crearBotonAdmin("PUEBLO");
        Button bosque = crearBotonAdmin("BOSQUE");

        pueblo.setOnAction(e -> {

            mostrarConfirmacionAdmin("Cambiando al pueblo...");

            cambiarMapa(
                    new MapaPueblo(),
                    450,
                    150
            );
        });

        bosque.setOnAction(e -> {

            mostrarConfirmacionAdmin("Cambiando al bosque...");

            cambiarMapa(
                    new MapaCasa(),
                    100,
                    500
            );
        });

        contenidoAdmin.getChildren().addAll(
                titulo,
                pueblo,
                bosque
        );
    }

    // --------------------------------------------------
    // MENSAJES DEL ADMINISTRADOR
    // --------------------------------------------------

    private void mostrarConfirmacionAdmin(String mensaje) {

        mensajeAdmin.setText("✓ " + mensaje);

        mensajeAdmin.setStyle(
                "-fx-text-fill: #a8d957;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }

    private void mostrarErrorAdmin(String mensaje) {

        mensajeAdmin.setText("✕ " + mensaje);

        mensajeAdmin.setStyle(
                "-fx-text-fill: #ff7777;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
    }

    private Label crearTituloAdmin(String texto) {

        Label titulo = new Label(texto);

        titulo.setStyle(
                "-fx-text-fill: #a8d957;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        return titulo;
    }

    private Label crearTextoAdmin(String texto) {

        Label label = new Label(texto);

        label.setStyle(estiloTextoAdmin());

        return label;
    }

    private String estiloTextoAdmin() {

        return
                "-fx-text-fill: #dce8ce;" +
                        "-fx-font-family: Monospaced;" +
                        "-fx-font-size: 11px;";
    }

    // --------------------------------------------------
    // ENCUENTROS
    // --------------------------------------------------

    private void generarEncuentro() {

        if (vistaMapa.getCargador() instanceof MapaCasa) {
            mostrarErrorAdmin("No se pueden generar encuentros en este mapa.");
            return;
        }

        if (encuentroEnCurso) {
            return;
        }

        Pokemon pokemonActivo = jugador.getPokemonActivo();

        if (pokemonActivo == null) {
            mostrarErrorAdmin("Necesitás un Pokémon activo.");
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

            if (evento.getCode() == KeyCode.ESCAPE) {

                alternarMenuPausa();
                evento.consume();
                return;
            }

            if (menuAbierto) {
                evento.consume();
                return;
            }

            controlador.manejarTeclaPresionada(evento);

            if (evento.getCode() == KeyCode.TAB) {

                pantallas.mostrarEquipo();
                evento.consume();

            } else if (evento.getCode() == KeyCode.E) {

                pantallas.mostrarInventario();
                evento.consume();

            } else if (evento.getCode() == KeyCode.C) {

                pantallas.mostrarPokemonesCapturados();
                evento.consume();
            }
        });

        escena.addEventFilter(
                KeyEvent.KEY_RELEASED,
                evento -> {

                    if (!menuAbierto) {
                        controlador.manejarTeclaLiberada(evento);
                    }
                }
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
}
