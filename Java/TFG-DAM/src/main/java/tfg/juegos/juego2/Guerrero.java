package tfg.juegos.juego2;

import com.badlogic.gdx.utils.Array;
import tfg.DBHelper;
import tfg.Pantalla;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Pablo Marcos Sánchez
 */
public class Guerrero extends Pantalla {

    public Guerrero(Juego2 juego2) {
        super(juego2);
    }

    private Texture fondo;
    private Array<Texture> heroeTexturas;
    
    //Arrays de textureRegion para el heroe
    private Array<TextureRegion> heroeFramesCorriendo;
    private Array<TextureRegion> heroeFramesParado;
    private Array<TextureRegion> heroeFramesSaltando;
    private Array<TextureRegion> heroeFramesMuriendo;
    private Array<TextureRegion> heroeFramesAtacando;
    
    //Arrays de textureRegion para el duende
    private Array<TextureRegion> duendeFramesCorriendo;
    private Array<TextureRegion> duendeFramesMuriendo;
    private Array<TextureRegion> duendeFramesAtacando;
    
    //Arrays de textureRegion para el esqueleto
    private Array<TextureRegion> esqueletoFramesCorriendo;
    private Array<TextureRegion> esqueletoFramesMuriendo;
    private Array<TextureRegion> esqueletoFramesAtacando; 
    
    //Arrays de textureRegion para el mago
    private Array<TextureRegion> magoFramesCorriendo;
    private Array<TextureRegion> magoFramesMuriendo;
    private Array<TextureRegion> magoFramesAtacando;

    //Animaciones para el heroe
    private Animation<TextureRegion> heroeAnimacionCorriendo;
    private Animation<TextureRegion> heroeAnimacionParado;
    private Animation<TextureRegion> heroeAnimacionSaltando;
    private Animation<TextureRegion> heroeAnimacionMuriendo;
    private Animation<TextureRegion> heroeAnimacionAtacando;
     
    //Animaciones para el duende
    private Animation<TextureRegion> duendeAnimacionCorriendo;
    private Animation<TextureRegion> duendeAnimacionMuriendo;
    private Animation<TextureRegion> duendeAnimacionAtacando;
    
    //Animaciones para el esqueleto
    private Animation<TextureRegion> esqueletoAnimacionCorriendo;
    private Animation<TextureRegion> esqueletoAnimacionMuriendo;
    private Animation<TextureRegion> esqueletoAnimacionAtacando;
    
    //Animaciones para el mago
    private Animation<TextureRegion> magoAnimacionCorriendo;
    private Animation<TextureRegion> magoAnimacionMuriendo;
    private Animation<TextureRegion> magoAnimacionAtacando;
    
    //Colisiones personajes
    private Rectangle rectHeroe;
    private Rectangle rectDuende;
    private Rectangle rectMago;
    private Rectangle rectAtaque;
    private Rectangle rectAtaqueDuende;
    private Rectangle rectAtaqueEsqueleto;
    private Rectangle rectAtaqueMago;
    
    //Limites ventana
    private static final int LIMITE_IZQUIERDO = -20;
    private static final int LIMITE_DERECHO = 820; 
    
    private float duracion = 0;
    private float tiempoAtaqueEnemigo = 0f;
    float tiempoCadaverVisible = 0f;

    //Atributos para el heroe
    private final int ANCHO_HEROE = 100;
    private final int ALTO_HEROE = 55;
    private float xHeroe, yHeroe;
    private boolean mirandoDerecha = true; 
    private final float VELOCIDAD = 200; //Velocidad en píxeles por segundo
    private float velY = 0;             //Velocidad vertical
    private final float GRAVEDAD = -800; 
    private final float FUERZA_SALTO = 400; //Velocidad de salto hacia arriba
    private boolean enSuelo = true;
    private boolean muriendo = false;
    private boolean muerto = false;
    private boolean atacando = false;
    private boolean animacionAtacandoActiva = false;
    private float tiempoMuerte = 0f;
    float escalaHeroe = 1.4f;
    
    //Atributos para el duende
    private final int ANCHO_DUENDE = 38;
    private final int ALTO_DUENDE = 41;
    private float xDuende, yDuende;
    private float VELOCIDAD_DUENDE = 100; //píxeles por segundo
    float escalaDuende = 4f;
    private boolean duendeMuriendo = false;
    private boolean duendeMuerto = false;
    private float tiempoMuerteDuende = 0f;
    private boolean duendeAtacando = false;
    private final float COOLDOWN_ATAQUE_DUENDE = 2.0f;
    private float tiempoDesdeUltimoAtaqueDuende = 0f;
    
    // Variables para el texto de puntuación del duende
    private boolean mostrarPuntuacionDuende = false;
    private float tiempoMostrarPuntuacionDuende = 0f;
    private float opacidadPuntuacionDuende = 1f;
    private float xPuntuacionDuende, yPuntuacionDuende;
    private BitmapFont fuentePuntuacionDuende;

    // Variables para el texto de puntuación del esqueleto
    private boolean mostrarPuntuacionEsqueleto = false;
    private float tiempoMostrarPuntuacionEsqueleto = 0f;
    private float opacidadPuntuacionEsqueleto = 1f;
    private float xPuntuacionEsqueleto, yPuntuacionEsqueleto;
    private BitmapFont fuentePuntuacionEsqueleto;

    // Variables para el texto de puntuación del mago
    private boolean mostrarPuntuacionMago = false;
    private float tiempoMostrarPuntuacionMago = 0f;
    private float opacidadPuntuacionMago = 1f;
    private float xPuntuacionMago, yPuntuacionMago;
    private BitmapFont fuentePuntuacionMago;	
    
    //Atributos para el esqueleto
    private final int ANCHO_ESQUELETO = 38;
    private final int ALTO_ESQUELETO = 41;
    private float xEsqueleto, yEsqueleto;
    private boolean esqueletoActivo = false;
    private boolean esqueletoMuriendo = false;
    private boolean esqueletoMuerto = false;
    private float tiempoMuerteEsqueleto = 0f;
    private float VELOCIDAD_ESQUELETO = 80; 
    private Rectangle rectEsqueleto;
    float escalaEsqueleto = 4.5f;
    private boolean esqueletoAtacando = false;
    private final float COOLDOWN_ATAQUE_ESQUELETO = 3.0f;
    private float tiempoDesdeUltimoAtaqueEsqueleto = 0f;
    
    //Atributos para el mago
    private final int ANCHO_MAGO = 38;
    private final int ALTO_MAGO = 41;
    private float xMago, yMago;
    private boolean magoMuriendo = false;
    private boolean magoMuerto = false;
    private float tiempoMuerteMago = 0f;
    private boolean magoActivo = false;
    private float VELOCIDAD_MAGO = 90; 
    private boolean magoAtacando = false;
    private final float COOLDOWN_ATAQUE_MAGO = 4.0f;
    private float tiempoDesdeUltimoAtaqueMago = 0f;
    boolean puntosSumadosMago = false;
    
    //Atributos para la pantalla GameOver
    private Texture imagenGameOver;
    private BitmapFont fuentePuntuacionGameOver;
    private boolean mostrarGameOver = false;
    private float opacidadGameOver = 0f;
    private Texture imagenReintentar;
    private Texture imagenGuardarPuntuacion;
    float tiempoGameOver = 0f;
    
    //Atributos para la pantalla de pausa
    private float opacidadPausa = 0f;
    private Texture imagenReanudar;
    private boolean juegoPausado = false;

    //Clase para ver las hitbox
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    //Puntuaciones
    private float tiempoAcumulado = 0f;
    private int puntuacionTiempo;
    private int puntuacionEnemigos;
    private final int PUNTOS_DUENDE = 100;
    private final int PUNTOS_ESQUELETO = 200;
    private final int PUNTOS_MAGO = 300;
    boolean puntosSumados = false;
    
    //Textos
    private BitmapFont textoTiempo;
    private BitmapFont fuenteNombreJugador;
    Texture texturaBlanca;
    
    // Variables para el sistema de oleadas
    private BitmapFont fuenteOleadas;
    private String textoOleada;
    private float tiempoMostrarOleada = 0f;
    private boolean mostrarTextoOleada = false;
    private int numeroOleada = 0;
    private int enemigosBasePorOleada = 3;
    private float incrementoEnemigosPorOleada = 0.5f;
    private float velocidadBase = 100f;
    private float incrementoVelocidadPorOleada = 5f;
    private int oleadaParaMago = 3;
    private int oleadaParaEsqueleto = 2;
    private int enemigosRestantesOleada;
    private int enemigosTotalesOleada;
    private float tiempoEntreEnemigos;
    private float tiempoUltimoEnemigo;
    private Random random = new Random();
    
    // Array de TextureRegion para la animación de cargando del héroe
    private Array<TextureRegion> heroeFramesCargando;
    // Animación para cargando
    private Animation<TextureRegion> heroeAnimacionCargando;
    // Estado de la animación de cargando
    private boolean animacionCargandoActiva = false;
    private float tiempoCargando = 0f;
    
    @Override
    public void show() {	
    	fondo = new Texture(Gdx.files.internal("assets/juego2/fondo/fondo_bosque.png")); 
    	
    	// Configuración para el heroe
        heroeTexturas = new Array<>();
        heroeFramesCorriendo = new Array<>();
        heroeFramesParado = new Array<>();
        heroeFramesSaltando = new Array<>(); 
        heroeFramesMuriendo = new Array<>();
        heroeFramesAtacando = new Array<>();

        for (int a = 0; a < 10; a++) {
            Texture textura = new Texture(Gdx.files.internal("assets/juego2/heroe/corriendo/Heroe_Corriendo_" + a + ".png"));
            heroeTexturas.add(textura);
            heroeFramesCorriendo.add(new TextureRegion(textura, 0, 0, ANCHO_HEROE, ALTO_HEROE));
        }

        for (int a = 0; a < 7; a++) {
            Texture textura = new Texture(Gdx.files.internal("assets/juego2/heroe/parado/Heroe_Parado_" + a + ".png"));
            heroeTexturas.add(textura);
            heroeFramesParado.add(new TextureRegion(textura, 0, 0, ANCHO_HEROE, ALTO_HEROE));
        }
        
        for (int a = 0; a < 6; a++) {
            Texture textura = new Texture(Gdx.files.internal("assets/juego2/heroe/atacando/Heroe_Atacando_" + a + ".png"));
            heroeTexturas.add(textura);
            heroeFramesAtacando.add(new TextureRegion(textura, 0, 0, ANCHO_HEROE, ALTO_HEROE));
        }
        
        for (int a = 0; a < 3; a++) {
            Texture textura = new Texture(Gdx.files.internal("assets/juego2/heroe/saltando/Heroe_Saltando_" + a + ".png"));
            heroeTexturas.add(textura);
            heroeFramesSaltando.add(new TextureRegion(textura, 0, 0, ANCHO_HEROE, ALTO_HEROE));
        }
        
        for (int a = 0; a < 10; a++) {
            Texture textura = new Texture(Gdx.files.internal("assets/juego2/heroe/muriendo/Heroe_Muriendo_" + a + ".png"));
            heroeTexturas.add(textura);
            heroeFramesMuriendo.add(new TextureRegion(textura, 0, 0, ANCHO_HEROE, ALTO_HEROE));
        }

        heroeAnimacionCorriendo = new Animation<>(0.08f, heroeFramesCorriendo);
        heroeAnimacionParado = new Animation<>(0.12f, heroeFramesParado);
        heroeAnimacionSaltando = new Animation<>(0.5f, heroeFramesSaltando);
        heroeAnimacionMuriendo = new Animation<>(0.15f, heroeFramesMuriendo);
        heroeAnimacionAtacando = new Animation<>(0.08f, heroeFramesAtacando);

        xHeroe = 100;
        yHeroe = 80;
        
    	// Configuración para el duende
        Texture texturaDuendeCorriendo = new Texture(Gdx.files.internal("assets/juego2/duende/duende_Corriendo.png"));
        int columnasDuende = 8;
        int anchoFrameDuende = texturaDuendeCorriendo.getWidth() / columnasDuende;
        int altoFrameDuende = texturaDuendeCorriendo.getHeight();
        TextureRegion[][] tmpFramesDuendeCorriendo = TextureRegion.split(texturaDuendeCorriendo, anchoFrameDuende, altoFrameDuende);
        duendeFramesCorriendo = new Array<>();
        for(int i = 0; i < 8; i++) {
        	TextureRegion region = tmpFramesDuendeCorriendo[0][i]; 
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            duendeFramesCorriendo.add(region);
        }      
        duendeAnimacionCorriendo = new Animation<>(0.08f, duendeFramesCorriendo);
        
        Texture texturaDuendeMuriendo = new Texture(Gdx.files.internal("assets/juego2/duende/duende_Muriendo.png"));
        int columnasMuerteDuende = 4;
        int anchoFrameMuerte = texturaDuendeMuriendo.getWidth() / columnasMuerteDuende;
        int altoFrameMuerte = texturaDuendeMuriendo.getHeight();
        TextureRegion[][] tmpFramesDuendeMuriendo = TextureRegion.split(texturaDuendeMuriendo, anchoFrameMuerte, altoFrameMuerte);
        duendeFramesMuriendo = new Array<>();
        for (int i = 0; i < columnasMuerteDuende; i++) {
            TextureRegion region = tmpFramesDuendeMuriendo[0][i];
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            duendeFramesMuriendo.add(region);
        }
        duendeAnimacionMuriendo = new Animation<>(0.15f, duendeFramesMuriendo);
        
        Texture texturaDuendeAtacando = new Texture(Gdx.files.internal("assets/juego2/duende/duende_Atacando.png"));
        int columnasDuendeAtaque = 8;
        TextureRegion[][] tmpFramesDuendeAtacando = TextureRegion.split(texturaDuendeAtacando, texturaDuendeAtacando.getWidth() / columnasDuendeAtaque, texturaDuendeAtacando.getHeight());
        duendeFramesAtacando = new Array<>();
        for(int i = 0; i < columnasDuendeAtaque; i++) {
            TextureRegion region = tmpFramesDuendeAtacando[0][i];
            region.flip(true, false);
            duendeFramesAtacando.add(region);
        }
        duendeAnimacionAtacando = new Animation<>(0.08f, duendeFramesAtacando);

	    xDuende = Gdx.graphics.getWidth() + 300;
	    yDuende = 25;
	    
	    // Configuración para el esqueleto
	    Texture texturaEsqueletoCorriendo = new Texture(Gdx.files.internal("assets/juego2/esqueleto/esqueleto_Corriendo.png"));
	    int columnasEsqueleto = 4;
        int anchoFrameEsqueleto = texturaEsqueletoCorriendo.getWidth() / columnasEsqueleto;
        int altoFrameEsqueleto = texturaEsqueletoCorriendo.getHeight();
        TextureRegion[][] tmpFramesEsqueletoCorriendo = TextureRegion.split(texturaEsqueletoCorriendo, anchoFrameEsqueleto, altoFrameEsqueleto);
        esqueletoFramesCorriendo = new Array<>();
        for(int i = 0; i < columnasEsqueleto; i++) {
        	TextureRegion region = tmpFramesEsqueletoCorriendo[0][i]; 
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            esqueletoFramesCorriendo.add(region);
        }   
        esqueletoAnimacionCorriendo = new Animation<>(0.08f, esqueletoFramesCorriendo);
        
	    Texture texturaEsqueletoMuriendo = new Texture(Gdx.files.internal("assets/juego2/esqueleto/esqueleto_Muriendo.png"));
        TextureRegion[][] tmpFramesEsqueletoMuriendo = TextureRegion.split(texturaEsqueletoMuriendo, anchoFrameEsqueleto, altoFrameEsqueleto);
        esqueletoFramesMuriendo = new Array<>();
        for(int i = 0; i < columnasEsqueleto; i++) {
        	TextureRegion region = tmpFramesEsqueletoMuriendo[0][i]; 
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            esqueletoFramesMuriendo.add(region);
        }   
        esqueletoAnimacionMuriendo = new Animation<>(0.08f, esqueletoFramesMuriendo);
        
        Texture texturaEsqueletoAtacando = new Texture(Gdx.files.internal("assets/juego2/esqueleto/esqueleto_Atacando.png"));
        int columnasEsqueletoAtaque = 8; 
        TextureRegion[][] tmpFramesEsqueletoAtacando = TextureRegion.split(texturaEsqueletoAtacando, texturaEsqueletoAtacando.getWidth() / columnasEsqueletoAtaque, texturaEsqueletoAtacando.getHeight());
        esqueletoFramesAtacando = new Array<>();
        for(int i = 0; i < columnasEsqueletoAtaque; i++) {
            TextureRegion region = tmpFramesEsqueletoAtacando[0][i];
            region.flip(true, false); 
            esqueletoFramesAtacando.add(region);
        }
        esqueletoAnimacionAtacando = new Animation<>(0.08f, esqueletoFramesAtacando);
        
        xEsqueleto = Gdx.graphics.getWidth() + 300;
        yEsqueleto = 20;
        
        Texture texturaMagoCorriendo = new Texture(Gdx.files.internal("assets/juego2/mago/mago_Corriendo.png"));
	    int columnasMago = 8;
        int anchoFrameMago = texturaMagoCorriendo.getWidth() / columnasMago;
        int altoFrameMago = texturaMagoCorriendo.getHeight();
        TextureRegion[][] tmpFramesMagoCorriendo = TextureRegion.split(texturaMagoCorriendo, anchoFrameMago, altoFrameMago);
        magoFramesCorriendo = new Array<>();
        for(int i = 0; i < columnasMago; i++) {
        	TextureRegion region = tmpFramesMagoCorriendo[0][i]; 
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            magoFramesCorriendo.add(region);
        }  
        magoAnimacionCorriendo = new Animation<>(0.08f, magoFramesCorriendo);
        
	    Texture texturaMagoMuriendo = new Texture(Gdx.files.internal("assets/juego2/mago/mago_Muriendo.png"));
        TextureRegion[][] tmpFramesMagoMuriendo = TextureRegion.split(texturaMagoMuriendo, anchoFrameMago, altoFrameMago);
        magoFramesMuriendo = new Array<>();
        for(int i = 0; i < 5; i++) {
        	TextureRegion region = tmpFramesMagoMuriendo[0][i]; 
            if (!region.isFlipX()) {
                region.flip(true, false);
            }
            magoFramesMuriendo.add(region);
        }   
        magoAnimacionMuriendo = new Animation<>(0.08f, magoFramesMuriendo);
        
        Texture texturaMagoAtacando = new Texture(Gdx.files.internal("assets/juego2/mago/mago_Atacando.png"));
        int columnasMagoAtaque = 8; 
        TextureRegion[][] tmpFramesMagoAtacando = TextureRegion.split(texturaMagoAtacando, texturaMagoAtacando.getWidth() / columnasMagoAtaque, texturaMagoAtacando.getHeight());
        magoFramesAtacando = new Array<>();
        for(int i = 0; i < columnasMagoAtaque; i++) {
            TextureRegion region = tmpFramesMagoAtacando[0][i];
            region.flip(true, false); 
            magoFramesAtacando.add(region);
        }
        magoAnimacionAtacando = new Animation<>(0.08f, magoFramesAtacando);
        
        xMago = Gdx.graphics.getWidth() + 300;
        yMago = 20;
        
	    rectHeroe = new Rectangle(xHeroe, yHeroe, ANCHO_HEROE * escalaHeroe - 100, ALTO_HEROE * escalaHeroe - 10);
	    rectDuende = new Rectangle(xDuende, yDuende, ANCHO_DUENDE, ALTO_DUENDE * 2.5f);
	    rectEsqueleto = new Rectangle(xEsqueleto, yEsqueleto, ANCHO_ESQUELETO, ALTO_ESQUELETO * 2.5f);
	    rectMago = new Rectangle(xMago, yMago, rectDuende.width, ALTO_MAGO * 2.5f);
	    rectAtaque = new Rectangle(0, 0, 0, 0); 
	    rectAtaqueDuende = new Rectangle(0, 0, 0, 0);
	    rectAtaqueEsqueleto = new Rectangle(0, 0, 0, 0);
	    rectAtaqueMago = new Rectangle(0, 0, 0, 0);
	    
	    imagenGameOver = new Texture(Gdx.files.internal("assets/juego2/imagenesGameOver/HasSidoDerrotado.png"));
	    imagenReintentar = new Texture(Gdx.files.internal("assets/juego2/imagenesGameOver/Reintentar.png"));
	    imagenGuardarPuntuacion = new Texture(Gdx.files.internal("assets/juego2/imagenesGameOver/Guardar_Puntuacion.png"));
	    imagenReanudar = new Texture(Gdx.files.internal("assets/juego2/imagenPausa/Reanudar.png"));
	    textoTiempo = new BitmapFont(); 
	    textoTiempo.setColor(Color.WHITE);
	    textoTiempo.getData().setScale(1.5f);
	    fuenteOleadas = new BitmapFont();
	    fuenteOleadas.setColor(Color.YELLOW);
	    fuenteOleadas.getData().setScale(3f); 
	    mostrarOleada(1);
	    fuentePuntuacionGameOver = new BitmapFont();
	    fuentePuntuacionGameOver.setColor(Color.WHITE);
	    fuentePuntuacionGameOver.getData().setScale(1.5f); 
	    fuenteNombreJugador = new BitmapFont();
	    fuenteNombreJugador.setColor(Color.WHITE);
	    fuenteNombreJugador.getData().setScale(1.2f);
	    texturaBlanca = new Texture(Gdx.files.internal("assets/juego2/fondo/blanco.png"));
	    
	    // Configurar fuente para el texto de puntuación del duende
	    fuentePuntuacionDuende = new BitmapFont();
	    fuentePuntuacionDuende.setColor(Color.GREEN);
	    fuentePuntuacionDuende.getData().setScale(1.5f);
	    
	    // Configurar fuente para el texto de puntuación del esqueleto
	    fuentePuntuacionEsqueleto = new BitmapFont();
	    fuentePuntuacionEsqueleto.setColor(Color.YELLOW);
	    fuentePuntuacionEsqueleto.getData().setScale(1.5f);
	    
	    // Configurar fuente para el texto de puntuación del mago
	    fuentePuntuacionMago = new BitmapFont();
	    fuentePuntuacionMago.setColor(Color.RED);
	    fuentePuntuacionMago.getData().setScale(1.5f);
	    
	    heroeFramesCargando = new Array<>();
	    Texture texturaCargando = new Texture(Gdx.files.internal("assets/juego2/heroe/cargando/cargando.png"));
	    heroeTexturas.add(texturaCargando);
	    int columnasCargando = 6;
	    int anchoFrameCargando = texturaCargando.getWidth() / columnasCargando;
	    int altoFrameCargando = texturaCargando.getHeight();
	    TextureRegion[][] tmpFramesCargando = TextureRegion.split(texturaCargando, anchoFrameCargando, altoFrameCargando);
	    for (int i = 0; i < 20; i++) {
	        TextureRegion region = tmpFramesCargando[0][i % columnasCargando];
	        if (!mirandoDerecha && !region.isFlipX()) {
	            region.flip(true, false);
	        }
	        heroeFramesCargando.add(region);
	    }
	    heroeAnimacionCargando = new Animation<>(0.15f, heroeFramesCargando, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(float delta) {
        if (!juegoPausado && !mostrarGameOver) {
            tiempoAcumulado += delta;
        }
        if (tiempoAcumulado >= 1f && !mostrarGameOver) {
            puntuacionTiempo += 1;
            tiempoAcumulado -= 1f;
        }
        
        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean enMovimiento = izquierda || derecha;
        TextureRegion frameHeroe;

        if (muriendo) {
            velY = 0;
            yHeroe = rectHeroe.y;
            tiempoMuerte += delta;
            frameHeroe = heroeAnimacionMuriendo.getKeyFrame(tiempoMuerte, false);
            if (heroeAnimacionMuriendo.isAnimationFinished(tiempoMuerte)) {
                muerto = true;
            }
            
        } else {
            if (!juegoPausado) {
                if (!atacando) {
                    if (izquierda) {
                        xHeroe -= VELOCIDAD * delta;
                        if (xHeroe < LIMITE_IZQUIERDO) {
                            xHeroe = LIMITE_IZQUIERDO;
                        }
                        if (mirandoDerecha) {
                            voltearFrames(heroeFramesCorriendo);
                            voltearFrames(heroeFramesParado);
                            voltearFrames(heroeFramesSaltando);
                            voltearFrames(heroeFramesAtacando);
                            voltearFrames(heroeFramesCargando);
                            mirandoDerecha = false;
                        }
                    } else if (derecha) {
                        xHeroe += VELOCIDAD * delta;
                        if (xHeroe + ANCHO_HEROE * escalaHeroe > LIMITE_DERECHO) {
                            xHeroe = LIMITE_DERECHO - ANCHO_HEROE * escalaHeroe;
                        }
                        if (!mirandoDerecha) {
                            voltearFrames(heroeFramesCorriendo);
                            voltearFrames(heroeFramesParado);
                            voltearFrames(heroeFramesSaltando);
                            voltearFrames(heroeFramesAtacando);
                            voltearFrames(heroeFramesCargando);
                            mirandoDerecha = true;
                        }
                    }
                }
                if (!atacando && enSuelo && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    velY = FUERZA_SALTO;
                    enSuelo = false;
                }
                velY += GRAVEDAD * delta;
                yHeroe += velY * delta;
                if (yHeroe <= 80) {
                    yHeroe = 80;
                    velY = 0;
                    enSuelo = true;
                }
                if (!juegoPausado && !atacando && !animacionAtacandoActiva && !animacionCargandoActiva) {
                    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        atacando = true;
                        animacionAtacandoActiva = true;
                        duracion = 0f;
                    }
                }
            }
        }

        if (!juegoPausado) {
            duracion += delta;
        }

        if (muriendo) {
            tiempoMuerte += delta;
            frameHeroe = heroeAnimacionMuriendo.getKeyFrame(tiempoMuerte, false);
            if (heroeAnimacionMuriendo.isAnimationFinished(tiempoMuerte)) {
                muerto = true;
                mostrarGameOver = true;
            }
        } else {
            if (atacando) {
                frameHeroe = heroeAnimacionAtacando.getKeyFrame(duracion, false);
                float anchoAtaque = 40;
                float altoAtaque = 80;
                float offsetX = mirandoDerecha ? 100 : -40;
                float ataqueX = xHeroe + offsetX;
                float ataqueY = yHeroe + (ALTO_HEROE * escalaHeroe / 3f);
                rectAtaque.set(ataqueX, ataqueY, anchoAtaque, altoAtaque);
                if (rectAtaque.overlaps(rectDuende) && !duendeMuriendo && !duendeMuerto) {
                    rectDuende.set(0, 0, 0, 0);
                    duendeMuriendo = true;
                    tiempoMuerteDuende = 0f;
                    // Activar texto de puntuación
                    mostrarPuntuacionDuende = true;
                    tiempoMostrarPuntuacionDuende = 0f;
                    opacidadPuntuacionDuende = 1f;
                    xPuntuacionDuende = xDuende + (ANCHO_DUENDE * escalaDuende / 2);
                    yPuntuacionDuende = yDuende + (ALTO_DUENDE * escalaDuende) + 20;
                }
                if (rectAtaque.overlaps(rectEsqueleto) && !esqueletoMuriendo && !esqueletoMuerto) {
                    esqueletoMuriendo = true;
                    tiempoMuerteEsqueleto = 0f;
                    rectEsqueleto.set(0, 0, 0, 0);
                    // Activar texto de puntuación
                    mostrarPuntuacionEsqueleto = true;
                    tiempoMostrarPuntuacionEsqueleto = 0f;
                    opacidadPuntuacionEsqueleto = 1f;
                    xPuntuacionEsqueleto = xEsqueleto + (ANCHO_ESQUELETO * escalaEsqueleto / 2);
                    yPuntuacionEsqueleto = yEsqueleto + (ALTO_ESQUELETO * escalaEsqueleto) + 20;
                }
                if (rectAtaque.overlaps(rectMago) && !magoMuriendo && !magoMuerto) {
                    magoMuriendo = true;
                    tiempoMuerteMago = 0f;
                    puntosSumadosMago = false;
                    rectMago.set(0, 0, 0, 0);
                    // Activar texto de puntuación
                    mostrarPuntuacionMago = true;
                    tiempoMostrarPuntuacionMago = 0f;
                    opacidadPuntuacionMago = 1f;
                    xPuntuacionMago = xMago + (ANCHO_MAGO * escalaEsqueleto / 2);
                    yPuntuacionMago = yMago + (ALTO_MAGO * escalaEsqueleto) + 20;
                }
                if (heroeAnimacionAtacando.isAnimationFinished(duracion)) {
                    atacando = false;
                    animacionAtacandoActiva = false;
                    rectAtaque.set(0, 0, 0, 0);
                    animacionCargandoActiva = true;
                    tiempoCargando = 0f;
                }
            } else {
                rectAtaque.set(0, 0, 0, 0);
                if (!enSuelo) {
                    frameHeroe = heroeAnimacionSaltando.getKeyFrame(duracion, true);
                } else if (enMovimiento) {
                    frameHeroe = heroeAnimacionCorriendo.getKeyFrame(duracion, true);
                } else {
                    frameHeroe = heroeAnimacionParado.getKeyFrame(duracion, true);
                }
            }
            if (animacionCargandoActiva) {
                tiempoCargando += delta;
                fuenteNombreJugador.setColor(Color.RED);
                GlyphLayout layout = new GlyphLayout(fuenteNombreJugador, "RECARGANDO...");
                float x = xHeroe + (ANCHO_HEROE * escalaHeroe / 2) - (layout.width / 2);
                float y = yHeroe + (ALTO_HEROE * escalaHeroe) + 50;
                juego2.batch.begin();
                fuenteNombreJugador.draw(juego2.batch, "RECARGANDO...", x, y);
                juego2.batch.end();
                fuenteNombreJugador.setColor(Color.WHITE);
                if (heroeAnimacionCargando.isAnimationFinished(tiempoCargando)) {
                    animacionCargandoActiva = false;
                }
            }
        }

        TextureRegion frameDuende;
        if (duendeMuriendo) {
            tiempoMuerteDuende += delta;
            frameDuende = duendeAnimacionMuriendo.getKeyFrame(tiempoMuerteDuende, false);
            if (duendeAnimacionMuriendo.isAnimationFinished(tiempoMuerteDuende)) {
                if (!duendeMuerto) {  
                    duendeMuerto = true;
                    puntuacionEnemigos += PUNTOS_DUENDE;
                    tiempoCadaverVisible = 0f;
                }
            }
            if (duendeMuerto) {
                tiempoCadaverVisible += delta;
                if (tiempoCadaverVisible >= 5.0f) {
                    duendeMuerto = false;
                }
            }
        } else if (!duendeMuerto) {
            if (!juegoPausado && !duendeAtacando) {
                xDuende -= VELOCIDAD_DUENDE * delta;
            }
            frameDuende = duendeAnimacionCorriendo.getKeyFrame(duracion, true);
        } else {
            frameDuende = duendeAnimacionMuriendo.getKeyFrame(duendeAnimacionMuriendo.getAnimationDuration() - 0.01f, false);
        }

        if (!duendeMuriendo && !duendeMuerto) {
            tiempoDesdeUltimoAtaqueDuende += delta;
            float distanciaAlDuende = Math.abs(xHeroe - xDuende);
            if (!juegoPausado && !duendeAtacando) {
                frameDuende = duendeAnimacionCorriendo.getKeyFrame(duracion, true);
                xDuende -= VELOCIDAD_DUENDE * delta;
            }
            if (distanciaAlDuende < 100 && tiempoDesdeUltimoAtaqueDuende >= COOLDOWN_ATAQUE_DUENDE && !duendeAtacando) {
                duendeAtacando = true;
                tiempoAtaqueEnemigo = 0f;
                tiempoDesdeUltimoAtaqueDuende = 0f;
            }
            if (duendeAtacando) {
                tiempoAtaqueEnemigo += delta;
                frameDuende = duendeAnimacionAtacando.getKeyFrame(tiempoAtaqueEnemigo, false);
                if (duendeAnimacionAtacando.getKeyFrameIndex(tiempoAtaqueEnemigo) == 3 && !muriendo) {
                    float anchoAtaque = 30;
                    float altoAtaque = 40;
                    float offsetX = -30;
                    float offsetY = 10;
                    rectAtaqueDuende.set(xDuende + offsetX, yDuende + offsetY, anchoAtaque, altoAtaque);
                    if (rectAtaqueDuende.overlaps(rectHeroe)) {
                        muriendo = true;
                        tiempoMuerte = 0f;
                    }
                } else {
                    rectAtaqueDuende.set(0, 0, 0, 0); 
                }
                if (duendeAnimacionAtacando.isAnimationFinished(tiempoAtaqueEnemigo)) {
                    duendeAtacando = false;
                }
            }
            if (!muriendo && rectHeroe.overlaps(rectDuende) && !duendeMuriendo && !duendeMuerto) {
                muriendo = true;
                tiempoMuerte = 0f;
            }
        }
        
        if (!esqueletoActivo && !esqueletoMuerto) {
            boolean duendeCompletamenteMuerto = duendeMuriendo && duendeAnimacionMuriendo.isAnimationFinished(tiempoMuerteDuende);
            boolean duendeSalioDePantalla = !duendeMuerto && !duendeMuriendo && xDuende + ANCHO_DUENDE * escalaDuende < 0;
            if (duendeCompletamenteMuerto || duendeSalioDePantalla) {
                duendeMuerto = true;
                esqueletoActivo = true;
                xEsqueleto = Gdx.graphics.getWidth() + 50;
                yEsqueleto = 25; 
                esqueletoMuriendo = false;
                esqueletoMuerto = false;
                rectEsqueleto.set(xEsqueleto, yEsqueleto, ANCHO_ESQUELETO, ALTO_ESQUELETO * 2.5f);
            }
        }
        
        TextureRegion frameEsqueleto = null;
        if (esqueletoActivo) {
            if (!juegoPausado && esqueletoActivo && !esqueletoMuerto) {
                if (!esqueletoAtacando) {
                    xEsqueleto -= VELOCIDAD_ESQUELETO * delta;
                    rectEsqueleto.setPosition(xEsqueleto + 15, yEsqueleto + 15); 
                }
            }
            if (esqueletoMuriendo) {
                tiempoMuerteEsqueleto += delta;
                frameEsqueleto = esqueletoAnimacionMuriendo.getKeyFrame(tiempoMuerteEsqueleto, false);
                if (esqueletoAnimacionMuriendo.isAnimationFinished(tiempoMuerteEsqueleto)) {
                    if (!esqueletoMuerto) {
                        esqueletoMuerto = true;
                        puntuacionEnemigos += PUNTOS_ESQUELETO;
                    }
                }
            } else {
                frameEsqueleto = esqueletoAnimacionCorriendo.getKeyFrame(duracion, true);
            }
            if (!muriendo && !muerto && rectHeroe.overlaps(rectEsqueleto)) {
                muriendo = true;
                tiempoMuerte = 0f;
            }
        }
        
        if (esqueletoActivo && !esqueletoMuriendo && !esqueletoMuerto) {
            tiempoDesdeUltimoAtaqueEsqueleto += delta;
            float distanciaEsqueleto = Math.abs(xHeroe - xEsqueleto);
            if (!juegoPausado && !esqueletoAtacando) {
                frameEsqueleto = esqueletoAnimacionCorriendo.getKeyFrame(duracion, true);
                xEsqueleto -= VELOCIDAD_ESQUELETO * delta;
            }
            if (distanciaEsqueleto < 90 && tiempoDesdeUltimoAtaqueEsqueleto >= COOLDOWN_ATAQUE_ESQUELETO && !esqueletoAtacando) {
                esqueletoAtacando = true;
                tiempoAtaqueEnemigo = 0f;
                tiempoDesdeUltimoAtaqueEsqueleto = 0f;
            }
            if (esqueletoAtacando) {
                tiempoAtaqueEnemigo += delta;
                frameEsqueleto = esqueletoAnimacionAtacando.getKeyFrame(tiempoAtaqueEnemigo, false);
                if (esqueletoAnimacionAtacando.getKeyFrameIndex(tiempoAtaqueEnemigo) == 3 && !muriendo) {
                    float anchoAtaque = 35;
                    float altoAtaque = 45;
                    float offsetX = -25;
                    rectAtaqueEsqueleto.set(xEsqueleto + offsetX, yEsqueleto, anchoAtaque, altoAtaque);
                    if (rectAtaqueEsqueleto.overlaps(rectHeroe)) {
                        muriendo = true;
                        tiempoMuerte = 0f;
                    }
                } else {
                    rectAtaqueEsqueleto.set(0, 0, 0, 0);
                }
                if (esqueletoAnimacionAtacando.isAnimationFinished(tiempoAtaqueEnemigo)) {
                    esqueletoAtacando = false;
                    tiempoDesdeUltimoAtaqueEsqueleto = -0.5f;
                }
            }
        }
        
        TextureRegion frameMago = null;
        
        if (magoActivo) {
            if (!juegoPausado) {
                if (magoMuriendo) {
                    tiempoMuerteMago += delta;
                    frameMago = magoAnimacionMuriendo.getKeyFrame(tiempoMuerteMago, false);
                    if (magoAnimacionMuriendo.isAnimationFinished(tiempoMuerteMago)) {
                        magoMuerto = true;
                        if (!puntosSumadosMago) {
                            puntuacionEnemigos += PUNTOS_MAGO;
                            puntosSumadosMago = true;
                        }
                        frameMago = magoAnimacionMuriendo.getKeyFrame(tiempoMuerteMago, false);
                        tiempoCadaverVisible += delta;
                    }
                } else if (!magoMuerto) {
                    tiempoDesdeUltimoAtaqueMago += delta;
                    float distanciaMago = Math.abs(xHeroe - xMago);
                    if (magoAtacando) {
                        tiempoAtaqueEnemigo += delta;
                        frameMago = magoAnimacionAtacando.getKeyFrame(tiempoAtaqueEnemigo, false);
                        if (magoAnimacionAtacando.getKeyFrameIndex(tiempoAtaqueEnemigo) == 3 && !muriendo) {
                            float anchoAtaque = 40;
                            float altoAtaque = 50;
                            float offsetX = -35;
                            rectAtaqueMago.set(xMago + offsetX, yMago, anchoAtaque, altoAtaque);
                            if (rectAtaqueMago.overlaps(rectHeroe)) {
                                muriendo = true;
                                tiempoMuerte = 0f;
                            }
                        } else {
                            rectAtaqueMago.set(0, 0, 0, 0);
                        }
                        if (magoAnimacionAtacando.isAnimationFinished(tiempoAtaqueEnemigo)) {
                            magoAtacando = false;
                            frameMago = magoAnimacionCorriendo.getKeyFrame(duracion, true);
                        }
                    } else {
                        frameMago = magoAnimacionCorriendo.getKeyFrame(duracion, true);
                        if (distanciaMago > 100) {
                            xMago -= VELOCIDAD_MAGO * delta;
                        }
                        if (distanciaMago <= 150 && tiempoDesdeUltimoAtaqueMago >= COOLDOWN_ATAQUE_MAGO) {
                            magoAtacando = true;
                            tiempoAtaqueEnemigo = 0f;
                            tiempoDesdeUltimoAtaqueMago = 0f;
                        }
                    }
                    rectMago.setPosition(xMago + 20, yMago + 15);
                    if (!muriendo && rectHeroe.overlaps(rectMago)) {
                        muriendo = true;
                        tiempoMuerte = 0f;
                    }
                }
            }
        }
        
        if (!juegoPausado && enemigosRestantesOleada > 0) {
            tiempoUltimoEnemigo += delta;
            if (tiempoUltimoEnemigo >= tiempoEntreEnemigos) {
                tiempoUltimoEnemigo = 0f;
                generarEnemigo();
                enemigosRestantesOleada--;
            }
        }
        
        if (enemigosRestantesOleada == 0 && !hayEnemigosActivos() && !mostrarTextoOleada) {
            mostrarOleada(numeroOleada + 1);
        }
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juegoPausado = !juegoPausado;
            if (juegoPausado) {
                opacidadPausa = 0f;
            }
        }
        
        // Actualizar texto de puntuación del duende
        if (mostrarPuntuacionDuende) {
            tiempoMostrarPuntuacionDuende += delta;
            yPuntuacionDuende += 50 * delta; // Mover hacia arriba
            opacidadPuntuacionDuende -= delta; // Desvanecer
            if (tiempoMostrarPuntuacionDuende >= 1f) {
                mostrarPuntuacionDuende = false;
                opacidadPuntuacionDuende = 1f;
            }
        }
        
        // Actualizar texto de puntuación del esqueleto
        if (mostrarPuntuacionEsqueleto) {
            tiempoMostrarPuntuacionEsqueleto += delta;
            yPuntuacionEsqueleto += 50 * delta; // Mover hacia arriba
            opacidadPuntuacionEsqueleto -= delta; // Desvanecer
            if (tiempoMostrarPuntuacionEsqueleto >= 1f) {
                mostrarPuntuacionEsqueleto = false;
                opacidadPuntuacionEsqueleto = 1f;
            }
        }
        
        // Actualizar texto de puntuación del mago
        if (mostrarPuntuacionMago) {
            tiempoMostrarPuntuacionMago += delta;
            yPuntuacionMago += 50 * delta; // Mover hacia arriba
            opacidadPuntuacionMago -= delta; // Desvanecer
            if (tiempoMostrarPuntuacionMago >= 1f) {
                mostrarPuntuacionMago = false;
                opacidadPuntuacionMago = 1f;
            }
        }
        
        if (juegoPausado) {
            if (opacidadPausa < 1f) {
                opacidadPausa += Gdx.graphics.getDeltaTime() * 1f;
                if (opacidadPausa > 1f) opacidadPausa = 1f;
            }
            juego2.batch.begin();
            juego2.batch.setColor(1f, 1f, 1f, 1f);
            juego2.batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            juego2.batch.draw(frameHeroe, xHeroe, yHeroe, ANCHO_HEROE * escalaHeroe, ALTO_HEROE * escalaHeroe);
            if (animacionCargandoActiva) {
                TextureRegion frameCargando = heroeAnimacionCargando.getKeyFrame(tiempoCargando, false);
                float escalaCargando = 0.7f;
                float ancho = ANCHO_HEROE * escalaHeroe * escalaCargando;
                float alto = ALTO_HEROE * escalaHeroe * escalaCargando;
                float offsetX = (ANCHO_HEROE * escalaHeroe - ancho) / 2;
                float offsetY = ALTO_HEROE * escalaHeroe + 30;
                juego2.batch.draw(frameCargando, xHeroe + offsetX, yHeroe + offsetY, ancho, alto);
            }
            String nombre = juego2.getNombreUsuario();
            if (nombre != null && !nombre.isEmpty()) {
                GlyphLayout layout = new GlyphLayout();
                layout.setText(fuenteNombreJugador, nombre);
                float nombreAncho = layout.width;
                float nombreAlto = layout.height;
                float nombreX = xHeroe + (ANCHO_HEROE * escalaHeroe / 2) - (nombreAncho / 2);
                float nombreY = yHeroe + (ALTO_HEROE * escalaHeroe) + 20;
                TextureRegion region = new TextureRegion(texturaBlanca);
                juego2.batch.setColor(0, 0, 0, 0.5f);
                juego2.batch.draw(region, nombreX - 5, nombreY - nombreAlto - 2, nombreAncho + 10, nombreAlto + 5);
                juego2.batch.setColor(1, 1, 1, 1);
                fuenteNombreJugador.setColor(Color.WHITE);
                fuenteNombreJugador.draw(juego2.batch, nombre, nombreX, nombreY);
            }
            if (!duendeMuerto || duendeMuriendo) {
                juego2.batch.draw(frameDuende, xDuende, yDuende, ANCHO_DUENDE * escalaDuende, ALTO_DUENDE * escalaDuende);
            }
            if (esqueletoActivo && frameEsqueleto != null) {
                juego2.batch.draw(frameEsqueleto, xEsqueleto, yEsqueleto, ANCHO_ESQUELETO * escalaEsqueleto, ALTO_ESQUELETO * escalaEsqueleto);
            }
            if (magoActivo && frameMago != null) {
                juego2.batch.draw(frameMago, xMago, yMago, ANCHO_MAGO * escalaEsqueleto, ALTO_MAGO * escalaEsqueleto);
            }
            juego2.batch.end();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.5f * opacidadPausa);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            juego2.batch.begin();
            juego2.batch.setColor(1f, 1f, 1f, 0.9f * opacidadPausa);
            float xImg = (Gdx.graphics.getWidth() - imagenReanudar.getWidth()) / 2f;
            float yImg = (Gdx.graphics.getHeight() - imagenReanudar.getHeight()) / 1.5f;
            juego2.batch.draw(imagenReanudar, xImg, yImg - 100);
            juego2.batch.setColor(1f, 1f, 1f, 1f);
            juego2.batch.end();
            if (Gdx.input.justTouched()) {
                float touchX = Gdx.input.getX();
                float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
                Rectangle reanudarRect = new Rectangle(xImg, yImg - 100, imagenReanudar.getWidth(), imagenReanudar.getHeight());
                if (reanudarRect.contains(touchX, touchY)) {
                    juegoPausado = false;
                    opacidadPausa = 0f;
                    return;
                }
            }
            Gdx.gl.glDisable(GL20.GL_BLEND);
            return;
        }

        if (mostrarGameOver) {
            if (opacidadGameOver < 1f) {
                opacidadGameOver += delta * 0.02f;
                if (opacidadGameOver > 1f) {
                    opacidadGameOver = 1f;
                }
            }
            tiempoGameOver += delta;
            duendeMuerto = true;
            Gdx.gl.glEnable(GL20.GL_BLEND);
            juego2.batch.begin();
            juego2.batch.setColor(0f, 0f, 0f, opacidadGameOver);
            juego2.batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            juego2.batch.setColor(1f, 1f, 1f, opacidadGameOver);
            float xImg = (Gdx.graphics.getWidth() - imagenGameOver.getWidth()) / 2f;
            float yImg = (Gdx.graphics.getHeight() - imagenGameOver.getHeight()) / 1.5f;
            juego2.batch.draw(imagenGameOver, xImg, yImg);
            String textoPuntuacion = "Puntuación: " + (puntuacionTiempo + puntuacionEnemigos);
            float xPuntuacion = Gdx.graphics.getWidth()/2 - fuentePuntuacionGameOver.getXHeight() * textoPuntuacion.length()/2;
            float yPuntuacion = yImg - 50;
            fuentePuntuacionGameOver.draw(juego2.batch, textoPuntuacion, xPuntuacion, yPuntuacion + 30);
            juego2.batch.setColor(1f, 1f, 1f, 1f);
            juego2.batch.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            if (tiempoGameOver >= 4.5f) {
                juego2.batch.begin();
                float xImgGameOver = (Gdx.graphics.getWidth() - imagenGameOver.getWidth()) / 2f;
                float yImgGameOver = (Gdx.graphics.getHeight() - imagenGameOver.getHeight()) / 1.5f;
                float reintentarX = xImgGameOver - 50;
                float reintentarY = yImgGameOver - 100;
                float guardarX = xImgGameOver + 190;
                float guardarY = yImgGameOver - 100;
                juego2.batch.draw(imagenReintentar, reintentarX, reintentarY, imagenReintentar.getWidth() * 0.7f, imagenReintentar.getHeight() * 0.5f);
                juego2.batch.draw(imagenGuardarPuntuacion, guardarX, guardarY, imagenGuardarPuntuacion.getWidth() * 0.7f, imagenGuardarPuntuacion.getHeight() * 0.5f);
                juego2.batch.end();
                if (Gdx.input.justTouched()) {
                    float touchX = Gdx.input.getX();
                    float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
                    Rectangle reintentarRect = new Rectangle(reintentarX, reintentarY, imagenReintentar.getWidth() * 0.7f, imagenReintentar.getHeight() * 0.5f);
                    Rectangle guardarRect = new Rectangle(guardarX, guardarY, imagenGuardarPuntuacion.getWidth() * 0.7f, imagenGuardarPuntuacion.getHeight() * 0.5f);
                    if (reintentarRect.contains(touchX, touchY)) {
                        reiniciarJuego();
                    } else if (guardarRect.contains(touchX, touchY)) {
                        guardarPuntuacion();
                        Gdx.app.exit();
                    }
                }
            }
            return;
        }
        
        if (mostrarTextoOleada) {
            if (!juegoPausado && mostrarTextoOleada) {
                tiempoMostrarOleada += delta;
                if (tiempoMostrarOleada > 2f) {
                    mostrarTextoOleada = false;
                }
            }
        }
        
        if ((duendeMuriendo && duendeAnimacionMuriendo.isAnimationFinished(tiempoMuerteDuende)) || 
            (!duendeMuerto && !duendeMuriendo && xDuende + ANCHO_DUENDE * escalaDuende < 0)) {
            if (numeroOleada == 1) {
                mostrarOleada(2);
            }
        }
        
        if (esqueletoActivo && ((esqueletoMuriendo && esqueletoAnimacionMuriendo.isAnimationFinished(tiempoMuerteEsqueleto)) || 
            (!esqueletoMuerto && !esqueletoMuriendo && xEsqueleto + ANCHO_ESQUELETO * escalaEsqueleto < 0))) {
            if (numeroOleada == 2) {
                mostrarOleada(3);
            }
        }

        rectHeroe.setPosition(xHeroe, yHeroe);
        rectDuende.setPosition(xDuende + 15, yDuende);

        juego2.batch.begin();
        juego2.batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        juego2.batch.draw(frameHeroe, xHeroe, yHeroe, ANCHO_HEROE * escalaHeroe, ALTO_HEROE * escalaHeroe);
        
        if (animacionCargandoActiva) {
            TextureRegion frameCargando = heroeAnimacionCargando.getKeyFrame(tiempoCargando, false);
            float escalaCargando = 0.4f;
            float anchoCargando = ANCHO_HEROE * escalaHeroe * escalaCargando;
            float altoCargando = ALTO_HEROE * escalaHeroe * escalaCargando;
            float offsetX = (ANCHO_HEROE * escalaHeroe - anchoCargando) / 2 + 10;
            float offsetY = ALTO_HEROE * escalaHeroe + 30;
            juego2.batch.draw(frameCargando, xHeroe + offsetX, yHeroe + offsetY, anchoCargando * 0.5f, altoCargando * 0.5f);
        }
        
        String nombre = juego2.getNombreUsuario();
        if (nombre != null && !nombre.isEmpty()) {
            GlyphLayout layout = new GlyphLayout();
            layout.setText(fuenteNombreJugador, nombre);
            float nombreAncho = layout.width;
            float nombreAlto = layout.height;
            float nombreX = xHeroe + (ANCHO_HEROE * escalaHeroe / 2) - (nombreAncho / 2);
            float nombreY = yHeroe + (ALTO_HEROE * escalaHeroe) + 20;
            TextureRegion region = new TextureRegion(texturaBlanca);
            juego2.batch.setColor(0, 0, 0, 0.5f);
            juego2.batch.draw(region, nombreX - 5, nombreY - nombreAlto - 2, nombreAncho + 10, nombreAlto + 5);
            juego2.batch.setColor(1, 1, 1, 1);
            fuenteNombreJugador.setColor(Color.WHITE);
            fuenteNombreJugador.draw(juego2.batch, nombre, nombreX, nombreY);
        }
        
        if (!duendeMuerto || duendeMuriendo) {
            juego2.batch.draw(frameDuende, xDuende, yDuende, ANCHO_DUENDE * escalaDuende, ALTO_DUENDE * escalaDuende);
        }
        
        if (esqueletoActivo && frameEsqueleto != null) {
            juego2.batch.draw(frameEsqueleto, xEsqueleto, yEsqueleto, ANCHO_ESQUELETO * escalaEsqueleto, ALTO_ESQUELETO * escalaEsqueleto);
        }
        
        if (magoActivo && frameMago != null) {
            juego2.batch.draw(frameMago, xMago, yMago, ANCHO_MAGO * escalaEsqueleto, ALTO_MAGO * escalaEsqueleto);
        }
        
        // Dibujar texto de puntuación del duende
        if (mostrarPuntuacionDuende) {
            juego2.batch.setColor(1f, 1f, 1f, opacidadPuntuacionDuende);
            GlyphLayout layout = new GlyphLayout(fuentePuntuacionDuende, "+100");
            float textoAncho = layout.width;
            float textoX = xPuntuacionDuende - (textoAncho / 2);
            fuentePuntuacionDuende.draw(juego2.batch, "+100", textoX, yPuntuacionDuende);
            juego2.batch.setColor(1f, 1f, 1f, 1f);
        }
        
        // Dibujar texto de puntuación del esqueleto
        if (mostrarPuntuacionEsqueleto) {
            juego2.batch.setColor(1f, 1f, 1f, opacidadPuntuacionEsqueleto);
            GlyphLayout layout = new GlyphLayout(fuentePuntuacionEsqueleto, "+200");
            float textoAncho = layout.width;
            float textoX = xPuntuacionEsqueleto - (textoAncho / 2);
            fuentePuntuacionEsqueleto.draw(juego2.batch, "+200", textoX, yPuntuacionEsqueleto);
            juego2.batch.setColor(1f, 1f, 1f, 1f);
        }
        
        // Dibujar texto de puntuación del mago
        if (mostrarPuntuacionMago) {
            juego2.batch.setColor(1f, 1f, 1f, opacidadPuntuacionMago);
            GlyphLayout layout = new GlyphLayout(fuentePuntuacionMago, "+300");
            float textoAncho = layout.width;
            float textoX = xPuntuacionMago - (textoAncho / 2);
            fuentePuntuacionMago.draw(juego2.batch, "+300", textoX, yPuntuacionMago);
            juego2.batch.setColor(1f, 1f, 1f, 1f);
        }
        
        textoTiempo.draw(juego2.batch, "Tiempo: " + puntuacionTiempo, 150, Gdx.graphics.getHeight() - 20);
        textoTiempo.draw(juego2.batch, "Puntuación: " + puntuacionEnemigos, 500, Gdx.graphics.getHeight() - 20);
        
        if (mostrarTextoOleada) {
            float x = Gdx.graphics.getWidth() / 2 - fuenteOleadas.getXHeight() * textoOleada.length() / 2;
            float y = Gdx.graphics.getHeight() / 1.2f;
            fuenteOleadas.draw(juego2.batch, textoOleada, x, y);
        }
        
        juego2.batch.end();
    }
    
    private boolean hayEnemigosActivos() {
        boolean duendeActivo = !duendeMuerto && !duendeMuriendo && xDuende + ANCHO_DUENDE * escalaDuende > 0;
        boolean esqueletoActivoActual = esqueletoActivo && !esqueletoMuerto && !esqueletoMuriendo && xEsqueleto + ANCHO_ESQUELETO * escalaEsqueleto > 0;
        boolean magoActivoActual = magoActivo && !magoMuerto && !magoMuriendo && xMago + ANCHO_MAGO * escalaEsqueleto > 0;
        return duendeActivo || esqueletoActivoActual || magoActivoActual;
    }
    
    private void guardarPuntuacion() {
        String nombreUsuario = juego2.getNombreUsuario();
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            int puntuacionTotal = puntuacionTiempo + puntuacionEnemigos;
            DBHelper.insertarPuntuacion(nombreUsuario, "GUERRERO", puntuacionTotal);
        }
    }
    
    private void generarEnemigo() {
        boolean puedeEsqueleto = numeroOleada >= oleadaParaEsqueleto;
        boolean puedeMago = numeroOleada >= oleadaParaMago;
        int tipoEnemigo;
        if (numeroOleada == oleadaParaMago) {
            tipoEnemigo = 2;
        } else if (!puedeEsqueleto && !puedeMago) {
            tipoEnemigo = 0;
        } else {
            float probabilidad = random.nextFloat();
            if (puedeMago) {
                if (probabilidad < 0.3f + (0.05f * numeroOleada)) {
                    tipoEnemigo = 2;
                } else if (probabilidad < 0.6f + (0.03f * numeroOleada)) {
                    tipoEnemigo = 1;
                } else {
                    tipoEnemigo = 0;
                }
            } else if (puedeEsqueleto) {
                if (probabilidad < 0.2f + (0.015f * numeroOleada)) {
                    tipoEnemigo = 1;
                } else {
                    tipoEnemigo = 0;
                }
            } else {
                tipoEnemigo = 0;
            }
        }
        switch (tipoEnemigo) {
            case 0:
                if (duendeMuerto || (xDuende + ANCHO_DUENDE * escalaDuende < 0)) {
                    xDuende = Gdx.graphics.getWidth() + 50;
                    duendeMuerto = false;
                    duendeMuriendo = false;
                    rectDuende.set(xDuende, yDuende, ANCHO_DUENDE, ALTO_DUENDE * 2.5f);
                }
                break;
            case 1:
                if (!esqueletoActivo || esqueletoMuerto || (xEsqueleto + ANCHO_ESQUELETO * escalaEsqueleto < 0)) {
                    esqueletoActivo = true;
                    xEsqueleto = Gdx.graphics.getWidth() + 50;
                    esqueletoMuerto = false;
                    esqueletoMuriendo = false;
                    rectEsqueleto.set(xEsqueleto, yEsqueleto, ANCHO_ESQUELETO, ALTO_ESQUELETO * 2.5f);
                }
                break;
            case 2:
                if (!magoActivo || magoMuerto || (xMago + ANCHO_MAGO * escalaEsqueleto < 0)) {
                    magoActivo = true;
                    xMago = Gdx.graphics.getWidth() + 50;
                    magoMuerto = false;
                    magoMuriendo = false;
                    puntosSumados = false;
                    rectMago.set(xMago, yMago, rectDuende.width, ALTO_MAGO * 2.5f);
                }
                break;
        }
    }

    private void mostrarOleada(int numero) {
        numeroOleada = numero;
        textoOleada = "OLEADA " + numero;
        mostrarTextoOleada = true;
        tiempoMostrarOleada = 0f;
        enemigosTotalesOleada = (int)(enemigosBasePorOleada + (incrementoEnemigosPorOleada * numeroOleada));
        enemigosRestantesOleada = enemigosTotalesOleada;
        tiempoEntreEnemigos = Math.max(0.5f, 2f - (numeroOleada * 0.1f)); 
        VELOCIDAD_DUENDE = velocidadBase + (incrementoVelocidadPorOleada * numeroOleada);
        VELOCIDAD_ESQUELETO = velocidadBase * 0.8f + (incrementoVelocidadPorOleada * numeroOleada);
        VELOCIDAD_MAGO = velocidadBase * 0.9f + (incrementoVelocidadPorOleada * numeroOleada);
        tiempoUltimoEnemigo = 0f;
    }
    
    private void reiniciarJuego() {
    	juegoPausado = false;
        opacidadPausa = 0f;
        tiempoAtaqueEnemigo = 0f;
        xHeroe = 100;
        yHeroe = 80;
        mirandoDerecha = true;
        velY = 0;
        enSuelo = true;
        muriendo = false;
        muerto = false;
        atacando = false;
        animacionAtacandoActiva = false;
        tiempoMuerte = 0f;
        animacionCargandoActiva = false;
        tiempoCargando = 0f;
        rectHeroe.set(xHeroe, yHeroe, ANCHO_HEROE * escalaHeroe - 100, ALTO_HEROE * escalaHeroe - 10);
        xDuende = Gdx.graphics.getWidth() + 300;
        yDuende = 25;
        duendeMuriendo = false;
        duendeMuerto = false;
        duendeAtacando = false;
        tiempoMuerteDuende = 0f;
        rectDuende.set(xDuende, yDuende, ANCHO_DUENDE, ALTO_DUENDE * 2.5f);
        tiempoDesdeUltimoAtaqueDuende = 0f;
        // Reiniciar texto de puntuación del duende
        mostrarPuntuacionDuende = false;
        tiempoMostrarPuntuacionDuende = 0f;
        opacidadPuntuacionDuende = 1f;
        xEsqueleto = Gdx.graphics.getWidth() + 300;
        yEsqueleto = 20;
        esqueletoActivo = false;
        esqueletoMuriendo = false;
        esqueletoMuerto = false;
        esqueletoAtacando = false;
        tiempoMuerteEsqueleto = 0f;
        rectEsqueleto.set(xEsqueleto, yEsqueleto, ANCHO_ESQUELETO, ALTO_ESQUELETO * 2.5f);
        tiempoDesdeUltimoAtaqueEsqueleto = 0f;
        // Reiniciar texto de puntuación del esqueleto
        mostrarPuntuacionEsqueleto = false;
        tiempoMostrarPuntuacionEsqueleto = 0f;
        opacidadPuntuacionEsqueleto = 1f;
        xMago = Gdx.graphics.getWidth() + 300;
        magoActivo = false;
        magoMuriendo = false;
        magoMuerto = false;
        magoAtacando = false;
        tiempoMuerteMago = 0f;
        rectMago.set(0, 0, 0, 0);
        tiempoDesdeUltimoAtaqueMago = 0f;
        // Reiniciar texto de puntuación del mago
        mostrarPuntuacionMago = false;
        tiempoMostrarPuntuacionMago = 0f;
        opacidadPuntuacionMago = 1f;
        rectAtaque.set(0, 0, 0, 0);
        rectAtaqueDuende.set(0, 0, 0, 0);
        rectAtaqueEsqueleto.set(0, 0, 0, 0);
        rectAtaqueMago.set(0, 0, 0, 0);
        mostrarGameOver = false;
        opacidadGameOver = 0f;
        tiempoGameOver = 0f;
        puntuacionTiempo = 0;
        puntuacionEnemigos = 0; 
        tiempoAcumulado = 0f;
        numeroOleada = 0;
        enemigosRestantesOleada = 0;
        enemigosTotalesOleada = 0;
        tiempoEntreEnemigos = 0;
        tiempoUltimoEnemigo = 0;
        mostrarOleada(1);      
    }

    private void voltearFrames(Array<TextureRegion> frames) {
        for (TextureRegion frame : frames) {
            frame.flip(true, false);
        }
    }

    @Override
    public void dispose() {
        for (Texture textura : heroeTexturas) {
            textura.dispose();
        }
        duendeFramesCorriendo.first().getTexture().dispose(); 
        shapeRenderer.dispose();
        imagenGameOver.dispose();
        fuenteOleadas.dispose();
        fuentePuntuacionGameOver.dispose();
        fuenteNombreJugador.dispose();
        fuentePuntuacionDuende.dispose();
        fuentePuntuacionEsqueleto.dispose();
        fuentePuntuacionMago.dispose();
        for (Texture textura : heroeTexturas) {
            textura.dispose();
        }
    }
    
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Juego 2");
        config.setWindowedMode(800, 600);
        config.setWindowIcon("icons/LogoGuerrero.png");
        new Lwjgl3Application(new Juego2(), config);
    }
}