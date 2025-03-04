package concesionario;

import java.io.*;
import java.util.*;
import com.db4o.*;

public class GestorConcesionario {
	//Guardamos todas las rutas de los futuros archivos en variables globales
	private String nombreFicheroPROP = "D:\\javam\\ConcesionarioPROP\\concesionario.props";
	private String nombreFicheroFWFR = "D:\\javam\\ConcesionarioFWYFR\\concesionarioFWYFR.txt";
	private String nombreFicheroRAF = "D:\\javam\\ConcesionarioRAF\\concesionarioRAF.dat";
	private String nombreFicheroSerializable = "D:\\javam\\ConcesionarioSerializable\\concesionarioSerializbale.obj";
	private String nombreFicheroDB4O = "D:\\javam\\ConcesionarioDB4O\\concesionario.db4o";
	
	/*Instanciamos el arraylist que usaremos para todo el codigo, la clase scanner para poder recibir 
	mensajes del usuario y la clase properties que se usará para el fichero de configuración*/
	ArrayList<Coche> coches = new ArrayList<Coche>();
	static Scanner scanner = new Scanner(System.in);
	Properties configuracion = new Properties();

	//Método principal
	public static void main(String[] args) throws FileNotFoundException {
		//Instanciamos la propia clase para no hacer los métodos staticos
		GestorConcesionario concesionario = new GestorConcesionario();
		
		int numero;
		
		//Bucle con el menú y el switch con la función correspondiente en cada caso
		do {
			//MENU CONCESIONARIO
			System.out.println("\n\nMENU DEL CONCESIONARIO-----------------");
			System.out.println("1. Mostrar todos los coches en el inventario");
			System.out.println("2. Buscar coche por marca");
			System.out.println("3. Buscar coche por modelo");
			System.out.println("4. Añadir un nuevo coche al inventario");
			System.out.println("5. Actualizar información de un coche");
			System.out.println("6. Eliminar un coche del inventario");
			System.out.println("7. Recuperar datos");
			System.out.println("8. Guardar datos");
			System.out.println("9. Compactar datos");
			System.out.println("10. Salir");
			
			//Dependiendo del numero que introduzca el usuario entrará en un caso del switch
			numero = scanner.nextInt();
			scanner.nextLine();
			
			//En caso de que el numero sea 8, salimos del bucle. Y si es un número distinto de 1-8 se ejecuta la opcion default.
			switch(numero) {
				case 1:
					concesionario.verInventario();
					break;
					
				case 2:
					concesionario.buscarPorMarca();
					break;
					
				case 3:
					concesionario.buscarPorModelo();
					break;
				
				case 4:
					concesionario.añadirCoche();
					break;
					
				case 5:
					concesionario.actualizarCoche();
					break;
					
				case 6:
					concesionario.eliminarCoche();
					break;
					
				case 7:
					concesionario.recuperarDatos();
					break;
					
				case 8:
					concesionario.guardarDatos();
					break;
					
				case 9:
					concesionario.compactarDatos();
					break;
					
				case 10:
					System.out.println("Has salido del programa");
					break;
				
				default:
					System.out.println("Pruebe con otro número");	
			}

		//Si el número es 10, salimos
		} while(numero != 10);
		
		scanner.close();
	}


	//Método para mostrar todo lo que tiene el array 'coches'
	private void verInventario() {
		//Si el array no está vacío, lo mostramos
		if(!coches.isEmpty()) {
			for(Coche coche : coches) {
				System.out.println(coche);
			}
		//Si está vacío mostramos un mensaje por consola
		}else {
			System.out.println("El inventario está vacío");
		}
	}
	
	//Método para filtrar los coches por marca 
	private void buscarPorMarca() {
		System.out.println("Marca del coche: ");
		//El usuario introduce una marca
		String marcaCoche = scanner.nextLine();
		
		//Recorremos el array 'coches'
		for(Coche coche : coches) {
			//Si la marca que pone el usuario coincide con alguna del array, se muestra
			if(coche.getMarca().contains(marcaCoche)){
				System.out.println("Coches de la marca " + marcaCoche + " en nuestro concesionario:");
			}
		}
		
		//Si la marca que pone el usuario coincide con alguna del array, se muestra
		for(Coche coche : coches) {
			if(coche.getMarca().contains(marcaCoche)) {

				System.out.println(coche.getMarca() + " " + coche.getModelo() + " " + coche.getAño() + " " + coche.getPrecio());
			}
		}
	}
	
	//Método para filtrar por modelo
	private void buscarPorModelo() {
		//Pedimos por consola el nombre del modelo
		System.out.println("Modelo del coche: ");
		//El usuario introduce un modelo
		String modeloCoche = scanner.nextLine();
		
		//Si el modelo que pone el usuario coincide con alguno del array, se muestra
		for(Coche coche : coches) {
			if(coche.getModelo().contains(modeloCoche)){
				System.out.println("Coches del modelo " + modeloCoche + " en nuestro concesionario:");
			}
			
		}
		
		//Si el modelo que pone el usuario coincide con alguno del array, se muestra
		for(Coche coche : coches) {
			if(coche.getModelo().contains(modeloCoche)) {
				System.out.println(coche.getMarca() + " " + coche.getModelo() + " " + coche.getAño() + " " + coche.getPrecio());
			}
		}
	
	}
	
	//Método para añadir coches al array
	private void añadirCoche() {
		//El usuario introduce todos los atributos del coche
		System.out.println("Marca del coche: ");
		String marcaCoche = scanner.nextLine();		
		
		System.out.println("Modelo del coche: ");
		String modeloCoche = scanner.nextLine();
		
		System.out.println("Año del coche: ");
		int añoCoche = Integer.valueOf(scanner.nextLine());
		
		System.out.println("Precio del coche: ");
		int precioCoche = Integer.valueOf(scanner.nextLine());
		
		//Creamos el coche con los atributos que ha puesto el usuario
		Coche coche = new Coche(marcaCoche, modeloCoche, añoCoche, precioCoche);
		
		//Lo añadimos al array
		coches.add(coche);
		
	}
	
	//Método que actualiza los coches del array
	private void actualizarCoche() {
		//El usuario introduce todos los atributos del coche
		System.out.println("Marca del coche a actualizar: ");
		String marca = scanner.nextLine();
		
		System.out.println("Modelo del coche a actualizar: ");
		String modelo = scanner.nextLine();
		
		System.out.println("Año del coche a actualizar: ");
		int año = Integer.valueOf(scanner.nextLine());
		
		System.out.println("Precio del coche a actualizar: ");
		int precio = Integer.valueOf(scanner.nextLine());
		
		int numero;
		
		//Bucle con un menú y un switch
		do {
			//MENU CONCESIONARIO
			System.out.println("\n¿Qué atributo del coche quieres actualizar?");
			System.out.println("1. Actualizar la marca");
			System.out.println("2. Actualizar el modelo");
			System.out.println("3. Actualizar el año");
			System.out.println("4. Actualizar el precio");
			System.out.println("5. No quiere actualizar nada más");
			
			//Dependiendo del numero que introduzca el usuario entrará en un caso del switch
			numero = scanner.nextInt();
			scanner.nextLine();
			
			//En caso de que el numero sea 8, salimos del bucle. Y si es un número distinto de 1-8 se ejecuta la opcion default.
			switch(numero) {
				case 1:
					System.out.println("Nombre de la nueva marca: ");
					String marcaNueva = scanner.nextLine();
					for(Coche coche : coches) {
						//Si el coche que ha puesto el usuario coincide con alguno del array, se cambia la marca vieja por la nueva
						if(coche.getMarca().contains(marca) && coche.getModelo().contains(modelo) && coche.getAño() == año && coche.getPrecio() == precio) {
							coche.setMarca(marcaNueva);
						}
					}
					break;
					
				case 2:
					System.out.println("Nombre del nuevo modelo: ");
					String modeloNuevo = scanner.nextLine();
					for(Coche coche : coches) {
						//Si el coche que ha puesto el usuario coincide con alguno del array, se cambia el modelo viejo por el nuevo
						if(coche.getMarca().contains(marca) && coche.getModelo().contains(modelo) && coche.getAño() == año && coche.getPrecio() == precio) {
							coche.setModelo(modeloNuevo);
						}
					}
					break;
					
				case 3:
					System.out.println("Nombre del nuevo año: ");
					int añoNuevo = Integer.valueOf(scanner.nextLine());
					for(Coche coche : coches) {
						//Si el coche que ha puesto el usuario coincide con alguno del array, se cambia el año viejo por el nuevo
						if(coche.getMarca().contains(marca) && coche.getModelo().contains(modelo) && coche.getAño() == año && coche.getPrecio() == precio) {
							coche.setAño(añoNuevo);
						}
					}
					break;
				
				case 4:
					System.out.println("Nombre del nuevo precio: ");
					int precioNuevo = Integer.valueOf(scanner.nextLine());
					for(Coche coche : coches) {
						//Si el coche que ha puesto el usuario coincide con alguno del array, se cambia el precio viejo por el nuevo
						if(coche.getMarca().contains(marca) && coche.getModelo().contains(modelo) && coche.getAño() == año && coche.getPrecio() == precio) {
							coche.setPrecio(precioNuevo);
						}
					}
					break;
					
				case 5:
					System.out.println("Coche actualizado");
					break;
				
				default:
					System.out.println("Pruebe con otro número");	
			}

		//Si el numero es 5, salimos
		} while(numero != 5);
		
	}

	//Método para cambiar el atributo 'eliminado' de los coches del array
	private void eliminarCoche() {
		//El usuario introduce todos los atributos del coche
		System.out.println("Marca del coche a eliminar: ");
		String marca = scanner.nextLine();
		
		System.out.println("Modelo del coche a eliminar: ");
		String modelo = scanner.nextLine();
		
		System.out.println("Año del coche a eliminar: ");
		int año = Integer.valueOf(scanner.nextLine());
		
		System.out.println("Precio del coche a eliminar: ");
		int precio = Integer.valueOf(scanner.nextLine());

		for(Coche coche : coches) {
			//Si el coche que ha puesto el usuario coincide con alguno del array, se cambia el atributo eliminado a true
			if(coche.getMarca().contains(marca) && coche.getModelo().contains(modelo) && coche.getAño() == año && coche.getPrecio() == precio) {
				coche.setEliminado(true);
				System.out.println("Coche eliminado");
			}
		}
	}
	
	//Método para elegir la manera de guardar los archivos
	private void guardarDatos() {
		int opcion;
			//MENU ELECCION GUARDADO	
			System.out.println("¿Qué método de guardado deseas utilizar?");
			System.out.println("1. Properties (Fichero de configuración)");
			System.out.println("2. Texto plano");
			System.out.println("3. RAF (RandomAccessFile)");
			System.out.println("4. Serializable");
			System.out.println("5. DB4O");
			System.out.println("6. Salir");
			
			opcion = scanner.nextInt();
			scanner.nextLine();
			
			switch(opcion) {
			case 1:
				guardarProperties();
				break;
				
			case 2: 
				guardarTextoPlano();
				break;
				
			case 3: 
				guardarRAF();
				break;
				
			case 4:
				guardarSerializable();
				break;
				
			case 5: 
				guardarDB4O();
				break;
				
			case 6: 
				break;
				
			default:
				System.out.println("Introduzca un número válido (1-6)");
		}	
	}
	
	//Método para elegir la forma de recuperar los datos
	private void recuperarDatos() throws FileNotFoundException {
		int opcion;
		
			System.out.println("¿Qué concesionario quieres recuperar?");
			System.out.println("1. Properties");
			System.out.println("2. Texto plano (FileWriter/ FileReader)");
			System.out.println("3. RAF (RandomAccessFile)");
			System.out.println("4. Serializable");
			System.out.println("5. DB4O");
			
			opcion = scanner.nextInt();
			scanner.nextLine();
			
			switch(opcion) {
			case 1:
				recuperarProperties();
				break;
				
			case 2: 
				recuperarTextoPlano();
				break;
				
			case 3: 
				recuperarRAF();
				break;
				
			case 4:
				recuperarSerializable();
				break;
				
			case 5: 
				recuperarDB4O();
				break;
				
			default:
				System.out.println("Introduzca un número válido (1-5)");			
			}
	}
	
	//Método para compactar el array, borrando los coches eliminados
	private void compactarDatos() {
		if (!coches.isEmpty()) {
		    Iterator<Coche> iterador = coches.iterator();
		    while (iterador.hasNext()) {
		        Coche coche = iterador.next();
		        if (coche.isEliminado()) {
		            iterador.remove(); // Usar el método remove() del iterador
		        }
		    }
		    System.out.println("Concesionario compactado");
		} else {
		    System.out.println("Debes tener algún coche en el inventario para poder compactar");
		}

		
	}
	
	//Método para guardar los datos del array en un archivo de configuración
	private void guardarProperties() {
		int contador = 1;
		
		//Recorremos el array y establecemos las propiedades (con un contador autoincrementable para que no se sobreescriba)
		for(Coche coche: coches) {
			configuracion.setProperty("Coche" + contador + ".marca", coche.getMarca());
			configuracion.setProperty("Coche" + contador + ".modelo", coche.getModelo());
			configuracion.setProperty("Coche" + contador + ".anio", String.valueOf(coche.getAño()));
			configuracion.setProperty("Coche" + contador + ".precio", String.valueOf(coche.getPrecio()));
			configuracion.setProperty("Coche" + contador + ".eliminado", Boolean.toString(coche.isEliminado()));
			
			contador++;
		}
		
		//Guardamos y escribimos las propiedades
		try (FileOutputStream fos = new FileOutputStream(nombreFicheroPROP)) {
            configuracion.store(fos, "Concesionario");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		System.out.println("¡Coches guardados!");
	}
	
	//Método para guardar los datos del array en un archivo de texto plano
	private void guardarTextoPlano() {
		//Instanciamos printwriter y filewriter
		FileWriter fichero = null;
		PrintWriter escritor = null;
		
		try {
			fichero = new FileWriter(nombreFicheroFWFR, true);
			escritor = new PrintWriter(fichero);
			
			//Escribimos en el fichero concreto cada coche
			for(Coche coche : coches) {
				escritor.println(coche);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(fichero != null) {
			System.out.println("¡Coches guardados!");
			escritor.close();
		}
		
	}

	//Método para guardar los datos del array con RandomAccessFile
	private void guardarRAF() {
		try {
			//Instanciamos la clase RAF, le pasamos el fichero, y 'rw' que quiere decir lectura y escritura
			RandomAccessFile raf = new RandomAccessFile(nombreFicheroRAF, "rw");
			
			//Recorremos el array
			for(Coche coche : coches) {
				//Escribimos el primer atributo del coche, que es un string
				StringBuffer sbMarca = new StringBuffer(coche.getMarca());
				sbMarca.setLength(10); //Le asignamos un tamaño de 10 bytes
				raf.writeChars(sbMarca.toString());
				
				//Escribimos el segundo atributo del coche, que es un string
				StringBuffer sbModelo = new StringBuffer(coche.getModelo());
				sbModelo.setLength(10); //Le asignamos un tamaño de 10 bytes
				raf.writeChars(sbModelo.toString());
				
				//Escribimos el año, precio y el boolean eliminado
				raf.writeInt(coche.getAño());
				raf.writeInt(coche.getPrecio());
				raf.writeBoolean(coche.isEliminado());
			}
			
			
			if(nombreFicheroRAF != null) {
				System.out.println("¡Coches guardados!");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	//Método para guaradar los datos del array con la interfaz Serializable
	private void guardarSerializable() {
		try {
			//Instanciamos estas dos clases
			FileOutputStream fos = new FileOutputStream(nombreFicheroSerializable);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			//Gracias al método writeObject podemos escribir directamente objetos en el fichero
			oos.writeObject(coches);
			oos.close();
			
			if(nombreFicheroSerializable != null) {
				System.out.println("¡Coches gurardados!");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Método para guaradar los datos del array en una base de datos de objetos (DB40)
	private void guardarDB4O() {
		//Instanciamos la clase que contendrá la base de datos y le asignamos el fichero
		ObjectContainer db = Db4oEmbedded.openFile(nombreFicheroDB4O);
		
		//Guardamos todos los coches del array
		for(Coche coche : coches) {
			db.store(coche);
		}
		
		if(db != null) {
			db.close();
			System.out.println("¡Coches guardados en " + nombreFicheroDB4O + "!");
		}
	}
		
	//Método para recuperar la info. del archivo de configuración y guardarla en el array
	private void recuperarProperties() {
		File fichero = new File(nombreFicheroPROP);
		
		if(fichero.exists()) {
	        try (FileInputStream fis = new FileInputStream(nombreFicheroPROP)) {
	            configuracion.load(fis);
	
	            //Recuperamos los nombres de las propiedades
	            Set<String> nombreProperties = configuracion.stringPropertyNames();
	            int contador = 1;
	            
	            //Gracias al contador diferenciamos los coches
	            while(nombreProperties.contains("Coche" + contador + ".marca")) {
	                // Obtenemos las propiedades
	                String marca = configuracion.getProperty("Coche" + contador + ".marca");
	                String modelo = configuracion.getProperty("Coche" + contador + ".modelo");
	                int year = Integer.valueOf(configuracion.getProperty("Coche" + contador + ".anio"));
	                int precio = Integer.valueOf(configuracion.getProperty("Coche" + contador + ".precio"));
	                Boolean eliminado = Boolean.valueOf(configuracion.getProperty("Coche" + contador + ".eliminado"));
	                
	                //Creamos el coche
	                Coche coche = new Coche(marca, modelo, year, precio, eliminado);
	                //Lo añadimos al array
	                coches.add(coche);
	                
	                contador++;
	            }
	
	            System.out.println("¡Concesionario recuperado!");
	
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}else {
			System.out.println("No existe ningun fichero de configuración que se pueda recuperar");
		}
	}
	
	//Método para recuperar la info. del archivo de texto plano y añadirla al array
	private void recuperarTextoPlano() {
		BufferedReader buffer = null;
		
		File fichero = new File(nombreFicheroFWFR);
		
		if(fichero.exists()) {
			try {
				//Creamos un buffer y le pasamo el fichero
				buffer = new BufferedReader(new FileReader(nombreFicheroFWFR));
				//Obtenemos la primera linea del fichero 
				String linea = buffer.readLine();
				
				//Si la linea no es nula
				while(linea != null){
					//Creamos una lista de atributos (Que son cada palabra separada por comas)
					String datos[] = linea.split(",");
					
					//Si el tamaño de la la lista es 5:
					if(datos.length == 5) {
						//Quitamos los espacios y el relleno de cada atributo
						String marca = datos[0].trim();
						String modelo = datos[1].trim();
						int año = Integer.valueOf(datos[2].trim());
						int precio = Integer.valueOf(datos[3].substring(0, (datos[3].length()) - 1).trim());
						boolean eliminado = Boolean.valueOf(datos[4].substring(12).trim());
						
						//Creamos un coche con los datos obtenidos
						Coche coche = new Coche(marca, modelo, año, precio, eliminado);	
						//Lo añadimos al array
						coches.add(coche);
						
						System.out.println("¡Concesionario recuperado!");
					}else {
						System.out.println("Error al recuperar");
						break;
					}
					
					//Pasamos a la siguiente linea
					linea = buffer.readLine();
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("No existe ningun fichero de texto plano que se pueda recuperar");
		}
		
	}
	
	//Método para recuperar los datos del fichero aleatorio y guardarlos en el array
	private void recuperarRAF() {
		File fichero = new File(nombreFicheroRAF);
		
		if(fichero.exists()) {
			try {
				//Instanciamos la clase RAF, le pasamos el fichero y el modo de solo lectura
				RandomAccessFile raf = new RandomAccessFile(nombreFicheroRAF, "r");

				//Nos recorremos el fichero
				while (raf.getFilePointer() < raf.length()) {
					//Obtenemos el primer valor (la marca) que siempre va a ser de 10 bytes
					char[] marcaChar = new char[10];
					for(int a = 0; a < marcaChar.length; a++) {
						marcaChar[a] = raf.readChar();
					}
					String marca = new String(marcaChar).trim();
					
					//Obtenemos el segundo valor (el modelo) que siempre va a ser de 10 bytes
					char[] modeloChar = new char[10];
					for(int a = 0; a < modeloChar.length; a++) {
						modeloChar[a] = raf.readChar();
					}
					String modelo = new String(modeloChar).trim();
					
					//Obtenemos los 3 siguientes valores en el orden en el que los guardamos
					int año = raf.readInt();
					int precio = raf.readInt();
					boolean eliminado = raf.readBoolean();
					
					//Creamos un coche con los datos obtenidos
					Coche coche = new Coche(marca, modelo, año, precio, eliminado);
					coches.add(coche); //Lo añadimos al array
				}
				
				System.out.println("¡Concesionario recuperado!");
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("No existe ningun archivo guardado con RAF que se pueda recuperar");
		}
	}
	
	//Método para recuperar los datos del fichero de objetos y añadirlos en el array
	private void recuperarSerializable() {
		File fichero = new File(nombreFicheroSerializable);
		
		if(fichero.exists()) {
			try {
				//Instanciamos estas dos clases
				FileInputStream fis = new FileInputStream(nombreFicheroSerializable);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				//Recuperamos todos los objetos a la vez y los metemos en el array
				coches = (ArrayList<Coche>) ois.readObject();

				ois.close();
				
				System.out.println("¡Concesionario recuperado!");
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("No existe ningun archivo serializable que se pueda recuperar");
		}
	}
	
	//Método para recuperar los datos de la bbdd y meterlos en el array
	private void recuperarDB4O() {
		//Creamos un coche de ejemplo sin ningun atributo
		Coche ejemplo = new Coche();
				
		File archivo = new File(nombreFicheroDB4O);
				
		ObjectContainer db = null; 
		
			try {
				//Abrimos el fichero
				db = Db4oEmbedded.openFile(nombreFicheroDB4O);
					
				//Inicializamos una lista de objetos Coche llamada 'resultado'
			       ObjectSet<Coche> resultado = db.queryByExample(ejemplo);
			        
			    //En caso de que resultado esté vacío se mostrará un mensaje por consola
			    if(!archivo.exists()) {
			        System.out.println("El archivo no existe");
			    //Si resultado no está vacío se muestra toda la información de este
			    }else {
			    	System.out.println("¡Concesionario recuperado!");
			        while (resultado.hasNext()) {
			            Coche coche = resultado.next();
			            coches.add(coche);
			        }
			    }
			    
			}catch(Exception e) {
				e.printStackTrace();
				
			}finally{
				if(db != null) {
					db.close();
				}
			}
	}
}