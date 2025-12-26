package tfg.juegos.juego3;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Pablo Marcos Sánchez
 */
public class Plataforma {
    Rectangle rect;
    boolean movil;
    boolean especial; 
    float velocidad;
    int direccion;
    boolean superada;
    int golpes;
    boolean tocada; 
    float tiempoDestruccion; 
    
    public Plataforma(Rectangle rect, boolean movil, float velocidad) {
        this.rect = rect;
        this.movil = movil;
        this.velocidad = velocidad;
        this.direccion = MathUtils.random() < 0.5 ? 1 : -1;
        this.superada = false;
        this.especial = false; 
        this.golpes = 0;
        this.tocada = false;
        this.tiempoDestruccion = 0;
    }
    
    // Constructor alternativo para compatibilidad
    public Plataforma(Rectangle rect) {
        this(rect, false, 0);
    }
}
