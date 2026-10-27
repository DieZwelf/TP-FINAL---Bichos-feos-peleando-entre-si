package model;

public class Mapa {

    public static final int PASTO = 0;
    public static final int AGUA = 1;
    public static final int TIERRA = 2;
    public static final int PIEDRA = 3;

    private final int filas;
    private final int columnas;
    private final int[][] terreno;

    public Mapa(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.terreno = new int[filas][columnas];

        generarTerrenoInicial();
    }

    private void generarTerrenoInicial() {
        // El mapa comienza completamente cubierto de pasto.
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                terreno[fila][columna] = PASTO;
            }
        }

        // Creamos una pequeña laguna.
        terreno[3][4] = AGUA;
        terreno[3][5] = AGUA;
        terreno[4][4] = AGUA;
        terreno[4][5] = AGUA;

        // Agregamos una zona de tierra.
        terreno[7][8] = TIERRA;
        terreno[7][9] = TIERRA;
        terreno[8][8] = TIERRA;
    }

    public int getTerreno(int fila, int columna) {
        return terreno[fila][columna];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}