package batallaMarvel;

import java.util.*;

public class DeadpoolVSLobezno {
	static Scanner scanner = new Scanner(System.in);
	Random random = new Random();

	public static void main(String[] args) throws InterruptedException {
		DeadpoolVSLobezno batalla = new DeadpoolVSLobezno();
		
		System.out.println("\n========BATALLA DEADPOOL VS WOLVERINE=========");
		
		batalla.combate();
	}

	public void combate() throws InterruptedException {
		int vidaDeadpool;
		int vidaLobezno;
		
		System.out.println("Antes de empezar tienes que asignar la vida a cada personaje:");
		
        do {
            System.out.println("Introduce la vida de Deadpool (Entre 500 y 2000): ");
            vidaDeadpool = scanner.nextInt();
        } while (vidaDeadpool < 500 || vidaDeadpool > 2000);

        do {
            System.out.println("Introduce la vida de Lobezno (Entre 500 y 2000): ");
            vidaLobezno = scanner.nextInt();
        } while (vidaLobezno < 500 || vidaLobezno > 2000);
        
        System.out.println("\n¡Comienza la batalla! 🔥⚔️");
		
        int contador = 1;
        
        //COMIENZO DEL COMBATE================================================================
		while(vidaDeadpool > 0 && vidaLobezno > 0) {
			boolean lobeznoDescansa = false;
			boolean deadpoolDescansa = false;
			
			System.out.println("---TURNO " + contador + "---");
			
			//Deadpool ataca primero
			int ataqueDeadpool = random.nextInt(10, 100);
			
			//Si el ataque de Lobezno es el mayor posible, saltamos turno
			if(ataqueDeadpool == 100) {
				lobeznoDescansa = true;
				System.out.println("💥 ¡El ataque de Lobezno es tan fuerte que Deadpool tiene que descansar un turno! 💀");
			}else {
				//Probabilidad de esquivo de Deadpool:
				int esquiveLobezno = random.nextInt(4);
					
				if(esquiveLobezno != 0) {
					vidaLobezno -= ataqueDeadpool;
					System.out.println("Deadpool ataca y le quita " + ataqueDeadpool + " puntos de vida a Lobezno");
					System.out.println("Vida restante de Lobezno: " + vidaLobezno);
				}else {
					System.out.println("Lobezno esquiva el ataque de Deadpool!");
				}
			}

			//Ahora ataca Lobezno
			int ataqueLobezno = random.nextInt(10 , 120);
			
			//Si el ataque de Deadpool es el mayor posible, saltamos turno
			if(ataqueLobezno == 120) {
				deadpoolDescansa = true;
				System.out.println("💥 ¡El ataque de Deadpool es tan fuerte que Lobezno tiene que descansar un turno! 💀");
			}else {
				//Probabilidad de esquivo de Deadpool:
				int esquiveDeadpool = random.nextInt(5);
				
				if(esquiveDeadpool != 0) {
					vidaDeadpool -= ataqueLobezno;
					System.out.println("Lobezno ataca y le quita " + ataqueLobezno + " puntos de vida a Deadpool");
					System.out.println("Vida restante de Deadpool: " + vidaDeadpool);
				}else {
					System.out.println("Deadpool esquiva el ataque de Lobezno!");
				}
			}

			if(vidaDeadpool <= 0) {
				System.out.println("\nLobezno ha ganado la batalla");
				System.out.println("\nDeadpool -> " + vidaDeadpool + " puntos de vida");
				System.out.println("\nLobezno -> " + vidaLobezno + " puntos de vida");
			}else if(vidaLobezno <= 0){
				System.out.println("\nDeadpool ha ganado la batalla");
				System.out.println("\nDeadpool -> " + vidaDeadpool + " puntos de vida");
				System.out.println("\nLobezno -> " + vidaLobezno + " puntos de vida");
			}	
			
			Thread.sleep(1000);
			
			contador++;
		}
	}
}
