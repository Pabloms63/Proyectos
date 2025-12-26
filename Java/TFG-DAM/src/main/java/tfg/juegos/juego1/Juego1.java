package tfg.juegos.juego1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tfg.DBHelper;

/**
 * @author Pablo Marcos Sánchez
 */
public class Juego1 extends Game {  //Clase lanzadora Juego1
	public SpriteBatch batch;
	private String nombreUsuario;
	
	public Juego1(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
        System.out.println("Nombre recibido en 'Laberinto': " + nombreUsuario); 
        DBHelper.inicializarBaseDatos();
	}
	
	public Juego1() { 
        this("Invitado");
    } 

    @Override
    public void create() {
    	batch = new SpriteBatch();
    	setScreen(new PantallaSplashJuego1(this));
    	
    	System.out.println("Nombre al crear pantalla: " + nombreUsuario); 
    }

	public String getNombreUsuario() {
        return nombreUsuario;
    }
}