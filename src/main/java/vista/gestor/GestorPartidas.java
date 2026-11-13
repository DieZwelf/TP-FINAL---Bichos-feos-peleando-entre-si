
package vista.gestor;

import DAO.GuardarPartidaDAO;
import javafx.scene.control.Alert;
import model.Jugador;
import servicio.PartidaService;

/**
 * Gestiona las operaciones relacionadas con las partidas.
 *
 * Se ocupa de crear partidas nuevas y recuperar, guardar
 * o eliminar partidas existentes.
 */
public class GestorPartidas {

    private final GestorPantallas gestorPantallas;
    private final GuardarPartidaDAO partidaDAO;
    private final PartidaService partidaService;

    public GestorPartidas(GestorPantallas gestorPantallas) {

        this.gestorPantallas = gestorPantallas;

        partidaDAO = new GuardarPartidaDAO();
        partidaService = new PartidaService();
    }

    /**
     * Recupera una partida guardada y abre su mapa.
     *
     * @param jugadorId identificador de la partida
     */
    public void cargarPartida(int jugadorId) {

        Jugador jugador = partidaDAO.recuperarJugador(jugadorId);

        if (jugador == null) {
            return;
        }

        jugador.setX(254);
        jugador.setY(177);

        gestorPantallas.abrirMapa(jugador);
    }

    /**
     * Guarda el estado actual de un jugador.
     *
     * @param jugador jugador que se desea guardar
     */
    public void guardarPartida(Jugador jugador) {

        if (jugador == null) {
            return;
        }

        partidaDAO.guardarJugador(jugador);
    }

    /**
     * Elimina una partida guardada.
     *
     * @param jugadorId identificador de la partida
     * @return true si se eliminó correctamente
     */
    public boolean eliminarPartida(int jugadorId) {
        return partidaDAO.eliminarPartida(jugadorId);
    }

    /**
     * Crea una nueva partida si existe espacio disponible.
     */
    public void iniciarPartida() {

        if (!partidaDAO.hayLugarDisponible()) {

            Alert alerta = new Alert(Alert.AlertType.WARNING);

            alerta.setTitle("LÍMITE DE PARTIDAS");
            alerta.setHeaderText("NO HAY ESPACIO DISPONIBLE");

            alerta.setContentText(
                    "Ya existen tres aventuras guardadas. "
                            + "Eliminá una partida para comenzar otra."
            );

            alerta.showAndWait();

            return;
        }

        Jugador jugador = partidaService.crearJugadorInicial(
                gestorPantallas.getTipo()
        );

        jugador.setX(254);
        jugador.setY(177);

        gestorPantallas.abrirMapa(jugador);
    }
}
