package tfg.juegos.juego2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tfg.DBHelper;

/**
 * @author Pablo Marcos Sánchez
 */
public class Juego2 extends Game { //Clase lanzadora Juego2
	public SpriteBatch batch;
	public Stage stage;
	
    private String nombreUsuario;
    
    public Juego2(String nombreUsuario) { 
        this.nombreUsuario = nombreUsuario;
        System.out.println("Nombre recibido en 'Heroe Medieval: " + nombreUsuario); 
        DBHelper.inicializarBaseDatos();
    }
     
    public Juego2() {
        this("Invitado");
    }
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		stage = new Stage(new ScreenViewport());
		setScreen(new PantallaSplashJuego2(this));
		
		System.out.println("Nombre al crear pantalla: " + nombreUsuario); 
	}
	
	public String getNombreUsuario() {
        return nombreUsuario;
    }
}
  