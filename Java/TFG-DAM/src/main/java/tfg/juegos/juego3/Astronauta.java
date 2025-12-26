package tfg.juegos.juego3; 

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import tfg.DBHelper;
import tfg.Pantalla; 

/**
 * @author Pablo Marcos Sánchez
 */
public class Astronauta extends Pantalla {
    private OrthographicCamera camara;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer; 
    
    //Texturas
    private Texture texturaJugador, texturaPlataforma, texturaFondo;
    private Texture texturaFinPartida, texturaGuadarPunt, texturaReiniciar;
    
    // Texturas de fondo
    private Texture[] fondos;
    private int fondoActual = 0;
    
    //Hitbox del jugador
    private Rectangle player;
    
    //Array de plataformas
    private Array<Plataforma> platforms = new Array<>();
    
    private float gravedad = -20f;
    private float velocidadSalto = 0;
    private float velocidadJugador = 5f;
    
    private String nombreUsuario;
    
    private static final int ANCHO_PLATAFORMA = 70;
    private static final int ALTURA_PLATAFORMA = 20;
    private static final int ANCHO_JUGADOR = 64;
    private static final int ALTURA_JUGADOR = 64;
    
    private boolean mirandoDerecha = true; 
    
    //Puntuación
    private int puntuacionTotal;
    private BitmapFont puntuacion; 
    
    //Tiempo
    private float tiempoAcumulado = 0f;
    private int puntuacionTiempo;
    private BitmapFont tiempo;
    
    //Atributos para la pantalla de pausa
    private float opacidadPausa = 0f;
    private Texture imagenReanudar;
    private boolean juegoPausado = false;
    
    private boolean gameOver = false;
    
    private Texture texturaPlataformaRompible1;
    private Texture texturaPlataformaRompible2; 
    private Texture texturaPlataformaRompible3;

    public Astronauta(Juego3 juego3) {
        super(juego3);
        this.nombreUsuario = juego3.getNombreUsuario();
    }
    
    @Override
    public void show() {
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 800, 480);
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // Cargar texturas (rutas corregidas)
        texturaJugador = new Texture(Gdx.files.internal("assets/juego3/jugador/astronauta.png"));
        texturaPlataforma = new Texture(Gdx.files.internal("assets/juego3/plataforma/nube.png"));
        
	    //Configuración para la pantalla Pausa
	    imagenReanudar = new Texture(Gdx.files.internal("assets/juego3/imagenPausa/Reanudar.png"));
	    
	    //Texturas de gameover
	    texturaFinPartida = new Texture(Gdx.files.internal("assets/juego3/imagenesGameOver/FIN-DE-LA-PARTIDA.png"));
	    texturaGuadarPunt = new Texture(Gdx.files.internal("assets/juego3/imagenesGameOver/GUARDAR-PUNTUACION.png"));
	    texturaReiniciar = new Texture(Gdx.files.internal("assets/juego3/imagenesGameOver/REINICIAR.png"));
	    
        // Cargar múltiples fondos
        fondos = new Texture[] {
            new Texture(Gdx.files.internal("assets/juego3/fondo/espacio1.png")),
            new Texture(Gdx.files.internal("assets/juego3/fondo/espacio2.png")),
            new Texture(Gdx.files.internal("assets/juego3/fondo/espacio3.png")),
            new Texture(Gdx.files.internal("assets/juego3/fondo/espacio4.png"))
        };
        
        // Seleccionar un fondo aleatorio al inicio
        fondoActual = MathUtils.random(fondos.length - 1);
        texturaFondo = fondos[fondoActual];
        
        // Inicializar jugador
        player = new Rectangle();
        player.width = ANCHO_JUGADOR;
        player.height = ALTURA_JUGADOR;
        player.x = 800 / 2 - ANCHO_JUGADOR/2;
        player.y = 100;
        
        platforms = new Array<Plataforma>();
        crearPlataformasIniciales();
        
        puntuacion = new BitmapFont();
        tiempo = new BitmapFont();
        
        puntuacionTiempo = 0; 
        tiempoAcumulado = 0f;
        puntuacionTotal = 0;
        
        texturaPlataformaRompible1 = new Texture(Gdx.files.internal("assets/juego3/plataforma/plataformaRompible1.png"));
        texturaPlataformaRompible2 = new Texture(Gdx.files.internal("assets/juego3/plataforma/plataformaRompible2.png"));
        texturaPlataformaRompible3 = new Texture(Gdx.files.internal("assets/juego3/plataforma/plataformaRompible3.png"));
    }
    
    @Override
    public void render(float delta) {
    	tiempoAcumulado += delta;
    	
    	// Si ha pasado 1 segundo, sumar 1 al contador y reiniciar el acumulador
    	if (tiempoAcumulado >= 1f) {
    	    puntuacionTiempo += 1;
    	    tiempoAcumulado -= 1f;
    	}
    	
    	//Mostramos la pantalla de Pausa si el ususario presiona escape
    	if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
    	    juegoPausado = !juegoPausado;
    	    if (juegoPausado) {
    	        opacidadPausa = 0f;
    	    }
    	    return;
    	}
    	
	    // Si el juego está pausado, no actualizar nada
	    if (juegoPausado) {
	        dibujarPantallaPausa();
	        return; 
	    }
	    
	    if (gameOver) {
	        mostrarGameOver();
	        return;
	    }
    	
    	// Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Actualizar posición de la cámara
        float targetY = player.y + 100;
        camara.position.y += (targetY - camara.position.y) * 0.1f;
        camara.position.x = 400;
        camara.update();
        
        // Actualizar lógica del juego
        update(delta);
        
        // Configurar cámara para el fondo
        OrthographicCamera bgCamera = new OrthographicCamera();
        bgCamera.setToOrtho(false, 800, 480);
        bgCamera.position.set(400, 240, 0); 
        bgCamera.update();
        
        batch.setProjectionMatrix(bgCamera.combined);
        batch.begin();
        
        // Dibujar fondo que cubre toda la pantalla
        batch.draw(texturaFondo, 0, 0, 800, 480);
        
        tiempo.setColor(Color.WHITE);
        tiempo.draw(batch, "Tiempo: " + puntuacionTiempo, 100, 450);
        
        puntuacion.setColor(Color.WHITE);
        puntuacion.draw(batch, "Altitud: " + puntuacionTotal + " mts", 650, 450);
        
        batch.end();
        
        // Volver a la cámara normal para el resto de elementos
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        
        // Dibujar plataformas
        for(Plataforma platformData : platforms) {
            if(platformData.rect.y > camara.position.y - 500 && 
               platformData.rect.y < camara.position.y + 300) {
                
                Rectangle platform = platformData.rect;
                
                if(platformData.especial) {
                	if(platformData.tocada) {
                	    // Dibujar las tres texturas en posiciones ligeramente desplazadas
                	    batch.setColor(1, 1, 1, 1f);
                	    batch.draw(texturaPlataformaRompible1, platform.x, platform.y, platform.width, platform.height + 50);
                	    
                	    batch.setColor(1, 1, 1, 0.8f);
                	    batch.draw(texturaPlataformaRompible2, platform.x + 2, platform.y + 2, platform.width, platform.height + 50);
                	    
                	    batch.setColor(1, 1, 1, 1f);
                	    batch.draw(texturaPlataformaRompible3, platform.x + 4, platform.y + 4, platform.width, platform.height + 50);
                	    
                	    batch.setColor(1, 1, 1, 1f);
                	} else {
                        // Dibujar solo la primera textura si no ha sido tocada
                        batch.draw(texturaPlataformaRompible1, platform.x, platform.y, platform.width, platform.height + 50);
                    }
                } else { 
                    // Plataforma normal
                    if(platformData.movil) {
                        batch.setColor(0.8f, 0.8f, 1f, 1f);
                        batch.draw(texturaPlataforma, platform.x, platform.y, platform.width, platform.height + 50);
                        batch.setColor(1f, 1f, 1f, 1f);
                    } else {
                        batch.draw(texturaPlataforma, platform.x, platform.y, platform.width, platform.height + 50);
                    }
                }
            }
        }
        
        // Dibujar jugador volteado según la dirección
        if(mirandoDerecha) {
            batch.draw(texturaJugador, player.x + player.width, player.y, -player.width, player.height); 
        } else {
        	batch.draw(texturaJugador, player.x, player.y, player.width, player.height);
        }
	     
	    // Mostrar nombre del jugador
	    String nombre = juego3.getNombreUsuario();
        
        batch.end();
    }

    private void update(float delta) {
        // Movimiento horizontal más suave
        float moveInput = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveInput -= 1;
            mirandoDerecha = false;
        }
        
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveInput += 1;
            mirandoDerecha = true; 
        }
        
        player.x += moveInput * velocidadJugador * delta * 60;

        // Teletransporte horizontal si se sale por los bordes 
        if (player.x < -player.width / 2f) {
            player.x = 800 - player.width / 2f;
        } else if (player.x > 800 - player.width / 2f) {
            player.x = -player.width / 2f;
        }
        
        // Gravedad con delta time
        velocidadSalto += gravedad * delta;
        player.y += velocidadSalto * delta * 60;
        
        if (gameOver) return;
        
        // Detectar caída al vacío 
        if (player.y < camara.position.y - 500) {
            gameOver = true;
            return;
        }
        
        // Actualizar temporizadores de plataformas rompibles
        Iterator<Plataforma> iter = platforms.iterator();
        while(iter.hasNext()) {
            Plataforma plataforma = iter.next();
            if(plataforma.especial && plataforma.tocada) {
                plataforma.tiempoDestruccion -= delta;
                // Añadir efecto visual de parpadeo antes de desaparecer
                if(plataforma.tiempoDestruccion <= 1f) { // Último segundo
                    plataforma.rect.y += MathUtils.random(-1f, 1f); // Pequeño efecto de temblor
                }
                if(plataforma.tiempoDestruccion <= 0) {
                    iter.remove();
                }
            }
        }
        
        // Mover plataformas móviles
        for(Plataforma plataforma : platforms) {
            if(plataforma.movil) {  // Elimina la condición de puntuación
                plataforma.rect.x += plataforma.velocidad * plataforma.direccion * delta;
                
                // Rebotar en los bordes
                if(plataforma.rect.x < 0) {
                    plataforma.rect.x = 0;
                    plataforma.direccion *= -1;
                } else if(plataforma.rect.x > 800 - plataforma.rect.width) {
                    plataforma.rect.x = 800 - plataforma.rect.width;
                    plataforma.direccion *= -1;
                }
            }
        }
        
        // Verificar colisiones
        colisionPlataformas();
        
        // Generar nuevas plataformas
        if(player.y > camara.position.y - 300) {  
            crearNuevasPlataformas();
        }
    }
    
    private void crearPlataformasIniciales() {
        // Plataforma inicial más ancha y centrada (fija)
        Rectangle startingPlatform = new Rectangle();
        startingPlatform.width = 120;
        startingPlatform.height = ALTURA_PLATAFORMA;
        startingPlatform.x = 400 - startingPlatform.width/2;
        startingPlatform.y = 50;
        platforms.add(new Plataforma(startingPlatform, false, 0)); // Plataforma fija
        
        // Generar más plataformas iniciales 
        for(int i = 1; i <= 15; i++) {
            Rectangle platform = new Rectangle();
            platform.width = ANCHO_PLATAFORMA + MathUtils.random(-20, 20);
            platform.height = ALTURA_PLATAFORMA;
            platform.x = MathUtils.random(50, 750 - platform.width);
            platform.y = i * 100 + 50;
            
            // 20% de probabilidad de que sea móvil (1 de cada 5)
            boolean esMovil = MathUtils.random() < 0.2;
            float velocidad = esMovil ? MathUtils.random(40f, 80f) : 0;
            
            Plataforma nuevaPlataforma = new Plataforma(platform, esMovil, velocidad);
            // Marcar 1 de cada 5 como especial (independientemente de si es móvil)
            if(i % 5 == 0) {
                nuevaPlataforma.especial = true;
            }
            
            platforms.add(nuevaPlataforma);
        }
    }
    
    private void crearNuevasPlataformas() {
        // Eliminar plataformas que están muy abajo
        Iterator<Plataforma> iter = platforms.iterator();
        while(iter.hasNext()) {
            Plataforma plataforma = iter.next();
            if (plataforma.rect.y < camara.position.y - 600) {
                iter.remove();
            }
        }
        
        if(platforms.size > 50) {
            return;
        }
        
        float maxY = 0;
        for (Plataforma platformData : platforms) { 
            if (platformData.rect.y > maxY) {
                maxY = platformData.rect.y;
            }
        }
        
        int platformsToAdd = 5;
        float minDistance = 80;
        float maxDistance = 150;
        float lastY = maxY;
        
        for(int i = 0; i < platformsToAdd; i++) {
            Rectangle platform = new Rectangle();
            platform.width = ANCHO_PLATAFORMA + MathUtils.random(-15, 15);
            platform.height = ALTURA_PLATAFORMA;
            platform.x = MathUtils.random(50, 750 - platform.width);
            lastY += MathUtils.random(minDistance, maxDistance);
            platform.y = lastY;
            
            // 20% de probabilidad de plataforma móvil (1 de cada 5)
            boolean esMovil = MathUtils.random() < 0.2;
            
            boolean overlapping;
            int attempts = 0;
            do {
                overlapping = false;
                for (Plataforma existing : platforms) {
                    if(Math.abs(existing.rect.x - platform.x) < 100 &&
                       Math.abs(existing.rect.y - platform.y) < 50) {
                        overlapping = true;
                        platform.x = MathUtils.random(50, 750 - platform.width);
                        attempts++;
                        if(attempts > 10) {
                            platform.y += 30;
                            attempts = 0;
                        }
                        break;
                    }
                }
            } while(overlapping && attempts < 20);
            
            // Crear plataforma con posibilidad de ser móvil
            float velocidadMovil = esMovil ? MathUtils.random(40f, 80f) : 0;
            
            Plataforma nuevaPlataforma = new Plataforma(platform, esMovil, velocidadMovil);
            // Marcar 1 de cada 5 como especial
            if(MathUtils.random() < 0.2f) { // Alternativa al contador i
                nuevaPlataforma.especial = true;
            }
            platforms.add(nuevaPlataforma);
        }
    }
 
    private void colisionPlataformas() {
    	if(velocidadSalto >= 0) return;
        
        for (Plataforma platformData : platforms) {
            Rectangle platform = platformData.rect;
            
            if (player.y <= platform.y + platform.height &&
                player.y >= platform.y &&
                player.x + player.width > platform.x &&
                player.x < platform.x + platform.width) {

                // Si es rompible y ya fue tocada, no permitir saltar
                if(platformData.especial && platformData.tocada) {
                    continue;
                }

                player.y = platform.y + platform.height;
                velocidadSalto = 12f;
                
                if(platformData.movil) {
                    player.x += platformData.velocidad * platformData.direccion * 0.2f;
                }
                
                if (!platformData.superada) {
                    platformData.superada = true;
                    puntuacionTotal += 20;
                }
                
                // Comportamiento para plataformas rompibles
                if(platformData.especial && !platformData.tocada) {
                    platformData.tocada = true;
                    platformData.tiempoDestruccion = 1f; // 2 segundos para destruirse
                }
                
                break;
            }
        }
    }
    
    private void dibujarPantallaPausa() {
        // 1. Primero dibujar el juego completo (fondo + elementos)
        // Usamos la cámara de fondo para todo
        OrthographicCamera bgCamera = new OrthographicCamera();
        bgCamera.setToOrtho(false, 800, 480);
        bgCamera.position.set(400, camara.position.y, 0); 
        bgCamera.update();
        
        batch.setProjectionMatrix(bgCamera.combined);
        batch.begin();
        
        // Dibujar fondo (ajustado a la posición de la cámara)
        float bgY = camara.position.y - 240; 
        batch.draw(texturaFondo, 0, bgY, 800, 480);
        
        // Dibujar plataformas
        for(Plataforma platformData : platforms) {
            Rectangle platform = platformData.rect;
            batch.draw(texturaPlataforma, platform.x, platform.y, platform.width, platform.height + 50);
        }
        
        // Dibujar jugador
        if(mirandoDerecha) {
            batch.draw(texturaJugador, player.x + player.width, player.y, -player.width, player.height); 
        } else {
            batch.draw(texturaJugador, player.x, player.y, player.width, player.height);
        }
        
        batch.end();
        
        // 2. Dibujar HUD 
        OrthographicCamera hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, 800, 480);
        hudCamera.position.set(400, 240, 0);
        hudCamera.update();
        
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        tiempo.setColor(Color.WHITE);
        tiempo.draw(batch, "Tiempo: " + puntuacionTiempo, 100, 450);
        puntuacion.setColor(Color.WHITE);
        puntuacion.draw(batch, "Puntuación: " + puntuacionTotal, 650, 450);
        batch.end();

        // 3. Capa semitransparente de pausa 
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f); 
        shapeRenderer.rect(0, 0, 800, 480);
        shapeRenderer.end();

        // 4. Botón de reanudar 
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);
        
        float xImg = (800 - imagenReanudar.getWidth()) / 2f;
        float yImg = (480 - imagenReanudar.getHeight()) / 2f;
        batch.draw(imagenReanudar, xImg, yImg);
        
        batch.end();

        // Detección de clic en "Reanudar"
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = 480 - Gdx.input.getY(); 
            
            Rectangle reanudarRect = new Rectangle(xImg, yImg, imagenReanudar.getWidth(), imagenReanudar.getHeight());
            
            if (reanudarRect.contains(touchX, touchY)) {
                juegoPausado = false;
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    private void mostrarGameOver() {
        // Configurar la cámara HUD 
        OrthographicCamera hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();
        
        // Dibujar capa semitransparente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); 
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        // Dibujar elementos del game over
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        
        // Dibujar texto "Fin de partida"
        float escalaFinPartida = 0.7f;
        float anchoFin = texturaFinPartida.getWidth() * escalaFinPartida;
        float altoFin = texturaFinPartida.getHeight() * escalaFinPartida;
        float xFin = (Gdx.graphics.getWidth() - anchoFin) / 2;
        float yFin = Gdx.graphics.getHeight() - 200; 
        batch.draw(texturaFinPartida, xFin, yFin, anchoFin, altoFin);

        // Escala para los botones
        float escalaBotones = 0.45f;

        // Dibujar botón de reiniciar (izquierda)
        float anchoReiniciar = texturaReiniciar.getWidth() * escalaBotones;
        float altoReiniciar = texturaReiniciar.getHeight() * escalaBotones;
        float xReiniciar = Gdx.graphics.getWidth() / 4 - anchoReiniciar + 70;
        float yReiniciar = yFin - 100; 
        batch.draw(texturaReiniciar, xReiniciar, yReiniciar, anchoReiniciar, altoReiniciar);

        // Dibujar botón de guardar puntuación (derecha)
        float anchoGuardar = texturaGuadarPunt.getWidth() * escalaBotones;
        float altoGuardar = texturaGuadarPunt.getHeight() * escalaBotones;
        float xGuardar = Gdx.graphics.getWidth() * 3/4 - anchoGuardar/2 - 50; 
        float yGuardar = yReiniciar; 
        batch.draw(texturaGuadarPunt, xGuardar, yGuardar, anchoGuardar, altoGuardar);
        
        // Mostrar puntuación final 
        puntuacion.setColor(Color.WHITE);
        float yPuntuacion = yGuardar -20; 
        puntuacion.draw(batch, "Puntuación final: " + puntuacionTotal, xGuardar + 100, yPuntuacion);
        
        batch.end();
        
        // Detectar clics en los botones
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            hudCamera.unproject(touchPos);
            
            Rectangle guardarRect = new Rectangle(xGuardar, yGuardar, anchoGuardar, altoGuardar);
            Rectangle reiniciarRect = new Rectangle(xReiniciar, yReiniciar, anchoReiniciar, altoReiniciar);
            
            if (guardarRect.contains(touchPos.x, touchPos.y)) {
                // Guardar puntuación
                boolean exito = DBHelper.insertarPuntuacion(juego3.getNombreUsuario(), "ASTRONAUTA", puntuacionTotal);
                if (exito) {
                    Gdx.app.log("Puntuación", "Guardada correctamente");
	                Gdx.app.exit();
                }
            }
            
            if (reiniciarRect.contains(touchPos.x, touchPos.y)) {
                reiniciarJuego();
            }
        }
    }
    
    public void reiniciarJuego() {
        // Reiniciar todas las variables del juego
        player.x = 800 / 2 - ANCHO_JUGADOR/2;
        player.y = 100;
        velocidadSalto = 0;
        puntuacionTotal = 0;
        puntuacionTiempo = 0;
        tiempoAcumulado = 0f;
        
        // Reiniciar plataformas
        platforms.clear();
        crearPlataformasIniciales();
        
        // Reiniciar cámara
        camara.position.y = 240;
        
        // Volver al estado de juego activo
        gameOver = false;
        juegoPausado = false;
    }
    
    @Override
    public void resize(int width, int height) {
        camara.viewportWidth = width;
        camara.viewportHeight = height;
        camara.update();
    } 
    
    @Override
    public void dispose() {
        batch.dispose();
        texturaJugador.dispose();
        texturaPlataforma.dispose();
        texturaFondo.dispose();
        
        if (imagenReanudar != null) {
        	imagenReanudar.dispose(); 
        }
        
        if (texturaPlataformaRompible1 != null) {
        	texturaPlataformaRompible1.dispose();
        }
        
        if (texturaPlataformaRompible2 != null) {
        	texturaPlataformaRompible2.dispose();
        }
        
        if (texturaPlataformaRompible3 != null) {
        	texturaPlataformaRompible3.dispose();
        }
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
	}
	
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Astronauta");
        config.setWindowedMode(800, 600);
        config.setResizable(false);
        
        // Configuración específica para tu juego
        config.setWindowPosition(100, 100);
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4);
        
        config.setWindowIcon("icons/LogoAstronauta.png");
        
        String nombreUsuario = args.length > 0 ? args[0] : "Jugador";
        DBHelper.inicializarBaseDatos();
        
        try {
            // Crea una instancia mínima de Juego3 que actuará como contenedor
            new Lwjgl3Application(new Juego3(nombreUsuario) {
                @Override
                public void create() {
                    batch = new SpriteBatch();
                    setScreen(new Astronauta(this));
                }
            }, config);
        } catch (Exception e) {
            e.printStackTrace();
            // Modo seguro alternativo
            Lwjgl3ApplicationConfiguration fallbackConfig = new Lwjgl3ApplicationConfiguration();
            fallbackConfig.setTitle("Astronauta - Modo Seguro");
            fallbackConfig.setWindowedMode(800, 600);
            new Lwjgl3Application(new Juego3(nombreUsuario) {
                @Override
                public void create() {
                    setScreen(new Astronauta(this));
                }
            }, fallbackConfig);
        }
    }
}