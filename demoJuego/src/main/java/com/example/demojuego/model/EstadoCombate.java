package com.example.demojuego.model;

/**
 * La enumeración ESTADOCOMBATE tiene como posibilidades ACTIVO, VICTORIA, DERROTA O HUIDA.
 * Tiene un constructor que inicializa un mensaje personalizado para cada estado.
 * Métodos: getter
 * @author Uriel
 * @since 2026
 * @version 1.0
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