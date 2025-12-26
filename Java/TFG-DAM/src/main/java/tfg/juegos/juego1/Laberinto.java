package tfg.juegos.juego1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import tfg.DBHelper;
import tfg.Pantalla;
import tfg.PuntuacionUsuario;

/**
 * @author Pablo Marcos Sánchez
 */
public class Laberinto extends Pantalla{
    private ShapeRenderer shapeRenderer; 
    private SpriteBatch batch; 
	
	//Atributos de los laberintos
	private int filas = 15;  
    private int columnas = 15; 
    private int tamañoCelda = 40;  
    private boolean[][] paredesHorizontales;  
    private boolean[][] paredesVerticales;
    private int salidaFila, salidaColumna;
    
    //Instancias de los personajes
    private Jugador jugador;
    
    //Textos
    private BitmapFont textoTiempo, fuenteNombreJugador, textoVidas, puntuacion, txtnumLaberinto, txtTop3;
    Texture texturaBlanca;
    
    //Imagenes corazon
    private Texture corazonLleno;
    private Texture corazonVacio; 
    
    //Atributos para la pantalla de pausa
    private float opacidadPausa = 0f;
    private Texture imagenReanudar;
    private boolean juegoPausado = false;
    
    private int numLaberinto = 1;
    
    //Puntuacion
    private float tiempoAcumulado = 0f;
    private int tiempoInicial = 120 ; 
    private int tiempoRestante = tiempoInicial; 
    private int puntuacionTotal = 0;
    
    //Textura Game Over 
    private Texture texturaFinPartida, texturaGuadarPunt, texturaReiniciar;
    
    private List<Enemigo> enemigosActivos; // Lista para manejar enemigos dinámicamente
    private int laberintosCompletados = 0;
    
    private List<PuntuacionUsuario> top3 = new ArrayList<>();
    
    private boolean enTransicion = false;
    
	public Laberinto(Juego1 juego1) {
		super(juego1);
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch(); 
		enemigosActivos = new ArrayList<>();
		generarLaberinto();
		
		//Protagonista
		jugador = new Jugador(0, (filas-1) * tamañoCelda, 64, 200, filas, columnas, tamañoCelda);
        
        // Inicializar con 1 enemigo (el primero)
        inicializarEnemigos(1);
	}
	
	@Override
	public void show() {
	    if (batch == null) {
	        batch = new SpriteBatch();
	    }
		
	    //Texto para el usuario
	    fuenteNombreJugador = new BitmapFont();
	    fuenteNombreJugador.setColor(Color.BLACK);
	    fuenteNombreJugador.getData().setScale(1.2f);
	    
	    textoVidas = new BitmapFont(); 
	    textoVidas.setColor(Color.BLACK);
	    textoVidas.getData().setScale(1.1f);
	    
	    textoTiempo = new BitmapFont();
	    
	    puntuacion = new BitmapFont();
	    
	    txtnumLaberinto = new BitmapFont();
	    
	    txtTop3 = new BitmapFont();
	    txtTop3.setColor(Color.BLACK);
	    txtTop3.getData().setScale(1.0f);
	    
	    //Texturas de corazones
	    corazonLleno = new Texture(Gdx.files.internal("assets/juego1/corazon/CorazonLleno.png"));
	    corazonVacio = new Texture(Gdx.files.internal("assets/juego1/corazon/CorazonVacio.png"));
	    
	    //Texturas pantalla Game Over
	    texturaFinPartida = new Texture(Gdx.files.internal("assets/juego1/imagenesGameOver/FIN-DE-LA-PARTIDA.png"));
	    texturaGuadarPunt = new Texture(Gdx.files.internal("assets/juego1/imagenesGameOver/GUARDAR-PUNTUACION.png"));
	    texturaReiniciar = new Texture(Gdx.files.internal("assets/juego1/imagenesGameOver/REINICIAR.png"));
	    
	    //Configuración para la pantalla Pausa
	    imagenReanudar = new Texture(Gdx.files.internal("assets/juego1/imagenPausa/Reanudar.png"));
	    
	    cargarTop3();
	}
	
	@Override
	public void render(float delta) {
	    super.render(delta);
	    
	    // LIMPIAR PANTALLA 
	    Gdx.gl.glClearColor(0f, 0.55f, 0f, 1f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    // Gestionar transición
	    if (enTransicion) {
	        tiempoAcumulado += delta;
	        if (tiempoAcumulado >= 0.5f) { 
	            enTransicion = false;
	            tiempoAcumulado = 0f; 
	        }
	    }
	    
	    if (juegoPausado) {
	        dibujarPantallaPausa();
	        return;
	    }
	    
	    if (!enTransicion && haLlegadoASalida()) {
	        generarNuevoLaberinto();
	        return;
	    }
	    
	    // Mostramos la pantalla de Pausa si el usuario presiona escape
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
	    
	    // Sumar el delta al tiempo acumulado si el juego no está en pausa
	    // Actualizar el tiempo restante si el juego no está en pausa y el jugador tiene vidas
	    if (!juegoPausado && jugador.getVidas() > 0) {
	        tiempoAcumulado += delta;
	        
	        // Si ha pasado 1 segundo, restar 1 al contador y reiniciar el acumulador
	        if (tiempoAcumulado >= 1f) {
	            tiempoRestante -= 1;
	            tiempoAcumulado -= 1f;
	            
	            // Comprobar si se ha acabado el tiempo
	            if (tiempoRestante <= 0) {
	                tiempoRestante = 0;
	                jugador.setVidas(0); 
	            }
	        }
	    }
	    
	    if (haLlegadoASalida()) {
	        generarNuevoLaberinto();
	        return; 
	    }
	    
	    if (!enemigosActivos.isEmpty()) {
	        jugador.actualizar(delta, enemigosActivos);
	    }
	    
	    // Mostrar game over si se quedó sin vidas
	    if (jugador.getVidas() <= 0) {
	        mostrarGameOver();
	        
	        // Opción para reiniciar
	        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
	            reiniciarJuego();
	        }
	        return;
	    }
	    
	    // Actualizar y mover enemigos
	    for (Enemigo enemigo : enemigosActivos) {
	        enemigo.mover(paredesHorizontales, paredesVerticales, jugador);
	    }
	    
	    // Dibujar celda de salida (roja) ANTES del laberinto
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    shapeRenderer.setColor(Color.RED);
	    shapeRenderer.rect(
	        salidaColumna * tamañoCelda, 
	        salidaFila * tamañoCelda, 
	        tamañoCelda, 
	        tamañoCelda
	    );
	    shapeRenderer.end();
	    
	    // Dibujar laberinto DESPUÉS de la celda de salida
	    dibujarLaberinto();
	    
	    // Actualizar posiciones
	    jugador.mover(paredesHorizontales, paredesVerticales);
	    
	    //dibujarHitboxes();
	    
	    // Dibujar personajes y elementos de UI
	    dibujarPersonajesYUI();
	    
	    dibujarVidas();
	}
	
	// Métodos auxiliares separados para mejor organización
	private void dibujarLaberinto() {
	    // Grosor de las paredes (en píxeles)
	    float grosorPared = 5f; // Puedes ajustar este valor según lo grueso que quieras las paredes
	    
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    shapeRenderer.setColor(new Color(0f, 0.2f, 0f, 1f));
	    
	    // Dibujar paredes horizontales (como rectángulos gruesos)
	    for (int i = 0; i <= filas; i++) {
	        for (int j = 0; j < columnas; j++) {
	            if (paredesHorizontales[i][j]) {
	                shapeRenderer.rect(
	                    j * tamañoCelda, 
	                    i * tamañoCelda - grosorPared/2, // Centrar el grosor verticalmente
	                    tamañoCelda, 
	                    grosorPared
	                );
	            }
	        }
	    }
	    
	    // Dibujar paredes verticales (como rectángulos gruesos)
	    for (int i = 0; i < filas; i++) {
	        for (int j = 0; j <= columnas; j++) {
	            if (paredesVerticales[i][j]) {
	                shapeRenderer.rect(
	                    j * tamañoCelda - grosorPared/2, // Centrar el grosor horizontalmente
	                    i * tamañoCelda, 
	                    grosorPared, 
	                    tamañoCelda
	                );
	            }
	        }
	    }
	    
	    shapeRenderer.end();
	}

	private void dibujarPersonajesYUI() {
	    batch.begin();
	    // Dibujar personajes
	    jugador.dibujar(batch);
	    
	    for (Enemigo enemigo : enemigosActivos) {
	        enemigo.dibujar(batch);
	    }
	    
	    batch.end();
	}

	private void generarLaberinto() {
	    // Inicializar matrices de paredes (todas las paredes existen al inicio)
	    paredesHorizontales = new boolean[filas + 1][columnas];
	    paredesVerticales = new boolean[filas][columnas + 1];
	    
	    // Todas las paredes están activas inicialmente
	    for (int i = 0; i <= filas; i++) {
	        for (int j = 0; j < columnas; j++) {
	            paredesHorizontales[i][j] = true;
	        }
	    }
	    for (int i = 0; i < filas; i++) { 
	        for (int j = 0; j <= columnas; j++) {
	            paredesVerticales[i][j] = true;
	        }
	    }
	    
	    // Posición inicial (esquina inferior izquierda)
	    int inicioFila = filas - 1;
	    int inicioColumna = 0;
	    
	    // Posición de salida (esquina opuesta - superior derecha)
	    salidaFila = 0;
	    salidaColumna = columnas - 1;
	    
	    // Abrir entrada y salida
	    paredesVerticales[inicioFila][inicioColumna] = false;      // Entrada
	    paredesVerticales[salidaFila][salidaColumna] = false;      // Salida
	    
	    // Asegurar camino inicial desde la entrada (opcional)
	    paredesHorizontales[inicioFila][inicioColumna] = false;
	    
	    // Generar el laberinto usando Depth-First Search (DFS)
	    // Empezamos desde la posición inicial
	    generarLaberintoDFS(inicioFila, inicioColumna, new boolean[filas][columnas]);
	}
	
	private void generarLaberintoDFS(int fila, int columna, boolean[][] visitado) {
        visitado[fila][columna] = true;
        
        // Direcciones posibles: arriba, abajo, izquierda, derecha
        int[][] direcciones = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        // Mezclar direcciones para aleatoriedad
        mezclarArrays(direcciones);
        
        for (int[] dir : direcciones) {
            int nuevaFila = fila + dir[0];
            int nuevaColumna = columna + dir[1];
            
            if (nuevaFila >= 0 && nuevaFila < filas && nuevaColumna >= 0 && nuevaColumna < columnas 
                    && !visitado[nuevaFila][nuevaColumna]) {
                // Eliminar la pared entre la celda actual y la nueva
                if (dir[0] == -1) paredesHorizontales[fila][columna] = false;      
                else if (dir[0] == 1) paredesHorizontales[fila + 1][columna] = false; 
                else if (dir[1] == -1) paredesVerticales[fila][columna] = false;     
                else if (dir[1] == 1) paredesVerticales[fila][columna + 1] = false;     
                
                generarLaberintoDFS(nuevaFila, nuevaColumna, visitado);
            }
        }
    }
    
    // Método para mezclar un array (Fisher-Yates shuffle)
    private void mezclarArrays(int[][] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = (int)(Math.random() * (i + 1));
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    private void dibujarVidas() {
        // Dibujar fondo negro detrás de los textos y corazones
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fondo 2: Verde oscuro elegante (reemplaza al fondo negro lateral completo)
        shapeRenderer.setColor(0.180f, 0.490f, 0.196f, 1); // #2E7D32
        shapeRenderer.rect(600, 0, 250, 600);

        // Fondo 1: Gris claro (reemplaza los rectángulos grises individuales)
        shapeRenderer.setColor(0.941f, 0.941f, 0.941f, 1); // #F0F0F0
        shapeRenderer.rect(600, 500, 250, 100); // Nombre del jugador
        shapeRenderer.rect(600, 300, 250, 100); // Tiempo restante
        shapeRenderer.rect(600, 100, 250, 100); // Puntuación

        // Dibujar línea negra en el borde superior del rectángulo del nombre
        shapeRenderer.setColor(Color.BLACK);
        float grosorLinea = 2f; // Grosor de la línea en píxeles
        shapeRenderer.rect(600, 600 - grosorLinea, 250, grosorLinea); // Línea en y=600

        shapeRenderer.end();

        batch.begin();

        // Mostramos el nombre del jugador
        String nombre = juego1.getNombreUsuario();
        fuenteNombreJugador.setColor(Color.BLACK);
        fuenteNombreJugador.draw(batch, nombre, 665, 560);

        // Dibujamos el número del laberinto actual
        txtnumLaberinto.draw(batch, "LABERINTO " + numLaberinto, 650, 460);

        // Dibujamos el tiempo actual
        textoTiempo.setColor(Color.BLACK);
        textoTiempo.draw(batch, "Tiempo restante: " + tiempoRestante, 635, 355);

        // Dibujamos el número de vidas restantes
        textoVidas.draw(batch, "Vidas restantes: ", 640, 280);

        // Dibujamos la puntuación
        puntuacion.setColor(Color.BLACK);
        puntuacion.draw(batch, "Puntuación: " + puntuacionTotal, 655, 160);

        int yPos = 85;

        System.out.println("Top 3: " + top3.size());
        for (PuntuacionUsuario p : top3) {
            System.out.println(p.nombre + ": " + p.puntuacion);
        }

        for (int i = 0; i < top3.size(); i++) {
            PuntuacionUsuario p = top3.get(i);
            txtTop3.draw(batch, (i + 1) + ". " + p.nombre + ": " + p.puntuacion, 630, yPos);
            yPos -= 25;
        }

        // Dibujamos los corazones
        float x = 610;
        float y = 210;
        float anchoCorazon = 60;
        float altoCorazon = 60;

        for (int i = 0; i < 3; i++) {
            Texture corazonActual = (i < jugador.getVidas()) ? corazonLleno : corazonVacio;
            batch.draw(corazonActual, x + (i * anchoCorazon), y, anchoCorazon, altoCorazon);
        }

        batch.end();
    }
    
    private boolean haLlegadoASalida() {
        if (enTransicion) {
            return false; // Evitar detección durante la transición
        }
        
        // Obtener el centro del jugador
        float jugadorCentroX = jugador.getX() + jugador.getTamaño()/2;
        float jugadorCentroY = jugador.getY() + jugador.getTamaño()/2;
        
        // Área de la salida
        float salidaX = salidaColumna * tamañoCelda;
        float salidaY = salidaFila * tamañoCelda;
        
        // Incrementar numLaberinto solo si se confirma la llegada
        if (jugadorCentroX >= salidaX && jugadorCentroX <= salidaX + tamañoCelda && 
            jugadorCentroY >= salidaY && jugadorCentroY <= salidaY + tamañoCelda) {
            numLaberinto += 1;
            return true;
        }
        
        return false;
    }

    private void generarNuevoLaberinto() {
        enTransicion = true; // Activar bandera de transición
        laberintosCompletados++;
        puntuacionTotal += 1000 + tiempoRestante;
        tiempoInicial -= 10;

        int cantidadEnemigos = Math.min(1 + laberintosCompletados, 5);
        generarLaberinto();
        jugador.reiniciarPosicion(0, (filas - 1) * tamañoCelda);
        jugador.resetearAnimacion();
        inicializarEnemigos(cantidadEnemigos);
        tiempoRestante = tiempoInicial;
        tiempoAcumulado = 0f;
    }
    
    private void mostrarGameOver() {
        // Primero dibujar un fondo negro que cubra toda la pantalla
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        batch.begin();
        
        float anchoFinPartida = texturaFinPartida.getWidth(); 
        float altoFinPartida = texturaFinPartida.getHeight();
        batch.draw(texturaFinPartida, (Gdx.graphics.getWidth() - anchoFinPartida)/2, 400, anchoFinPartida, altoFinPartida);
        
        // Dibujar botón de guardar puntuación
        float anchoGuardar = texturaGuadarPunt.getWidth() * 0.8f; 
        float altoGuardar = texturaGuadarPunt.getHeight() * 0.8f;
        float xGuardar = 375;
        float yGuardar = 325;
        batch.draw(texturaGuadarPunt, xGuardar, yGuardar, anchoGuardar, altoGuardar);
        
        // Dibujar botón de reiniciar
        float anchoReiniciar = texturaReiniciar.getWidth() * 0.8f; 
        float altoReiniciar = texturaReiniciar.getHeight() * 0.8f;
        float xReiniciar = 100;
        float yReiniciar = 325;
        batch.draw(texturaReiniciar, xReiniciar, yReiniciar, anchoReiniciar, altoReiniciar);
        
        batch.end();

        // Detectar clics
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); 
            
            // Comprobar clic en botón de guardar puntuación
            if (touchX >= xGuardar && touchX <= xGuardar + anchoGuardar &&
                touchY >= yGuardar && touchY <= yGuardar + altoGuardar) {
                
                // Guardar la puntuación en la base de datos
                boolean exito = DBHelper.insertarPuntuacion(juego1.getNombreUsuario(), "LABERINTO", puntuacionTotal);
                
                if (exito) {
                    Gdx.app.log("Puntuación", "Puntuación guardada correctamente");
                } else {
                    Gdx.app.error("Puntuación", "Error al guardar la puntuación");
                }
                
                Gdx.app.exit();
            }
            
            // Comprobar clic en botón de reiniciar
            if (touchX >= xReiniciar && touchX <= xReiniciar + anchoReiniciar &&
                touchY >= yReiniciar && touchY <= yReiniciar + altoReiniciar) {
                reiniciarJuego();
            }
        }
        
        // Opción para reiniciar con tecla R
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            reiniciarJuego();
        }
    }
  
    private void inicializarEnemigos(int cantidad) {
        enemigosActivos.clear();
        
        Random random = new Random();
        
        for (int i = 0; i < cantidad; i++) {
            // Elegir una posición aleatoria en el laberinto
            int filaAleatoria = random.nextInt(filas);
            int columnaAleatoria = random.nextInt(columnas);
            
            // Asegurarnos de que no empiece en la celda del jugador ni en la salida
            while ((filaAleatoria == filas - 1 && columnaAleatoria == 0) || 
                   (filaAleatoria == salidaFila && columnaAleatoria == salidaColumna)) {
                filaAleatoria = random.nextInt(filas);
                columnaAleatoria = random.nextInt(columnas);
            }
            
            // Convertir a coordenadas de píxeles
            float x = columnaAleatoria * tamañoCelda;
            float y = filaAleatoria * tamañoCelda;
            
            // Asignar diferentes tipos de enemigos según el índice
            Enemigo.TipoEnemigo tipo;
            if (i % 3 == 0) {
                tipo = Enemigo.TipoEnemigo.DEMONIO;
            } else if (i % 3 == 1) {
                tipo = Enemigo.TipoEnemigo.OGRO;
            } else {
                tipo = Enemigo.TipoEnemigo.ZOMBIE;
            }
            
            enemigosActivos.add(new Enemigo(x, y, 64, 150 + random.nextInt(50), filas, columnas, tamañoCelda, tipo));
        }
    }
    
    private void reiniciarJuego() {
        laberintosCompletados = 0;
        jugador = new Jugador(0, (filas-1) * tamañoCelda, 64, 200, filas, columnas, tamañoCelda);
        inicializarEnemigos(1); 
        tiempoRestante = tiempoInicial;
        tiempoAcumulado = 0f;    
        puntuacionTotal = 0;
        numLaberinto = 1;
    }
    
    private void dibujarPantallaPausa() {
        // Animación de fade-in
        if (opacidadPausa < 1f) {
            opacidadPausa += Gdx.graphics.getDeltaTime() * 1f;
            if (opacidadPausa > 1f) opacidadPausa = 1f;
        }

        //Dibujar elementos del juego congelados
        Gdx.gl.glClearColor(0f, 0.55f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        dibujarLaberinto();
        //dibujarHitboxes();
        dibujarPersonajesYUI();
        dibujarVidas();
        
	    // Dibujar celda de salida (roja)
	    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	    shapeRenderer.setColor(Color.RED);
	    shapeRenderer.rect(
	        salidaColumna * tamañoCelda, 
	        salidaFila * tamañoCelda, 
	        tamañoCelda, 
	        tamañoCelda
	    );
	    shapeRenderer.end();
        
        // 2. Capa semitransparente oscura
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f * opacidadPausa);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // 3. Menú de pausa (botón Reanudar)
        batch.begin();
        batch.setColor(1f, 1f, 1f, opacidadPausa);
        
        float xImg = (Gdx.graphics.getWidth() - imagenReanudar.getWidth()) / 2f;
        float yImg = (Gdx.graphics.getHeight() - imagenReanudar.getHeight()) / 1.5f;
        batch.draw(imagenReanudar, xImg, yImg - 100);
        
        batch.setColor(1f, 1f, 1f, 1f);
        batch.end();
        


        // Detección de clic en "Reanudar"
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            
            Rectangle reanudarRect = new Rectangle(xImg, yImg - 100, imagenReanudar.getWidth(), imagenReanudar.getHeight());
            
            if (reanudarRect.contains(touchX, touchY)) {
                juegoPausado = false;
                opacidadPausa = 0f;
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    private void cargarTop3() {
        try {
            ResultSet rs = DBHelper.obtenerPuntuacionesPorJuegoLaberinto("LABERINTO");
            top3.clear();
            while (rs.next()) {
                String nombre = rs.getString("nombreUsuario");
                int puntuacion = rs.getInt("puntuacion");
                top3.add(new PuntuacionUsuario(nombre, puntuacion));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar top 3: " + e.getMessage());
        }
    }
    
	@Override
	public void dispose() {
        shapeRenderer.dispose();
        batch.dispose(); 
        jugador.dispose();
        for (Enemigo enemigo : enemigosActivos) {
            enemigo.dispose();
        }
        corazonLleno.dispose();
        corazonVacio.dispose();
        
        if(texturaFinPartida != null) texturaFinPartida.dispose();
        if(texturaGuadarPunt != null) texturaGuadarPunt.dispose();
        if(texturaReiniciar != null) texturaReiniciar.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Juego 1");
        config.setWindowedMode(800, 600);
        config.setWindowPosition(100, 100); 
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); 
        
        config.setResizable(false);
        config.disableAudio(true);
        
        config.setWindowIcon("assets/juego1/icono/LogoLaberinto.png");
        
        try {
            new Lwjgl3Application(new Juego1(), config);
        } catch (Exception e) {
            e.printStackTrace();
            Lwjgl3ApplicationConfiguration fallbackConfig = new Lwjgl3ApplicationConfiguration();
            fallbackConfig.setTitle("Juego 1 (Modo Fallback)"); 
            fallbackConfig.setWindowedMode(800, 600);
            new Lwjgl3Application(new Juego1(), fallbackConfig);
        }
    }
	
}
