package simuladorJJOO;

public class Participante {
	
	String nombre;
	String pais;

	public Participante(String nombre, String pais) {
		this.nombre = nombre;
		this.pais = pais;
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
	
	@Override
	public String toString() {
		return "Nombre: " + nombre + ", País: " + pais;
	}
	
}
