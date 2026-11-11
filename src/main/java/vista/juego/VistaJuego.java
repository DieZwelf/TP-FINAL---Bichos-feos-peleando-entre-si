package vista.juego;

import controlador.AdministradorController;
import controlador.JuegoController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.*;
import vista.GestorPantallas;
import vista.base.PantallaBase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;

/** Pantalla de exploración: compone el mapa y al jugador. */
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
    private AdministradorController adminController = new AdministradorController();

    public VistaJuego(Jugador jugador, GestorPantallas pantallas) {
        super(pantallas);
        this.jugador = jugador;

        MapaPueblo mapaInicial = new MapaPueblo();

        this.vistaMapa = new VistaMapa(mapaInicial);
        this.vistaJugador = new VistaJugador(jugador);

        actualizarTransiciones(mapaInicial);
    }

    @Override
    protected Parent crearContenido() {
        root = new StackPane();
        StackPane.setAlignment(mundo, Pos.TOP_LEFT);
        mundo.getChildren().setAll(vistaMapa, vistaJugador);
        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());
        mundo.getTransforms().setAll(transformacionCamara);

        root.getChildren().setAll(mundo);
        root.setClip(recorte);

        if (jugador.getTipo() == TipoJugador.ADMINISTRADOR) {
            VBox panelAdmin = crearPanelAdministrador();

            root.getChildren().add(panelAdmin);
            root.setAlignment(panelAdmin, Pos.TOP_RIGHT);
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
        agregarCSS(escena,"/images/pantallaMenu/estilo.css");
        this.escena = escena;
        controlador = new JuegoController(jugador, vistaJugador, this, vistaMapa.getColisiones());
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

        panel.getChildren().addAll(titulo, encuentro, agregarPokemon,verPokemon, cambiarMapa);

        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        return panel;
    }

    private void mostrarPokemonesDisponibles() {
        List<DatosEspecie> especies = adminController.obtenerEspeciesDisponibles();

        StringBuilder contenido = new StringBuilder();

        for (DatosEspecie especie : especies) {
            contenido.append(especie.getNombre())
                    .append(" | Tipo: ").append(especie.getTipo())
                    .append(" | Vida: ").append(especie.getVida())
                    .append(" | Daño: ").append(especie.getDanio())
                    .append(" | Defensa: ").append(especie.getDefensa())
                    .append(" | Velocidad: ").append(especie.getVelocidad()).append("\n");
        }

        TextArea texto = new TextArea(contenido.toString());
        texto.setEditable(false);
        texto.setWrapText(true);
        texto.setPrefSize(450, 350);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Administrador");
        alerta.setHeaderText("Pokémon disponibles (" + especies.size() + ")");
        alerta.getDialogPane().setContent(texto);
        alerta.showAndWait();
    }

    private void mostrarSelectorMapa() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Pueblo", "Pueblo", "Bosque");

        dialog.setTitle("Administrador");
        dialog.setHeaderText("Cambiar mapa");
        dialog.setContentText("Seleccioná el destino:");

        dialog.showAndWait().ifPresent(mapa -> {
            switch (mapa) {
                case "Pueblo" -> cambiarMapa(new MapaPueblo(), 450, 150);

                case "Bosque" -> cambiarMapa(new MapaBosque(), 100, 500);
            }
        });
    }

    private void generarEncuentro() {
        Pokemon pokemonActivo = jugador.getEntrenador().getPokemonActivo();
        if (pokemonActivo == null) return;

        double probabilidad = CalculadoraCaptura.calcularProbabilidad(pokemonActivo);

        System.out.println("Vida: " + pokemonActivo.getVida() + "/" + pokemonActivo.getVidaMaxima());
        System.out.println("Probabilidad: " + (probabilidad * 100) + "%");

        int nivelJugador = pokemonActivo.getNivel();
        Pokemon salvaje = GeneradorPokemonSalvaje.generarPokemonSegunNivel(nivelJugador);
        pantallas.iniciarCombate(jugador.getEntrenador(), salvaje);
    }

    public void actualizarCamara() {
        if (escena == null || root == null) return;

        double anchoVentana = escena.getWidth();
        double altoVentana = escena.getHeight();
        if (anchoVentana <= 0 || altoVentana <= 0) return;

        double anchoEscalado = vistaMapa.getPrefWidth() * ZOOM;
        double altoEscalado = vistaMapa.getPrefHeight() * ZOOM;
        double centroX = jugador.getX() + 24;
        double centroY = jugador.getY() + 24;

        double x = anchoVentana / 2 - centroX * ZOOM;
        double y = altoVentana / 2 - centroY * ZOOM;

        x = limitarCamara(x, anchoEscalado, anchoVentana);
        y = limitarCamara(y, altoEscalado, altoVentana);

        transformacionCamara.setToTransform(ZOOM, 0, x, 0, ZOOM, y);
    }

    private double limitarCamara(double posicion, double tamanoMapa, double tamanoVentana) {
        if (tamanoMapa >= tamanoVentana) {
            return Math.max(tamanoVentana - tamanoMapa, Math.min(0, posicion));
        }
        return (tamanoVentana - tamanoMapa) / 2;
    }

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
        escena.addEventFilter(KeyEvent.KEY_RELEASED, controlador::manejarTeclaLiberada);
    }

    public void cambiarMapa(CargadorMapaTiled nuevoMapa, double nuevaX, double nuevaY) {

        VistaMapa nuevoVistaMapa = new VistaMapa(nuevoMapa);

        this.vistaMapa = nuevoVistaMapa;

        jugador.setX(nuevaX);
        jugador.setY(nuevaY);

        mundo.getChildren().setAll(vistaMapa, vistaJugador);

        mundo.setPrefSize(vistaMapa.getPrefWidth(), vistaMapa.getPrefHeight());

        actualizarTransiciones(nuevoMapa);

        if (controlador != null) {
            controlador.setColisiones(vistaMapa.getColisiones());
        }

        actualizarCamara();
    }

    private void configurarTransiciones(CargadorMapaTiled mapa) {

        transiciones.clear();

        if (mapa instanceof MapaPueblo) {

            // Salida del pueblo hacia el bosque.
        transiciones.add(new TransicionMapa(450, 100, 80, 40, MapaBosque::new,100, 500));

        } else if (mapa instanceof MapaBosque) {
            // Regreso del bosque hacia el pueblo.
            transiciones.add(new TransicionMapa(100, 550, 80, 40, MapaPueblo::new,
                    450, 150));
        }
    }

    public void verificarTransicionMapa() {

        for (TransicionMapa transicion : transiciones) {

            if (transicion.jugadorEntro(jugador.getX(), jugador.getY())) {

                cambiarMapa(transicion.getMapaDestino(), transicion.getSpawnX(), transicion.getSpawnY());
                return;
            }
        }
    }

    private void actualizarTransiciones(CargadorMapaTiled mapa) {
        transiciones = mapa.obtenerTransiciones();
    }


    private void mostrarFormularioPokemon()
    {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Administrador");
        dialog.setHeaderText("Registrar nueva especie");

        ButtonType crear = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(crear, ButtonType.CANCEL);

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

            if (resultado != crear) return;

            try {
                if (nombre.getText().isBlank()) {
                    throw new IllegalArgumentException("Ingresá un nombre.");
                }

                adminController.registrarEspecie(nombre.getText().trim(), tipo.getValue(), Double.parseDouble(vida.getText()), Double.parseDouble(danio.getText()),
                        Double.parseDouble(defensa.getText()), Double.parseDouble(velocidad.getText()));

                Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
                confirmacion.setTitle("Administrador");
                confirmacion.setHeaderText(null);
                confirmacion.setContentText("¡Especie registrada correctamente!");
                confirmacion.showAndWait();

            } catch (NumberFormatException ex) {
                mostrarErrorAdmin("Las estadísticas deben ser números válidos.");
            } catch (IllegalArgumentException ex) {
                mostrarErrorAdmin(ex.getMessage());
            }
        });
    }

    private void mostrarErrorAdmin(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText("No se pudo registrar la especie");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
