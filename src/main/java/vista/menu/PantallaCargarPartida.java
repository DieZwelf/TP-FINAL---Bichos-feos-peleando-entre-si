
package vista.menu;

import DAO.guardarPartidaDAO;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import vista.GestorPantallas;
import vista.base.PantallaMenuBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PantallaCargarPartida extends PantallaMenuBase {

    private final guardarPartidaDAO partidaDAO =
            new guardarPartidaDAO();

    public PantallaCargarPartida(GestorPantallas pantallas) {
        super(pantallas);
    }

    @Override
    protected void configurarBotones() {

        ocultarEncabezado();

        VBox contenedor = getBotonesBox();

        contenedor.getStyleClass().add("contenedor-guardados");
        contenedor.getChildren().clear();

        // Encabezado.
        VBox encabezado = new VBox(8);
        encabezado.setAlignment(Pos.CENTER);

        Label titulo = new Label("CARGAR PARTIDA");
        titulo.getStyleClass().add("titulo-guardados");

        Label subtitulo = new Label(
                "SELECCIONÁ UNA AVENTURA"
        );
        subtitulo.getStyleClass().add("subtitulo-guardados");

        encabezado.getChildren().addAll(titulo, subtitulo);

        contenedor.getChildren().add(encabezado);

        // Obtener partidas guardadas.
        Map<Integer, String> partidas =
                partidaDAO.obtenerPartidasDisponibles();

        List<Map.Entry<Integer, String>> ordenadas =
                new ArrayList<>(partidas.entrySet());

        // Mostrar primero las más recientes.
        ordenadas.sort((a, b) ->
                Integer.compare(b.getKey(), a.getKey())
        );

        // Crear los tres espacios.
        for (int i = 0; i < 3; i++) {

            VBox tarjeta = new VBox(8);
            tarjeta.getStyleClass().add("slot-guardado");
            tarjeta.setAlignment(Pos.CENTER_LEFT);

            HBox fila = new HBox(20);
            fila.setAlignment(Pos.CENTER_LEFT);

            VBox informacion = new VBox(7);
            informacion.setAlignment(Pos.CENTER_LEFT);

            Label numero = new Label(
                    "ARCHIVO 0" + (i + 1)
            );
            numero.getStyleClass().add("slot-numero");

            informacion.getChildren().add(numero);

            if (i < ordenadas.size()) {

                int idPartida = ordenadas.get(i).getKey();
                String descripcion = ordenadas.get(i).getValue();

                Label detalle = new Label(descripcion);
                detalle.getStyleClass().add("slot-descripcion");
                detalle.setWrapText(true);
                detalle.setMaxWidth(450);

                Label estado = new Label("PARTIDA GUARDADA");
                estado.getStyleClass().add("slot-estado");

                informacion.getChildren().addAll(
                        detalle,
                        estado
                );

                // Botón cargar.
                Button cargar = crearBoton("CARGAR");
                cargar.getStyleClass().add("boton-cargar");

                cargar.setOnAction(evento ->
                        pantallas.cargarPartida(idPartida)
                );

                // Botón eliminar.
                Button eliminar = crearBoton("ELIMINAR");
                eliminar.getStyleClass().add("boton-eliminar");

                eliminar.setOnAction(evento ->
                        confirmarEliminacion(idPartida)
                );

                VBox acciones = new VBox(10, cargar, eliminar);
                acciones.setAlignment(Pos.CENTER);

                fila.getChildren().addAll(
                        informacion,
                        acciones
                );

            } else {

                Label vacio = new Label("SLOT VACÍO");
                vacio.getStyleClass().add("slot-vacio");

                Label detalle = new Label(
                        "NO HAY NINGUNA AVENTURA GUARDADA"
                );
                detalle.getStyleClass().add("slot-vacio");

                informacion.getChildren().addAll(
                        vacio,
                        detalle
                );

                fila.getChildren().add(informacion);
            }

            HBox.setHgrow(informacion, Priority.ALWAYS);

            tarjeta.getChildren().add(fila);

            contenedor.getChildren().add(tarjeta);
        }

        // Botón volver.
        Button volver = crearBoton("VOLVER");
        volver.getStyleClass().add("boton-volver-guardados");

        volver.setOnAction(evento ->
                pantallas.mostrarInicio()
        );

        contenedor.getChildren().add(volver);
    }

    // =====================================================
    // CONFIRMAR ELIMINACIÓN
    // =====================================================

    private void confirmarEliminacion(int jugadorId) {

        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("ELIMINAR PARTIDA");
        confirmacion.setHeaderText(
                "¿ELIMINAR ESTA AVENTURA?"
        );

        confirmacion.setContentText(
                "Esta acción eliminará permanentemente todos " +
                        "los datos de la partida."
        );

        confirmacion.showAndWait().ifPresent(respuesta -> {

            if (respuesta == ButtonType.OK) {

                boolean eliminada =
                        pantallas.eliminarPartida(jugadorId);

                if (eliminada) {

                    pantallas.mostrarCargarPartida();

                } else {

                    Alert error =
                            new Alert(Alert.AlertType.ERROR);

                    error.setTitle("ERROR");
                    error.setHeaderText(
                            "NO SE PUDO ELIMINAR LA PARTIDA"
                    );

                    error.setContentText(
                            "Ocurrió un error al eliminar la aventura."
                    );

                    error.showAndWait();
                }
            }
        });
    }

    // =====================================================
    // CREAR BOTÓN
    // =====================================================

    private Button crearBoton(String texto) {

        Button boton = new Button(texto);
        boton.setFocusTraversable(false);

        return boton;
    }
}
