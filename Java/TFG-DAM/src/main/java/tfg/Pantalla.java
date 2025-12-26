package tfg;

import com.badlogic.gdx.Screen;
import tfg.juegos.juego1.Juego1;
import tfg.juegos.juego2.Juego2;
import tfg.juegos.juego3.Juego3;

/**
 * @author Pablo Marcos Sánchez
 */
public class Pantalla implements Screen{
	
	protected Juego1 juego1;
	protected Juego2 juego2;
	protected Juego3 juego3;

	public Pantalla(Juego1 juego1) { 
		this.juego1 = juego1;
	}
	
	public Pantalla(Juego2 juego2) {
		this.juego2 = juego2;
	}
	
	public Pantalla(Juego3 juego3) {
		this.juego3 = juego3;
	}

	@Override
	public void show() {
		
	}

	@Override
	public void render(float delta) {
		
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		
	}

}
