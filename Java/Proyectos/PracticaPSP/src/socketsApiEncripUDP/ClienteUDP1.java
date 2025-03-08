package socketsApiEncripUDP;

import java.io.*;
import java.net.*;
import java.util.*;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class ClienteUDP1 {
    
	//Definimos las constantes
    private static final String ALGORITMO = "AES";
    private static final String TIPO_CIFRADO = "AES/CBC/PKCS5Padding";
    private static final byte[] CLAVE_CLIENTE = "miclaveCliebytes".getBytes();
    private static final byte[] CLAVE_SERVIDOR = "miclaveServbytes".getBytes();
    
    private static List<String> respuestas = new ArrayList<>();

    //Método principal
    public static void main(String[] args) throws Exception {
    	//Definimos el puerto del servidor
        final int PUERTO_SERVIDOR = 5000;
        Scanner scanner = new Scanner(System.in);
        byte[] buffer = new byte[1024];
        
        //Creamos una instancia del servidor http, pasándole por parámetro un InetSocketAddress con el puerto 'localhost:8080'
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

		//Creamos el contexto
		HttpContext context = server.createContext("/");

		//Al contexto le establecemos un manejador con su respectivo método
		context.setHandler(ClienteUDP1::respuestaManejador);

		//Arrancamos el servidor
		server.start();
		    
		System.out.println("Servidor HTTP iniciado");

        try {
        	//Creamos una instancia del InetAddress para obtener la dirección IP
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            //Creamos el socket
            DatagramSocket socketUDP = new DatagramSocket();

            //Creamos un bucle infinito, a no ser, que el cliente introduza 'salir'
            while (true) {  
                System.out.println("Ingrese el nombre de una ciudad (o 'salir' para terminar):");
                //El cliente introduce un mensaje
                String ciudad = scanner.nextLine();
                
                //Si el cliente introduce 'salir', salimos del bucle
                if (ciudad.equalsIgnoreCase("salir")) {
                    System.out.println("Terminando la aplicación...");
                    break;
                }
                
                //Encriptamos el mensaje introducido
                String mensajeEncriptado = encriptar(CLAVE_CLIENTE, new byte[16], ciudad);
                //Lo convertimos a bytes
                buffer = mensajeEncriptado.getBytes();
                //Creamos un paquete para enviar el mensaje al servidor
                DatagramPacket pregunta = new DatagramPacket(buffer, buffer.length, direccionServidor, PUERTO_SERVIDOR);
                socketUDP.send(pregunta);
                System.out.println("\nEnviado: " + mensajeEncriptado + "\n");

                //Volvemos a iniciar el buffer
                buffer = new byte[1024];
                //Creamos un paquete para recibir el mensaje con la respuesta del servidor
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(peticion);
                //Lo convertimos a String ya que viene en bytes
                String respuestaTiempo = new String(peticion.getData(), 0, peticion.getLength());
                //Lo desencriptamos
                String mensajeDesencrip = desencriptar(CLAVE_SERVIDOR, new byte[16], respuestaTiempo);
                System.out.println("Respuesta desencriptada: " + mensajeDesencrip);
                
                //Lo añadimos al array donde se acumularán todos los mensajes, para poder mostrarlos uno tras otro en el servidor http
                respuestas.add(mensajeDesencrip);
            }

            socketUDP.close();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
    
    //Método para mostrar la info. del servidor en el servidor http
	private static void respuestaManejador(HttpExchange exchange) throws IOException{
		//Creamos un StringBuilder para almacenar todas las respuestas del servidor
		StringBuilder respuestaBuilder = new StringBuilder();
		
		respuestaBuilder.append("TIEMPO EN LAS CIUDADES INTRODUCIDAS POR EL CLIENTE 1: ");
        
		//Recorremos todo el array
        for (String respuesta : respuestas) {
        	//Añadimos cada mensaje del servidor al stringbuilder entre '<p> y </p>' para poder convertirlo a formato html
        	respuestaBuilder.append("<p>").append(respuesta).append("</p>");
        }
		
        //Convertimos a String el StringBuilder
		String respuesta = respuestaBuilder.toString();

		//Configuramos la cabecera del html
		exchange.getResponseHeaders().set("Content-Type", "text/html");
		
		//Enviamos las cabeceras de la respuesta HTTP al cliente, especificando la longitud del contenido de la respuesta
		exchange.sendResponseHeaders(200, respuesta.getBytes().length);
		
		//Enviamos el contenido de la respuesta
		OutputStream os = exchange.getResponseBody();
		os.write(respuesta.getBytes());
		
		os.close();
	}

    // Método que encripta un mensaje bajo el algoritmo AES que recibe por parámetro la clave, un vector y el mensaje
    private static String encriptar(byte[] clave, byte[] vector, String texto) throws Exception {
    	//Instanciamos la clase cipher y le pasamos por parámetro el tipo de cifrado
        Cipher cipher = Cipher.getInstance(TIPO_CIFRADO);
        //Instanciamos la clase SecretKeySpec y le pasamos por parámetros la clave y el algoritmo en este caso 'AES'
        SecretKeySpec claveSecreta = new SecretKeySpec(clave, ALGORITMO);
        //Instanciamos la clase IvParameterSpec y le pasamos por parámetro el vector que recibe el propio método
        IvParameterSpec vectorEsperado = new IvParameterSpec(vector);
        
        //Le pasamos al cipher el modo encrypt, la clave y el vector
        cipher.init(Cipher.ENCRYPT_MODE, claveSecreta, vectorEsperado);
        
        //Creamos un array de bytes con el mensaje que recibe el método
        byte[] encriptado = cipher.doFinal(texto.getBytes());
        
        //Devolvemos el mensaje final codificado en Base64
        return new String(encodeBase64(encriptado));
    }

    // Método que encripta un mensaje bajo el algoritmo AES que recibe por parámetro la clave, un vector y el mensaje
    private static String desencriptar(byte[] clave, byte[] vector, String txtEncrip) throws Exception {
    	//Instanciamos la clase cipher y le pasamos por parámetro el tipo de cifrado
        Cipher cipher = Cipher.getInstance(TIPO_CIFRADO);
        //Instanciamos la clase SecretKeySpec y le pasamos por parámetros la clave y el algoritmo en este caso 'AES'
        SecretKeySpec claveSecreta = new SecretKeySpec(clave, ALGORITMO);
        //Instanciamos la clase IvParameterSpec y le pasamos por parámetro el vector que recibe el propio método
        IvParameterSpec vectorEsperado = new IvParameterSpec(vector);
        
        //Decodificamos el mensaje y lo guardamos en un array de bytes
        byte[] enc = decodeBase64(txtEncrip);
        //Le pasamos al cipher el modo decrypt, la clave y el vector
        cipher.init(Cipher.DECRYPT_MODE, claveSecreta, vectorEsperado);
        //Creamos un array de bytes con el mensaje que recibe el método ya desencriptado
        byte[] desencriptado = cipher.doFinal(enc);
        //Devolvemos el resultado
        return new String(desencriptado);
    }
}
