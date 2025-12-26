package tfg.juegos.juego2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class PantallaSplashJuego2 implements Screen{
	
	Juego2 juego2;
	private Texture texturaSplash;
	private float tiempoTranscurrido;
	
	public PantallaSplashJuego2(Juego2 juego2) {
		this.juego2 = juego2;
	}
	
	@Override
	public void show() {
		//Definimos la texrtura del logo
		texturaSplash = new Texture("logos/LogoGuerrero.png");
		tiempoTranscurrido = 0;
	}

	@Override
	public void render(float delta) {
		tiempoTranscurrido += delta;
		
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        
        // Dibujar imagen
        juego2.batch.begin();
        
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        float logoWidth = 300;
        float logoHeight = 200;
        float x = (screenWidth - logoWidth) / 2;
        float y = (screenHeight - logoHeight) / 2;
        
        juego2.batch.draw(texturaSplash, x, y, logoWidth, logoHeight);
        juego2.batch.end();
        
        // Cambiamos a la siguiente pantalla después de 3 segundos
        if (tiempoTranscurrido > 3) {
        	juego2.setScreen(new Guerrero(juego2)); // Cambiamos a la pantalla siguiente
            dispose();
        }
        
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		texturaSplash.dispose();
	}

}
