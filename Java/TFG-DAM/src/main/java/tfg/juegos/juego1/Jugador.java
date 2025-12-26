package tfg.juegos.juego1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pablo Marcos Sánchez
 */
public class Jugador {
    private float x, y;
    private float tamaño;
    private float velocidad;
    private int filasLaberinto;
    private int columnasLaberinto;
    private int tamañoCelda;
    private int celdaFila, celdaColumna;
    private int objetivoFila, objetivoColumna;
    
    //Texturas y animación
    private Map<String, Texture[]> animaciones;
    private TextureRegion frameActual;
    private float stateTime;
    private float duracionFrame = 0.1f;
    private int indiceFrameActual = 0;
    
    //Dirección del jugador 
    private String direccion = "abajo";
    private boolean estaMoviendose = false;
    
    //Atributos para la vida
    private int vidas;
    private boolean invulnerable;
    private float tiempoInvulnerable;
    private final float duracionInvulnerabilidad = 3f;

    private float tamañoHitbox; 
    
    //Variables para el efecto de parpadeo
    private boolean parpadeando = false;
    private float tiempoParpadeo = 0;
    private final float duracionParpadeo = 1.5f; 
    private float intervaloParpadeo = 0.1f; 
    
    //Añadir un mapa para almacenar el número de frames por animación
    private Map<String, Integer> framesPorAnimacion = new HashMap<>();
    

    public Jugador(float x, float y, float tamaño, float velocidad, int filasLaberinto, int columnasLaberinto, int tamañoCelda) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.velocidad = velocidad;
        this.filasLaberinto = filasLaberinto;
        this.columnasLaberinto = columnasLaberinto;
        this.tamañoCelda = tamañoCelda;
        this.celdaFila = filasLaberinto - 1;
        this.celdaColumna = 0;
        this.objetivoFila = celdaFila;
        this.objetivoColumna = celdaColumna;
        this.tamañoHitbox = tamaño * 0.5f;
        
        this.x = celdaColumna * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        this.y = celdaFila * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        
        this.vidas = 3;
        this.invulnerable = false;
        this.tiempoInvulnerable = 0;
        
        cargarTexturas();
    }
    
    private void cargarTexturas() {
        animaciones = new HashMap<>();
        
        //Número de frames para cada animación
        framesPorAnimacion.put("arriba", 2);
        framesPorAnimacion.put("derecha", 4);
        framesPorAnimacion.put("izquierda", 4); 
        framesPorAnimacion.put("abajo", 4);
        
        //Animación de andar hacia arriba
        Texture[] arribaFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/jugador/andandoArriba/jugadorAndandoArriba" + (i+1) + ".png";
            arribaFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("arriba", arribaFrames);
        
        // Animación de andar hacia los lados (derecha)
        Texture[] derechaFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ruta = "assets/juego1/jugador/andandoLados/jugadorAndandoLados" + (i+1) + ".png";
            derechaFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("derecha", derechaFrames);
        
        //Para izquierda usaremos los mismos frames de derecha pero volteados
        animaciones.put("izquierda", derechaFrames);
        
        //Animación de andar hacia abajo
        Texture[] abajoFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ruta = "assets/juego1/jugador/andandoAbajo/jugadorAndandoAbajo" + (i+1) + ".png";
            abajoFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("abajo", abajoFrames);
        
        //Frame inicial
        frameActual = new TextureRegion(animaciones.get("abajo")[0]);
    }
    
    public void dibujar(SpriteBatch batch) {
        //Solo dibujamos en ciertos intervalos cuando está parpadeando
        if (parpadeando && (int)(tiempoParpadeo / intervaloParpadeo) % 2 == 0) {
            return; 
        }
    	
        //Obtenemos animación actual (usar derecha para izquierda)
    	String animacionActual = direccion.equals("izquierda") ? "derecha" : direccion;
        Texture[] frames = animaciones.get(animacionActual);
        int numFrames = framesPorAnimacion.get(animacionActual);
        
        //Actualizar animación si se está moviendo
        if (estaMoviendose) {
            stateTime += Gdx.graphics.getDeltaTime();
            if (stateTime > duracionFrame) {
                stateTime = 0;
                indiceFrameActual = (indiceFrameActual + 1) % numFrames;
                frameActual = new TextureRegion(frames[indiceFrameActual]);
                
                // Aplicar flip horizontal si es izquierda
                if (direccion.equals("izquierda")) {
                    frameActual.flip(true, false);
                }
            }
        } else {
            //Frame estático (primer frame de la dirección actual)
            frameActual = new TextureRegion(frames[0]);
            
            //Aplicamos flip horizontal si es izquierda
            if (direccion.equals("izquierda")) {
                frameActual.flip(true, false);
            }
        }

        //Dibujamos el frame actual
        batch.draw(frameActual, x, y, tamaño, tamaño);
        
        //Reseteamos el flip para el próximo frame (excepto para izquierda)
        if (frameActual.isFlipX() && !direccion.equals("izquierda")) {
            frameActual.flip(true, false);
        }
    }
    
    public void mover(boolean[][] paredesHorizontales, boolean[][] paredesVerticales) {
        // Solo procesar movimiento si realmente hay input
        boolean tieneInput = Gdx.input.isKeyPressed(Input.Keys.W) || 
                           Gdx.input.isKeyPressed(Input.Keys.S) ||
                           Gdx.input.isKeyPressed(Input.Keys.A) ||
                           Gdx.input.isKeyPressed(Input.Keys.D);
        
        if (!tieneInput && !estaMoviendose) {
            // Si no hay input y no se está moviendo, resetear animación
            resetearAnimacion();
            return;
        }
    	
        float delta = Gdx.graphics.getDeltaTime();
        float targetX = objetivoColumna * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        float targetY = objetivoFila * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        
        estaMoviendose = (Math.abs(x - targetX) > 1) || (Math.abs(y - targetY) > 1);
        
        if (Math.abs(x - targetX) > 1) {
            x += Math.signum(targetX - x) * velocidad * delta;
        } else {
            x = targetX;
        }

        if (Math.abs(y - targetY) > 1) {
            y += Math.signum(targetY - y) * velocidad * delta;
        } else {
            y = targetY;
        }

        if (x == targetX && y == targetY) {
            celdaFila = objetivoFila;
            celdaColumna = objetivoColumna;

            if (Gdx.input.isKeyPressed(Input.Keys.W) && !paredesHorizontales[celdaFila + 1][celdaColumna]) {
                objetivoFila += 1;
                direccion = "arriba";
            } else if (Gdx.input.isKeyPressed(Input.Keys.S) && !paredesHorizontales[celdaFila][celdaColumna]) {
                objetivoFila -= 1;
                direccion = "abajo";
            } else if (Gdx.input.isKeyPressed(Input.Keys.A) && !paredesVerticales[celdaFila][celdaColumna]) {
                objetivoColumna -= 1;
                direccion = "izquierda";
            } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !paredesVerticales[celdaFila][celdaColumna + 1]) {
                objetivoColumna += 1;
                direccion = "derecha";
            }

            objetivoFila = Math.max(0, Math.min(filasLaberinto - 1, objetivoFila));
            objetivoColumna = Math.max(0, Math.min(columnasLaberinto - 1, objetivoColumna));
        }
    }
    
    // Método para detectar colisión con el enemigo
    public boolean detectarColisionConEnemigo(Enemigo enemigo) {
        float jugadorMinX = this.x - this.tamañoHitbox / 2;
        float jugadorMaxX = this.x + this.tamañoHitbox / 2;
        float jugadorMinY = this.y - this.tamañoHitbox / 2;
        float jugadorMaxY = this.y + this.tamañoHitbox / 2;
        
        float enemigoMinX = enemigo.getX() - enemigo.getTamañoHitbox() / 2;
        float enemigoMaxX = enemigo.getX() + enemigo.getTamañoHitbox() / 2;
        float enemigoMinY = enemigo.getY() - enemigo.getTamañoHitbox() / 2;
        float enemigoMaxY = enemigo.getY() + enemigo.getTamañoHitbox() / 2;
        
        // Verificar si hay intersección de las hitboxes
        boolean colisionaEnX = jugadorMaxX > enemigoMinX && jugadorMinX < enemigoMaxX;
        boolean colisionaEnY = jugadorMaxY > enemigoMinY && jugadorMinY < enemigoMaxY;
        
        return colisionaEnX && colisionaEnY;
    }
    
    // Métodos para gestionar vidas
    public void perderVida() {
        if (!invulnerable) {
            vidas--;
            invulnerable = true;
            tiempoInvulnerable = 0;
            System.out.println("Vidas restantes: " + vidas);
        }
        
        // Si el jugador no está parpadeando, activamos el parpadeo de invulnerabilidad
        if (!parpadeando) {
            parpadeando = true;
            tiempoParpadeo = 0;
            System.out.println("Vidas restantes: " + vidas); 
        }
    }
    
    public boolean esInvulnerable() {
        return invulnerable;
    }
    
    public void actualizar(float delta, List<Enemigo> enemigos) {
        // Actualizar animación y estado
        stateTime += delta;
        
        // Verificar colisión con cada enemigo
        for (Enemigo enemigo : enemigos) {
            if (this.getHitbox().overlaps(enemigo.getHitbox())) {
                if (!invulnerable) {
                    vidas--;
                    invulnerable = true;
                    tiempoInvulnerable = 0;
                    // Efecto visual de daño (parpadeo)
                    Gdx.app.log("Jugador", "¡Colisión con enemigo! Vidas restantes: " + vidas);
                    break; // Salir del bucle después de una colisión para evitar múltiples golpes
                }
            }
        }
        
        // Resto de la lógica de actualización...
        if (invulnerable) {
            tiempoInvulnerable += delta;
            if (tiempoInvulnerable >= duracionInvulnerabilidad) {
                invulnerable = false;
            }
        }
        
        // Actualizar efecto de parpadeo
        if (parpadeando) {
            tiempoParpadeo += delta;
            if (tiempoParpadeo >= duracionParpadeo) {
                parpadeando = false;
            }
        }
    }
    
    public void reiniciarPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        // Calcular la nueva celda basada en la posición
        this.celdaColumna = (int)(x / tamañoCelda);
        this.celdaFila = (int)(y / tamañoCelda);
        // Sincronizar objetivo con la posición actual
        this.objetivoColumna = celdaColumna;
        this.objetivoFila = celdaFila;
        // Resetear completamente el estado de movimiento
        this.estaMoviendose = false;
        // Resetear la dirección para evitar animaciones residuales
        this.direccion = "abajo";
        // Resetear el frame de animación al frame estático
        this.indiceFrameActual = 0;
        this.stateTime = 0;
        // Forzar la actualización del frame actual
        Texture[] frames = animaciones.get("abajo");
        this.frameActual = new TextureRegion(frames[0]);
    }
    
    public void resetearAnimacion() {
        this.indiceFrameActual = 0;
        this.stateTime = 0;
        Texture[] frames = animaciones.get(direccion);
        this.frameActual = new TextureRegion(frames[0]);
        if (direccion.equals("izquierda")) {
            this.frameActual.flip(true, false);
        }
    }
    
    // Getters para la posición (útil para detección de colisiones)
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getTamaño() {
        return tamaño;
    }
    
    public float getTamañoHitbox() {
        return tamañoHitbox;
    }
    
    public int getVidas() {
        return vidas;
    }
    
    public void setVidas(int vidas) {
		this.vidas = vidas;
	}
    
    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void setX(float x) {
		this.x = x;
	}
    
    public void setY(float y) {
		this.y = y;
	}
    
    public Rectangle getHitbox() {
        return new Rectangle(x - tamañoHitbox/2, y - tamañoHitbox/2, tamañoHitbox, tamañoHitbox);
    }
    
    public void dispose() {
        // Liberar todas las texturas
        for (Texture[] frames : animaciones.values()) {
            for (Texture texture : frames) {
                texture.dispose();
            }
        }
    }
}