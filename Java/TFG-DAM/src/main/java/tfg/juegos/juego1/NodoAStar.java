package tfg.juegos.juego1;

/**
 * Clase auxiliar para el algoritmo de seguimiento de camino de los enemigos.
 */
public class NodoAStar {
    public int fila, columna;
    public double gCost; 
    public double hCost; 
    public double fCost; 
    public NodoAStar padre; 
    
    public NodoAStar(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.gCost = Double.MAX_VALUE;
        this.hCost = 0;
        this.fCost = Double.MAX_VALUE;
        this.padre = null;
    }
}