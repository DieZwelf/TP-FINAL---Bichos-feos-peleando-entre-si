package model;

/**
Los posibles estados de un combate visto desde el punto de vista de un Entrenador
 */
public enum EstadoCombate {
    ACTIVO("El combate está en curso"),
    VICTORIA("¡Has ganado el combate!"),
    DERROTA("Has perdido el combate..."),
    HUIDA("Has escapado del combate");

    private final String mensaje;

    EstadoCombate(String mensaje)
    {
        this.mensaje = mensaje;
    }

    public String getMensaje()
    {
        return mensaje;
    }
}
