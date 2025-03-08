package socketsApiEncripUDP;

import java.io.IOException;
import java.net.*;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServidorUDP implements Runnable {

	//Definimos las constantes
    private static final String ALGORITMO = "AES";
    private static final String TIPO_CIFRADO = "AES/CBC/PKCS5Padding";
    private static final byte[] CLAVE_SERVIDOR = "miclaveServbytes".getBytes();
    private static final byte[] CLAVE_CLIENTE = "miclaveCliebytes".getBytes();

    private DatagramSocket socketUDP;
    private DatagramPacket peticion;

    public ServidorUDP(DatagramSocket socket, DatagramPacket peticion) {
        this.socketUDP = socket;
        this.peticion = peticion;
    }

    //Método principal
    public static void main(String[] args) {
    	//Definimos el puerto
        final int PUERTO = 5000;
        
        //Le asignamos el puerto al socket
        try (DatagramSocket socketUDP = new DatagramSocket(PUERTO);) {
            System.out.println("Iniciado el servidor UDP...");

            //Siempre atendera peticiones
            while (true) {
            	//Creamos un array con un tamaño de 1024
                byte[] buffer = new byte[1024];
                
                //Creamos el paquete que recibe el mensaje del cliente
                DatagramPacket peticion = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(peticion);
                
                //Iniciamos el hilo
                new Thread(new ServidorUDP(socketUDP, peticion)).start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            // Convierto el mensaje que recibo del cliente en String
            String mensaje = new String(peticion.getData(), 0, peticion.getLength());
            System.out.println("Mensaje del cliente encriptado: " + mensaje);
            System.out.println("Desencriptando...");
            
            //Desencriptamos este mensaje con el método 'desencriptar' y lo guardamos en otro String
            String desencriptado = desencriptar(CLAVE_CLIENTE, new byte[16], mensaje);
            System.out.println("Mensaje del cliente desencriptado: " + desencriptado + "\n");

            //Si el mensaje del cliente es 'salir' mostramos el mensaje de que el cliente se ha desconectado
            if (desencriptado.equalsIgnoreCase("salir")) {
                System.out.println("El cliente ha salido");
            } else {   //Si el mensaje es cualquier otra cosa menos 'salir';
            	//Instanciamos la clase OkHttpCliente
                OkHttpClient cliente = new OkHttpClient();
                
                //Creamos la respuesta del api
                Request peticion1 = new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/weather?q=" + desencriptado + "&appid=4cab5222d32735a9ccb7011dd06f99d4")
                        .build();
                Response respuesta1 = cliente.newCall(peticion1).execute();
                String jsonRespuesta1 = respuesta1.body().string();

                //Convertimos la respuesta del api a String gracias al método 'imprimirInfoTiempo'
                String respuestaTiempo = imprimirInfoTiempo(desencriptado, jsonRespuesta1);
                //La encriptamos
                String respuestaEncriptada = encriptar(CLAVE_SERVIDOR, new byte[16], respuestaTiempo);

                //Guardamos la respuesta encriptada en bytes
                byte[] mensajeRespuesta = respuestaEncriptada.getBytes();
                //Y se la enviamos al cliente gracias a un DatagramPacket
                DatagramPacket respuesta = new DatagramPacket(mensajeRespuesta, mensajeRespuesta.length, peticion.getAddress(), peticion.getPort());
                socketUDP.send(respuesta);
                System.out.println("Enviando la respuesta encriptada al cliente...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para imprimir la información del tiempo
    private static String imprimirInfoTiempo(String ciudad, String jsonResponse) throws JSONException {
    	//Instanciamos la clase que nos va a permitir convertir el json a String, y le pasamos por parámetro la respuesta del api en formato json
        JSONObject jsonObject = new JSONObject(jsonResponse);
        
        //Obtenemos todos los apartados que queremos del json (haciendo una especie de consultas)
        String descripcionTiempo = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperatura = jsonObject.getJSONObject("main").getDouble("temp");
        double humedad = jsonObject.getJSONObject("main").getDouble("humidity");
        double temp_min = jsonObject.getJSONObject("main").getDouble("temp_min");
        double temp_max = jsonObject.getJSONObject("main").getDouble("temp_max");
        double velocidad = jsonObject.getJSONObject("wind").getDouble("speed");
        double presion = jsonObject.getJSONObject("main").getDouble("pressure");

        //Devolvemos un string con los valores obtenidos
        return "\nTiempo en " + ciudad + ":\n" +
                "Descripcion: " + descripcionTiempo + "\n" +
                "Temperatura: " + temperatura + " Kelvin\n" +
                "Humedad: " + humedad + "%\n" +
                "Temperatura minima: " + temp_min + " Kelvin\n" +
                "Temperatura maxima: " + temp_max + " Kelvin\n" +
                "Velocidad del viento: " + velocidad + " m/s\n" +
                "Presion atmosferica: " + presion + " hPa\n" +
                "-------------\n";
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
