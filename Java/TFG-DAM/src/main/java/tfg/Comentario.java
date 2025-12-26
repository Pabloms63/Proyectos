package tfg;

import javax.swing.ImageIcon;

public class Comentario {
    private String nombre;
    private String fecha;
    private String texto;
    private ImageIcon icono;

    public Comentario(String nombre, String fecha, String texto, ImageIcon icono) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.texto = texto;
        this.icono = icono;
    }

    public String getNombre() { return nombre; }
    public String getFecha() { return fecha; }
    public String getTexto() { return texto; }
    public ImageIcon getIcono() { return icono; }
}

