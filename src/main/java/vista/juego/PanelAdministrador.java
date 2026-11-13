
package vista.juego;

import controlador.AdministradorController;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import model.*;
import vista.juego.mapa.CargadorMapaTiled;
import vista.juego.mapa.MapaCasa;
import vista.juego.mapa.MapaPueblo;

import java.util.List;

/**
 * Construye y administra el panel de herramientas del administrador.
 *
 * Permite modificar el equipo y el inventario del jugador,
 * consultar las especies disponibles y cambiar de mapa.
 *
 * @author
 * @since 2026
 * @version 1.0
 */
public class PanelAdministrador {

    private final Jugador jugador;
    private final AdministradorController controlador;
    private final Runnable generarEncuentro;
    private final AccionCambioMapa accionCambioMapa;

    private final VBox panel;
    private final VBox contenido;
    private final Label mensaje;

    /**
     * Acción que permite solicitar un cambio de mapa sin depender
     * directamente de la clase VistaJuego.
     */
    @FunctionalInterface
    public interface AccionCambioMapa {
        void cambiar(CargadorMapaTiled mapa, double x, double y);
    }

    /**
     * Construye el panel del administrador.
     *
     * @param jugador jugador que se está administrando
     * @param generarEncuentro acción para generar un encuentro
     * @param accionCambioMapa acción para cambiar de mapa
     */
    public PanelAdministrador(
            Jugador jugador,
            Runnable generarEncuentro,
            AccionCambioMapa accionCambioMapa
    ) {
        this.jugador = jugador;
        this.generarEncuentro = generarEncuentro;
        this.accionCambioMapa = accionCambioMapa;

        this.controlador = new AdministradorController();
        this.contenido = new VBox(10);
        this.mensaje = new Label();

        this.panel = crearPanel();
    }

    /**
     * Construye el panel principal y sus botones.
     *
     * @return panel de administración
     */
    private VBox crearPanel() {

        VBox contenedor = new VBox(8);
        contenedor.getStyleClass().add("panel-admin");

        contenedor.setPrefWidth(230);
        contenedor.setMinWidth(230);
        contenedor.setMaxWidth(230);
        contenedor.setPadding(new Insets(12));

        Label titulo = new Label("MODO DESARROLLADOR");
        titulo.getStyleClass().add("titulo-admin");

        Button encuentro = crearBoton("FORZAR ENCUENTRO");
        encuentro.setOnAction(e -> generarEncuentro.run());

        Button agregarPokemon = crearBoton("AGREGAR POKÉMON");
        agregarPokemon.setOnAction(e -> mostrarFormularioPokemon());

        Button agregarObjeto = crearBoton("AGREGAR OBJETO");
        agregarObjeto.setOnAction(e -> mostrarFormularioObjeto());

        Button verPokemon = crearBoton("VER POKÉMON DISPONIBLES");
        verPokemon.setOnAction(e -> mostrarPokemonesDisponibles());

        Button cambiarMapa = crearBoton("CAMBIAR MAPA");
        cambiarMapa.setOnAction(e -> mostrarSelectorMapa());

        contenido.setFillWidth(true);

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(260);
        scroll.setMaxHeight(260);
        scroll.getStyleClass().add("scroll-admin");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mensaje.setWrapText(true);
        mensaje.setMinHeight(40);
        mensaje.setMaxWidth(Double.MAX_VALUE);
        mensaje.getStyleClass().add("mensaje-admin");

        contenedor.getChildren().addAll(
                titulo,
                new Separator(),
                encuentro,
                agregarPokemon,
                agregarObjeto,
                verPokemon,
                cambiarMapa,
                new Separator(),
                scroll,
                mensaje
        );

        contenedor.setMaxSize(
                javafx.scene.layout.Region.USE_PREF_SIZE,
                javafx.scene.layout.Region.USE_PREF_SIZE
        );

        return contenedor;
    }

    /**
     * Crea un botón con el estilo definido para administración.
     *
     * @param texto texto del botón
     * @return botón configurado
     */
    private Button crearBoton(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("boton-admin");
        boton.setFocusTraversable(false);
        return boton;
    }

    /**
     * Muestra el formulario para incorporar un Pokémon al equipo.
     */
    private void mostrarFormularioPokemon() {

        contenido.getChildren().clear();

        Label titulo = crearTitulo("AGREGAR POKÉMON");

        List<DatosEspecie> especies =
                controlador.obtenerEspeciesDisponibles();

        if (especies == null || especies.isEmpty()) {
            mostrarError("No hay especies disponibles.");
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
        informacion.getStyleClass().add("texto-admin");

        Button agregar = crearBoton("INCORPORAR AL EQUIPO");

        agregar.setOnAction(e -> agregarPokemonAlEquipo(selector.getValue()));

        contenido.getChildren().addAll(
                titulo,
                selector,
                informacion,
                agregar
        );
    }

    /**
     * Valida e incorpora una especie al equipo del jugador.
     *
     * @param especie especie seleccionada
     */
    private void agregarPokemonAlEquipo(DatosEspecie especie) {

        if (especie == null) {
            mostrarError("Seleccioná una especie.");
            return;
        }

        boolean duplicado = jugador.getEquipoCompleto().stream()
                .anyMatch(p -> p != null
                        && p.getNombre().equalsIgnoreCase(especie.getNombre()));

        if (duplicado) {
            mostrarError("Ese Pokémon ya existe en tu equipo.");
            return;
        }

        if (jugador.getEquipo().size() >= 5) {
            mostrarError("El equipo ya tiene cinco Pokémon.");
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

            if (!jugador.aniadir(nuevo)) {
                mostrarError("No se pudo incorporar el Pokémon.");
                return;
            }

            mostrarConfirmacion(
                    nuevo.getNombre() + " se agregó al equipo."
            );

        } catch (Exception ex) {
            mostrarError("No se pudo agregar el Pokémon.");
            ex.printStackTrace();
        }
    }

    /**
     * Muestra el formulario para agregar objetos al inventario.
     */
    private void mostrarFormularioObjeto() {

        contenido.getChildren().clear();

        Label titulo = crearTitulo("AGREGAR OBJETOS");

        ComboBox<String> selector = new ComboBox<>();
        selector.getItems().addAll("Poción", "Pokébola");
        selector.getSelectionModel().selectFirst();
        selector.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> cantidad = new Spinner<>(1, 99, 1);
        cantidad.setEditable(true);
        cantidad.setMaxWidth(Double.MAX_VALUE);

        Button agregar = crearBoton("AGREGAR AL INVENTARIO");

        agregar.setOnAction(e ->
                agregarObjetos(selector.getValue(), cantidad.getValue())
        );

        contenido.getChildren().addAll(
                titulo,
                crearTexto("TIPO DE OBJETO"),
                selector,
                crearTexto("CANTIDAD"),
                cantidad,
                agregar
        );
    }

    /**
     * Agrega la cantidad solicitada de objetos al inventario.
     *
     * @param tipo tipo de objeto seleccionado
     * @param cantidad cantidad solicitada
     */
    private void agregarObjetos(String tipo, int cantidad) {

        Inventario inventario = jugador.getInventario();

        if (inventario == null) {
            mostrarError("Inventario no disponible.");
            return;
        }

        if (cantidad <= 0) {
            mostrarError("La cantidad debe ser mayor que cero.");
            return;
        }

        Item item;

        if ("Poción".equals(tipo)) {
            item = new Pocion(30);
        } else if ("Pokébola".equals(tipo)) {
            item = new Pokebola(
                    "Pokebola",
                    "Permite capturar Pokémon salvajes.",
                    false
            );
        } else {
            mostrarError("Tipo de objeto no válido.");
            return;
        }

        if (inventario.agregarObjeto(item, cantidad)) {
            mostrarConfirmacion(
                    "Se agregaron " + cantidad + " " + tipo + "(s)."
            );
        } else {
            mostrarError(
                    "No se pudieron agregar los objetos. Inventario lleno."
            );
        }
    }

    /**
     * Muestra las especies disponibles y sus atributos.
     */
    private void mostrarPokemonesDisponibles() {

        contenido.getChildren().clear();

        Label titulo = crearTitulo("POKÉMON DISPONIBLES");

        List<DatosEspecie> especies =
                controlador.obtenerEspeciesDisponibles();

        if (especies == null || especies.isEmpty()) {
            mostrarError("No hay Pokémon disponibles.");
            return;
        }

        VBox lista = new VBox(8);
        lista.setPadding(new Insets(4));

        for (DatosEspecie especie : especies) {

            Label nombre = new Label(especie.getNombre());
            nombre.getStyleClass().add("nombre-especie-admin");

            Label datos = new Label(
                    "Tipo: " + especie.getTipo() +
                            "\nVida: " + especie.getVida() +
                            " | Daño: " + especie.getDanio() +
                            "\nDefensa: " + especie.getDefensa() +
                            " | Velocidad: " + especie.getVelocidad()
            );

            datos.setWrapText(true);
            datos.getStyleClass().add("texto-admin");

            VBox tarjeta = new VBox(5, nombre, datos);
            tarjeta.setPadding(new Insets(9));
            tarjeta.getStyleClass().add("tarjeta-admin");

            lista.getChildren().add(tarjeta);
        }

        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(250);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("scroll-admin");

        Label total = new Label("Total: " + especies.size());
        total.getStyleClass().add("texto-admin");

        contenido.getChildren().addAll(titulo, total, scroll);
    }

    /**
     * Muestra las opciones de mapas disponibles.
     */
    private void mostrarSelectorMapa() {

        contenido.getChildren().clear();

        Label titulo = crearTitulo("SELECCIONAR MAPA");

        Button pueblo = crearBoton("PUEBLO");
        Button bosque = crearBoton("CASA");

        pueblo.setOnAction(e -> {
            mostrarConfirmacion("Cambiando al pueblo...");
            accionCambioMapa.cambiar(new MapaPueblo(), 250, 150);
        });

        // Se conservan las coordenadas originales del bosque.
        bosque.setOnAction(e -> {
            mostrarConfirmacion("Cambiando a la casa...");
            accionCambioMapa.cambiar(new MapaCasa(), 100, 500);
        });

        contenido.getChildren().addAll(titulo, pueblo, bosque);
    }

    /**
     * Muestra un mensaje de confirmación en el panel.
     *
     * @param texto mensaje a mostrar
     */
    private void mostrarConfirmacion(String texto) {
        mensaje.setText("✓ " + texto);
        mensaje.getStyleClass().remove("mensaje-admin-error");

        if (!mensaje.getStyleClass().contains("mensaje-admin-exito")) {
            mensaje.getStyleClass().add("mensaje-admin-exito");
        }
    }

    /**
     * Muestra un mensaje de error en el panel.
     *
     * @param texto mensaje a mostrar
     */
    private void mostrarError(String texto) {
        mensaje.setText("✕ " + texto);
        mensaje.getStyleClass().remove("mensaje-admin-exito");

        if (!mensaje.getStyleClass().contains("mensaje-admin-error")) {
            mensaje.getStyleClass().add("mensaje-admin-error");
        }
    }

    /**
     * Crea un título para los formularios internos.
     *
     * @param texto texto del título
     * @return etiqueta configurada
     */
    private Label crearTitulo(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().add("titulo-admin");
        return titulo;
    }

    /**
     * Crea una etiqueta informativa.
     *
     * @param texto texto de la etiqueta
     * @return etiqueta configurada
     */
    private Label crearTexto(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("texto-admin");
        return label;
    }

    /**
     * Devuelve el panel completo para incorporarlo a la pantalla.
     *
     * @return panel de administración
     */
    public VBox getVista() {
        return panel;
    }

    /**
     * Permite que VistaJuego muestre mensajes de error
     * en el panel de administración.
     *
     * @param texto mensaje de error
     */
    public void mostrarErrorDesdeJuego(String texto) {
        mostrarError(texto);
    }
}
