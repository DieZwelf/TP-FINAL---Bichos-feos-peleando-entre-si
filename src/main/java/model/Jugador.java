package model;

public class Jugador {
    private double x;
    private double y;
    private static double velocidad = 3;

    private String direccion; // "ABAJO", "ARRIBA", "IZQUIERDA", "DERECHA"
    private int frameActual;
    private boolean enMovimiento;
    private Entrenador entrenador;
    private TipoJugador tipo;

    public Jugador(double xInicial, double yInicial, Entrenador entrenador, TipoJugador tipo) {
        this.x = xInicial;
        this.y = yInicial;
        this.velocidad = velocidad;
        this.entrenador = entrenador;
        this.direccion = "ABAJO";
        this.frameActual = 0;
        this.enMovimiento = false;
        this.tipo = tipo;
    }

    // Métodos para mover al personaje
    public void moverArriba() { this.y -= velocidad; this.direccion = "ARRIBA"; }
    public void moverAbajo() { this.y += velocidad; this.direccion = "ABAJO"; }
    public void moverIzquierda() { this.x -= velocidad; this.direccion = "IZQUIERDA"; }
    public void moverDerecha() { this.x += velocidad; this.direccion = "DERECHA"; }

    // Getters y Setters
    public double getX() { return x; }
    public double getY() { return y; }

    public TipoJugador getTipo() {
        return tipo;
    }

    public void setTipo(TipoJugador tipo) {
        this.tipo = tipo;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getDireccion() { return direccion; }
    public int getFrameActual() { return frameActual; }
    public void setFrameActual(int frameActual) { this.frameActual = frameActual; }
    public boolean isEnMovimiento() { return enMovimiento; }
    public void setEnMovimiento(boolean enMovimiento) { this.enMovimiento = enMovimiento; }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }
}
