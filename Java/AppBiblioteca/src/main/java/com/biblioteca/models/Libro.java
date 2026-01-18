package com.biblioteca.models;

public class Libro {
	private String id;
	private String titulo;
	private String autor;
	private String estado;
	private String genero;
	
	public Libro() {}
	
	public Libro(String id, String titulo, String autor, String estado, String genero) {
		this.id = id;
		this.titulo = titulo;
		this.autor = autor;
		this.estado = estado;
		this.genero = genero;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getGenero() {
		return genero;
	}
}
