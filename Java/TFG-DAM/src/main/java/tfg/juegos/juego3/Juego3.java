package tfg.juegos.juego3;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tfg.DBHelper;

/**
 * @author Pablo Marcos Sánchez
 */
public class Juego3 extends Game { //Clase lanzadora Juego3
    public SpriteBatch batch;
    private String nombreUsuario;

    public Juego3(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        System.out.println("Nombre recibido en Juego3: " + nombreUsuario); 
        DBHelper.inicializarBaseDatos();
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PantallaSplashJuego3(this));
        
        System.out.println("Nombre al crear pantalla: " + nombreUsuario); 
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) {
            batch.dispose();
        }
    }
} 