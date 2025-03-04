package concesionario;

import java.io.Serializable;

public class Coche implements Serializable{

	//Atributos
	private String marca;
	private String modelo;
	private int año;
	private int precio;
	private boolean eliminado;
	
	//Constructor vacío que usaremos para hacer consultas en DB4O
	public Coche() {}
	
	//Constructor con 4 atributos
	public Coche(String marca, String modelo, int año, int precio) {
		this.marca = marca;
		this.modelo = modelo;
		this.año = año;
		this.precio = precio;
	}
	
	//Constructor completo
	public Coche(String marca, String modelo, int año, int precio, boolean eliminado) {
		this(marca, modelo, año, precio);
		this.eliminado = eliminado;
	}

	//Getter y Setter
	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public int getAño() {
		return año;
	}

	public void setAño(int año) {
		this.año = año;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}
	
	public boolean isEliminado() {
		return eliminado;
	}

	public void setEliminado(boolean eliminado) {
		this.eliminado = eliminado;
	}

	//Método toString() sobreescrito
	@Override
	public String toString() {
		return getMarca() + ", " + getModelo() + ", " + getAño() + ", " + getPrecio() + "€" + ", Eliminado: " + isEliminado();
	}


	
}
