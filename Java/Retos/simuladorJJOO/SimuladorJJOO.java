package simuladorJJOO;

import java.io.Serializable;
import java.util.*;

public class SimuladorJJOO implements Serializable{
	/**
	 * Pablo Marcos Sánchez
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<String> eventos = new ArrayList<String>();
	ArrayList<Participante> participantes = new ArrayList<Participante>();
	HashMap<String, List<Participante>> participantesporEvento = new HashMap<String, List<Participante>>();
	HashMap<String, List<String>> medallasPorPais = new HashMap<String, List<String>>();
	
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) {
		SimuladorJJOO simuladorJJOO= new SimuladorJJOO();
		
		System.out.println("¡Bienvenido a los Juegos Olímpicos!");
		
		int num;
		Boolean condicional = true;
		
		do {
			System.out.println("\n1. Registro de eventos.");
			System.out.println("2. Registro de participantes.");
			System.out.println("3. Simulación de eventos.");
			System.out.println("4. Creación de informes.");
			System.out.println("5. Salir.\n");
			
			System.out.println("Introduzca un número del 1 al 5\n");
			num = scanner.nextInt();
			scanner.nextLine();
			
			switch (num) {
			case 1:
				simuladorJJOO.registrarEventos();
				break;
				
			case 2:
				simuladorJJOO.registrarParticipantes();
				break;
				
			case 3:
				simuladorJJOO.simularEventos();
				break;
				
			case 4:
				simuladorJJOO.crearInformes();
				break;
				
			case 5:
				System.out.println("- Hasta la próxima\n");
				condicional = false;
				break;

			default:
				System.out.println("Debe introducir un numero del 1 al 5");
				break;
			}

		} while (condicional);
	}
	
	public void registrarEventos() {
		System.out.println("\n- Introduzca el nombre del evento a registrar: \n");
		String nombreEvento = scanner.nextLine().strip();
		
		if(eventos.contains(nombreEvento)) {
			System.out.println("\n- El evento ya existe.\n");
		}else {
			System.out.println("\n- Evento registrado con éxito.\n");
			eventos.add(nombreEvento);
		}
		
		for(String evento : eventos) {
			System.out.println("\n" + evento);
		}
		
	}
	
	public void registrarParticipantes() {
		if (eventos.isEmpty()) {
			System.out.println("- No hay eventos registrados. REGISTRA UN EVENTO PRIMERO.");
		}else {
			System.out.println("\n- Introduzca el nombre del participante a registrar:");
			String nombreParticipante = scanner.nextLine().strip();
			
			System.out.println("\n- Introduzca el país del participante a registrar:");
			String paisParticipante = scanner.nextLine().strip();
			
			Participante participante = new Participante(nombreParticipante, paisParticipante);
			
			System.out.println("\n- Eventos disponibles :\n");
			for(String evento : eventos) {
				System.out.println("- " + evento);
			}
			
			System.out.println("\n- Introduzca el evento en el que quiere participar");
			String eventoParticipante = scanner.nextLine().strip();

			participantesporEvento.putIfAbsent(eventoParticipante, new ArrayList<>());
			participantesporEvento.get(eventoParticipante).add(participante);
			
			if (participantes.contains(participante)) {
			    System.out.println("\n- " + nombreParticipante + " ya está inscrito.");
			} else {
			    participantes.add(participante);
			    System.out.println("\n- " + nombreParticipante + " por " + paisParticipante + " registrado con éxito.\n");
			}
		}
		
		for(Participante prpt : participantes) {
			System.out.println(prpt);
		}
	}
	
	public void simularEventos() {
	    if (eventos.isEmpty()) {
	        System.out.println("\n- No hay eventos registrados. REGISTRA UN EVENTO PRIMERO.");
	    } else {
	        for (String evento : eventos) {
	            System.out.println("\nSimulando evento: " + evento);
	            
	            // Obtener la lista de participantes para el evento
	            List<Participante> participantesDelEvento = participantesporEvento.get(evento);
	            
	            if (participantesDelEvento != null && !participantesDelEvento.isEmpty()) {

	            	Collections.shuffle(participantesDelEvento, new Random());
	            	
	            	if (participantesDelEvento.size() >= 3) {
	            	    Participante primero = participantesDelEvento.get(0);
	            	    Participante segundo = participantesDelEvento.get(1);
	            	    Participante tercero = participantesDelEvento.get(2);
	                
		                medallasPorPais.putIfAbsent(primero.getPais(), new ArrayList<>());
		                medallasPorPais.get(primero.getPais()).add("ORO");
		                
		                medallasPorPais.putIfAbsent(segundo.getPais(), new ArrayList<>());
		                medallasPorPais.get(segundo.getPais()).add("PLATA");
		                
		                medallasPorPais.putIfAbsent(tercero.getPais(), new ArrayList<>());
		                medallasPorPais.get(tercero.getPais()).add("BRONCE");
		                
		                System.out.println(medallasPorPais);
		                
		                // Mostrar el ganador
		                System.out.println("\n* La medalla de oro en " + evento + " es para: " + primero.getNombre() + " de " + primero.getPais());
		                System.out.println("\n* La medalla de plata en " + evento + " es para: " + segundo.getNombre() + " de " + segundo.getPais());
		                System.out.println("\n* La medalla de bronce en " + evento + " es para: " + tercero.getNombre() + " de " + tercero.getPais() + "\n");
	            	}
	            } else {
	                System.out.println("\n- No hay suficientes participantes registrados para el evento " + evento);
	            }
	        }
	    }
	}
	
	public void crearInformes() {
		System.out.println("\nElije que quieres ver: ");
		
			System.out.println("1. Imprimir ganadores de cada evento");
			System.out.println("2. Imprimir ranking de países segun el número de medallas\n");
			
			int opcion = scanner.nextInt();
			
			switch (opcion) {
			case 1:
				if(medallasPorPais.isEmpty()) {
					System.out.println("Aun no ha sido simulado ningun evento");
				}else {
					for (String evento : eventos) {
						for (HashMap.Entry<String, List<String>> entry : medallasPorPais.entrySet()) {
						    if(entry.getValue().contains("ORO")) {
						    	System.out.println("\nGANADORES: ");
						    	System.out.println("- " + evento + " -> : " +  entry.getKey());
						    }
						}
					}
				}

				break;
				
			case 2:
				if(medallasPorPais.isEmpty()) {
					System.out.println("Aun no ha sido simulado ningun evento");
				}else {
					for (HashMap.Entry<String, List<String>> entry : medallasPorPais.entrySet()) {
					    String pais = entry.getKey();
					    List <String> medallas = entry.getValue();
					    
					    int oro = Collections.frequency(medallas, "ORO");
					    int plata = Collections.frequency(medallas, "PLATA");
					    int bronce = Collections.frequency(medallas, "BRONCE");
					    
					    System.out.println("- " + pais + ", oros: " + oro + ", platas: " + plata + ", bronces: " + bronce);
					}
				}
				
				break;

			default:
				System.out.println("Introduce un número válido (1 o 2)");
				break;
			}
	}
	
}
















