package tfg.juegos.juego1;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.*;

/**
 * @author Pablo Marcos Sánchez
 */
public class Enemigo {
    private float x, y;
    private float tamaño;
    private float velocidad;
    private int filasLaberinto;
    private int columnasLaberinto;
    private int tamañoCelda;
    private int celdaFila, celdaColumna;
    private int objetivoFila, objetivoColumna;
    
    // Texturas y animación (similar a Jugador)
    private Map<String, Texture[]> animaciones;
    private TextureRegion frameActual;
    private float stateTime;
    private float duracionFrame = 0.1f;
    private int indiceActualFrame = 0;
    
    // Dirección del enemigo
    private String direccion = "abajo";
    private boolean estaMoviendo = false;
    private Map<String, Integer> framesPorAnimacion = new HashMap<>();
    
    // Para el movimiento autónomo (persecución)
    private Random random;
    private float tiempoCambioCamino = 0;
    private float intervaloCambioCamino = 1f; // Recalcular camino cada 1 segundo
    
    private float tamañoHitbox; // Tamaño real de colisión
    private List<int[]> caminoActual; // Camino calculado por A*
    
    public enum TipoEnemigo {
        DEMONIO,
        OGRO,
        ZOMBIE
    }
    
    private TipoEnemigo tipo;
    
    public Enemigo(float x, float y, float tamaño, float velocidad, int filasLaberinto, int columnasLaberinto, int tamañoCelda, TipoEnemigo tipo) {
        this.x = x;
        this.y = y;
        this.tamaño = tamaño;
        this.velocidad = velocidad * 0.7f;
        this.filasLaberinto = filasLaberinto;
        this.columnasLaberinto = columnasLaberinto;
        this.tamañoCelda = tamañoCelda;
        this.random = new Random();
        this.caminoActual = new ArrayList<>();
        
        if (tipo == null) {
            throw new IllegalArgumentException("TipoEnemigo cannot be null");
        }
        this.tipo = tipo;
        
        // Posición inicial aleatoria (excepto la posición del jugador)
        this.celdaFila = random.nextInt(filasLaberinto);
        this.celdaColumna = random.nextInt(columnasLaberinto);
        
        // Asegurarnos de que no empiece en la misma celda que el jugador
        while (celdaFila == filasLaberinto - 1 && celdaColumna == 0) {
            this.celdaFila = random.nextInt(filasLaberinto);
            this.celdaColumna = random.nextInt(columnasLaberinto);
        }
        
        this.objetivoFila = celdaFila;
        this.objetivoColumna = celdaColumna;

        cargarTexturas();
        
        this.x = celdaColumna * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        this.y = celdaFila * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
        
        this.tamañoHitbox = tamaño * 0.5f;
    }
    
    private void cargarTexturas() {
        if (tipo == null) {
            throw new IllegalStateException("TipoEnemigo cannot be null");
        }
        
        animaciones = new HashMap<>();
        framesPorAnimacion = new HashMap<>();
        
        // Configurar número de frames para cada animación
        framesPorAnimacion.put("arriba", 2);
        framesPorAnimacion.put("derecha", 4);
        framesPorAnimacion.put("izquierda", 4); 
        framesPorAnimacion.put("abajo", 2);
        
        switch(tipo) {
            case DEMONIO:
                cargarTexturasDemonio();
                break;
            case OGRO:
                cargarTexturasOgro();
                break;
            case ZOMBIE:
                cargarTexturasZombie();
                break;
        }
         
        // Frame inicial
        frameActual = new TextureRegion(animaciones.get("abajo")[0]);
    }
    
    public void dibujar(SpriteBatch batch) {
        // Obtenemos animación actual 
        String animacionActual = direccion.equals("izquierda") ? "derecha" : direccion;
        
        if (!animaciones.containsKey(animacionActual)) {
            Gdx.app.error("Enemigo", "Animación no encontrada: " + animacionActual);
            animacionActual = "abajo"; 
        }
        
        Texture[] frames = animaciones.get(animacionActual);
        int numFrames = framesPorAnimacion.get(animacionActual);
        
        // Actualizamos animación si se está moviendo
        if (estaMoviendo) {
            stateTime += Gdx.graphics.getDeltaTime();
            if (stateTime > duracionFrame) {
                stateTime = 0;
                indiceActualFrame = (indiceActualFrame + 1) % numFrames;
                frameActual = new TextureRegion(frames[indiceActualFrame]);
                
                // Aplicamos flip horizontal si es izquierda
                if (direccion.equals("izquierda")) {
                    frameActual.flip(true, false);
                }
            }
        } else {
            // Frame estático (primer frame de la dirección actual)
            frameActual = new TextureRegion(frames[0]);
            
            // Aplicamos flip horizontal si es izquierda
            if (direccion.equals("izquierda")) {
                frameActual.flip(true, false);
            }
        }
        
        // Dibujamos el frame actual
        batch.draw(frameActual, x, y, tamaño, tamaño);
        
        // Reseteamos el flip para el próximo frame (excepto para izquierda)
        if (frameActual.isFlipX() && !direccion.equals("izquierda")) {
            frameActual.flip(true, false);
        }
    }
    
    public void mover(boolean[][] paredesHorizontales, boolean[][] paredesVerticales, Jugador jugador) {
        float delta = Gdx.graphics.getDeltaTime();
        
        // Actualizar el camino hacia el jugador periódicamente
        tiempoCambioCamino += delta;
        if (tiempoCambioCamino >= intervaloCambioCamino) {
            tiempoCambioCamino = 0;
            calcularCaminoHaciaJugador(paredesHorizontales, paredesVerticales, jugador);
        }
        
        // Si hay un camino válido, seguirlo
        if (!caminoActual.isEmpty()) {
            // Tomar el siguiente paso del camino
            int[] siguientePaso = caminoActual.get(0);
            objetivoFila = siguientePaso[0];
            objetivoColumna = siguientePaso[1];
            
            // Actualizar la dirección según el movimiento
            if (objetivoFila > celdaFila) {
                direccion = "arriba";
            } else if (objetivoFila < celdaFila) {
                direccion = "abajo";
            } else if (objetivoColumna < celdaColumna) {
                direccion = "izquierda";
            } else if (objetivoColumna > celdaColumna) {
                direccion = "derecha";
            }
            
            // Moverse hacia el objetivo
            float targetX = objetivoColumna * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
            float targetY = objetivoFila * tamañoCelda + tamañoCelda / 2f - tamaño / 2f;
            
            estaMoviendo = (Math.abs(x - targetX) > 1) || (Math.abs(y - targetY) > 1);
            
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
            
            // Si llegamos a la celda objetivo, actualizar la celda actual y avanzar en el camino
            if (Math.abs(x - targetX) < 1 && Math.abs(y - targetY) < 1) {
                celdaFila = objetivoFila;
                celdaColumna = objetivoColumna;
                caminoActual.remove(0); // Eliminar el paso actual
            }
        } else {
            // Si no hay camino, permanecer quieto
            estaMoviendo = false;
        }
    }
    
    private void calcularCaminoHaciaJugador(boolean[][] paredesHorizontales, boolean[][] paredesVerticales, Jugador jugador) {
        // Obtener la celda actual del jugador
        int jugadorFila = (int)(jugador.getY() / tamañoCelda);
        int jugadorColumna = (int)(jugador.getX() / tamañoCelda);
        
        // Asegurarse de que las coordenadas estén dentro de los límites
        jugadorFila = Math.max(0, Math.min(filasLaberinto - 1, jugadorFila));
        jugadorColumna = Math.max(0, Math.min(columnasLaberinto - 1, jugadorColumna));
        
        // Calcular el camino usando A*
        caminoActual = aStar(celdaFila, celdaColumna, jugadorFila, jugadorColumna, paredesHorizontales, paredesVerticales);
    }
    
    private List<int[]> aStar(int inicioFila, int inicioColumna, int metaFila, int metaColumna, boolean[][] paredesHorizontales, boolean[][] paredesVerticales) {
        PriorityQueue<NodoAStar> colaPrioridad = new PriorityQueue<>((a, b) -> Double.compare(a.fCost, b.fCost));
        boolean[][] visitado = new boolean[filasLaberinto][columnasLaberinto];
        Map<String, NodoAStar> nodos = new HashMap<>();
        
        // Crear el nodo inicial
        NodoAStar inicio = new NodoAStar(inicioFila, inicioColumna);
        inicio.gCost = 0;
        inicio.hCost = calcularHeuristica(inicioFila, inicioColumna, metaFila, metaColumna);
        inicio.fCost = inicio.gCost + inicio.hCost;
        colaPrioridad.add(inicio);
        nodos.put(inicioFila + "," + inicioColumna, inicio);
        
        // Direcciones posibles: arriba, abajo, izquierda, derecha
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        
        while (!colaPrioridad.isEmpty()) {
            NodoAStar actual = colaPrioridad.poll();
            int fila = actual.fila;
            int columna = actual.columna;
            
            if (visitado[fila][columna]) continue;
            visitado[fila][columna] = true;
            
            // Si llegamos a la meta, reconstruir el camino
            if (fila == metaFila && columna == metaColumna) {
                return reconstruirCamino(actual);
            }
            
            // Explorar vecinos
            for (int[] dir : direcciones) {
                int nuevaFila = fila + dir[0];
                int nuevaColumna = columna + dir[1];
                
                // Verificar si el vecino está dentro de los límites y no ha sido visitado
                if (nuevaFila >= 0 && nuevaFila < filasLaberinto && nuevaColumna >= 0 && nuevaColumna < columnasLaberinto && !visitado[nuevaFila][nuevaColumna]) {
                    // Verificar si el movimiento es válido (no hay pared)
                    boolean movimientoValido = false;
                    if (dir[0] == -1 && !paredesHorizontales[fila][columna]) movimientoValido = true; // Abajo
                    else if (dir[0] == 1 && !paredesHorizontales[fila + 1][columna]) movimientoValido = true; // Arriba
                    else if (dir[1] == -1 && !paredesVerticales[fila][columna]) movimientoValido = true; // Izquierda
                    else if (dir[1] == 1 && !paredesVerticales[fila][columna + 1]) movimientoValido = true; // Derecha
                    
                    if (movimientoValido) {
                        double nuevoGCost = actual.gCost + 1; // Costo de moverse a una celda adyacente
                        String claveNodo = nuevaFila + "," + nuevaColumna;
                        NodoAStar vecino = nodos.getOrDefault(claveNodo, new NodoAStar(nuevaFila, nuevaColumna));
                        
                        if (nuevoGCost < vecino.gCost) {
                            vecino.padre = actual;
                            vecino.gCost = nuevoGCost;
                            vecino.hCost = calcularHeuristica(nuevaFila, nuevaColumna, metaFila, metaColumna);
                            vecino.fCost = vecino.gCost + vecino.hCost;
                            nodos.put(claveNodo, vecino);
                            colaPrioridad.add(vecino);
                        }
                    }
                }
            }
        }
        
        // Si no se encuentra un camino, devolver una lista vacía
        return new ArrayList<>();
    }
    
    private double calcularHeuristica(int fila, int columna, int metaFila, int metaColumna) {
        // Usar distancia Manhattan como heurística
        return Math.abs(fila - metaFila) + Math.abs(columna - metaColumna);
    }
    
    private List<int[]> reconstruirCamino(NodoAStar nodo) {
        List<int[]> camino = new ArrayList<>();
        NodoAStar actual = nodo;
        
        while (actual != null) {
            camino.add(0, new int[]{actual.fila, actual.columna});
            actual = actual.padre;
        }
        
        // Eliminar el primer nodo (posición actual del enemigo)
        if (!camino.isEmpty()) {
            camino.remove(0);
        }
        
        return camino;
    }
    
    private void cargarTexturasDemonio() {
        // Cargar animación de andar hacia arriba
        Texture[] arribaFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/demonio/DemonioAndandoArriba/DemonioAndandoArriba" + (i+1) + ".png";
            arribaFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("arriba", arribaFrames);
        
        Texture[] abajoFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/demonio/DemonioAndandoAbajo/DemonioAndandoAbajo" + (i+1) + ".png";
            abajoFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("abajo", abajoFrames);
        
        Texture[] ladosFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ruta = "assets/juego1/demonio/DemonioAndando/DemonioAndando" + (i+1) + ".png";
            ladosFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("derecha", ladosFrames);
    }
    
    private void cargarTexturasOgro() {
        // Cargar animación de andar hacia arriba
        Texture[] arribaFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/ogro/OgroAndandoArriba/OgroAndandoArriba" + (i+1) + ".png";
            arribaFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("arriba", arribaFrames);
        
        Texture[] abajoFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/ogro/OgroAndandoAbajo/OgroAndandoAbajo" + (i+1) + ".png";
            abajoFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("abajo", abajoFrames);
        
        Texture[] ladosFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ruta = "assets/juego1/ogro/OgroAndando/OgroAndando" + (i+1) + ".png";
            ladosFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("derecha", ladosFrames);
    } 
    
    private void cargarTexturasZombie() {
        // Cargar animación de andar hacia arriba
        Texture[] arribaFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/zombie/ZombieAndandoArriba/ZombieAndandoArriba" + (i+1) + ".png";
            arribaFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("arriba", arribaFrames);
        
        Texture[] abajoFrames = new Texture[2];
        for (int i = 0; i < 2; i++) {
            String ruta = "assets/juego1/zombie/ZombieAndandoAbajo/ZombieAndandoAbajo" + (i+1) + ".png";
            abajoFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("abajo", abajoFrames);
        
        Texture[] ladosFrames = new Texture[4];
        for (int i = 0; i < 4; i++) {
            String ruta = "assets/juego1/zombie/ZombieAndando/ZombieAndando" + (i+1) + ".png";
            ladosFrames[i] = new Texture(Gdx.files.internal(ruta));
        }
        animaciones.put("derecha", ladosFrames);
    }
      
    public void dispose() {
        // Liberar todas las texturas
        for (Texture[] frames : animaciones.values()) {
            for (Texture texture : frames) {
                texture.dispose();
            }
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

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Rectangle getHitbox() {
        return new Rectangle(x - tamañoHitbox/2, y - tamañoHitbox/2, tamañoHitbox, tamañoHitbox);
    }
}
