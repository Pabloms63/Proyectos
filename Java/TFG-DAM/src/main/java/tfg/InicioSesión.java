package tfg;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

/**
 * @author Pablo Marcos Sánchez
 */
public class InicioSesión extends JFrame {
	private static final long serialVersionUID = 1L;
	private Font fuente;
	private Font fuenteTitulo;
	public String nombreObtenido;
	private Main main; 
	
	private String imagenSeleccionada;
	
    private static final String URL = "jdbc:postgresql://dpg-d0mbre6uk2gs73fhbi40-a.frankfurt-postgres.render.com/tfgtorneovideojuegos";  //Ruta a la base de datos SQLite
    private static final String USER = "tfgtorneovideojuegos_user";
    private static final String PASSWORD = "rgfyWBNsND74pyKq2OviG2RSEgNmk5vt";

	public InicioSesión(Main main) {
		this.main = main;
		
		//Características principales
		setTitle("INICIO DE SESIÓN");
		setSize(600, 300);
		setResizable(false); 
		setLocationRelativeTo(null);
		
        //Cargamos el ícono
        Image iconoVentana = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(iconoVentana);
		
		//Inicializamos las fuentes
        fuente = new Font("Tiny5", Font.PLAIN, 20);
        fuenteTitulo = new Font("Tiny5", Font.PLAIN, 30);
        
        //Panel para el título con FlowLayout centrado
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(64, 133, 78)); 
        JLabel titulo = new JLabel("INICIO SESIÓN");
        titulo.setFont(fuenteTitulo);
        panelTitulo.add(titulo);
        
        //Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(new Color(64, 133, 78));
        GridBagConstraints gbc = new GridBagConstraints();
		
        //Formulario: Nombre
        JLabel nombre = new JLabel("Nombre: ");
        JTextField nombreTf = new JTextField(20); 
        nombre.setFont(fuente);
        nombreTf.setFont(fuente); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 10, 10, 10);  
        panelPrincipal.add(nombre, gbc);
        gbc.gridx = 1;
        panelPrincipal.add(nombreTf, gbc);
        
        //Formulario: Contraseña
        JLabel pswd = new JLabel("Contraseña: ");
        JPasswordField pswdTf = new JPasswordField(20); 
        pswd.setFont(fuente);
        pswdTf.setFont(fuente); 
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelPrincipal.add(pswd, gbc); 
        gbc.gridx = 1;
        panelPrincipal.add(pswdTf, gbc);
        
        //Botón de INICIO SESIÓN
        JButton boton = new JButton("Iniciar Sesión");
        boton.setFont(fuente);
        gbc.gridy = 3;  
        gbc.gridx = 0;
        gbc.gridwidth = 2;  
        gbc.insets = new Insets(20, 10, 20, 10); 
        panelPrincipal.add(boton, gbc);
        
        //Usar BorderLayout para agregar los paneles en las posiciones correctas
        setLayout(new BorderLayout());
        add(panelTitulo, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);
        
        boton.addActionListener(e->{
        	nombreObtenido = nombreTf.getText();
        	String pswdObtenida = new String(pswdTf.getPassword());
        	
        	comprobarUsuario(nombreObtenido, pswdObtenida, imagenSeleccionada);
        });
        
        //Hacer la ventana visible
        setVisible(true);
	}
	
	public String enviarNombreUsuario() {
		return nombreObtenido;	 
	}
	
	//Metodo para comprobar si el ususario existe
	public void comprobarUsuario(String nombre, String pswd, String imagenSeleccionada) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND contraseña = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
               PreparedStatement pstmt = conn.prepareStatement(sql)) {

               pstmt.setString(1, nombre);
               pstmt.setString(2, pswd); 

               try (ResultSet rs = pstmt.executeQuery()) {
                   if (rs.next()) {
                       JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso. Bienvenido, " + nombre);
                       imagenSeleccionada = rs.getString("imagen");
                       
                       main.setNombrePerfil(nombreObtenido);
                       main.setImagenPerfil(imagenSeleccionada);
                       main.habilitarBotonesJuegos(true); 
                       
                       dispose();
                   } else {
                       JOptionPane.showMessageDialog(this, "Nombre de usuario o contraseña incorrectos");
                   }
               }
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(this, "Error al comprobar el usuario: " + e.getMessage());
           }
    }
}
