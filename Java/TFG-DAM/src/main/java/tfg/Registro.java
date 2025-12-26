package tfg;

import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Pablo Marcos Sánchez
 */
public class Registro extends JFrame {
    private static final long serialVersionUID = 1L;
    private Font fuente;
    private Font fuenteTitulo;
    
    private JTextField nombreTf, correoTf;
    private JPasswordField pswdTf;
    
    private String imagenSeleccionada; 
    
	private Main nuevaVentana;

    //Conexión a la base de datos SQLite
    private static final String URL = "jdbc:postgresql://dpg-d0mbre6uk2gs73fhbi40-a.frankfurt-postgres.render.com/tfgtorneovideojuegos";  //Ruta a la base de datos SQLite
    private static final String USER = "tfgtorneovideojuegos_user";
    private static final String PASSWORD = "rgfyWBNsND74pyKq2OviG2RSEgNmk5vt";

    public Registro(Main nuevaVentana) { 
    	this.nuevaVentana = nuevaVentana;
    	
        //Creamos la tabla si no existe
        DBHelper.crearTablas();

        //Características principales
        setTitle("REGISTRO");
        setSize(700, 450); 
        setResizable(false);
        setLocationRelativeTo(null);
        
        //Cargamos el ícono
        Image iconoVentana = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(iconoVentana);

        //Inicializar las fuentes
        fuente = new Font("Tiny5", Font.PLAIN, 20);
        fuenteTitulo = new Font("Tiny5", Font.PLAIN, 30);

        //Panel para el título con FlowLayout centrado
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(new Color(64, 133, 78)); 
        JLabel titulo = new JLabel("REGISTRO TORNEO DE VIDEOJUEGOS");
        titulo.setFont(fuenteTitulo);
        panelTitulo.add(titulo);

        //Panel principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(new Color(64, 133, 78));
        GridBagConstraints gbc = new GridBagConstraints();

        //Nombre
        JLabel nombre = new JLabel("Nombre: ");
        nombreTf = new JTextField(20);  
        nombre.setFont(fuente);
        nombreTf.setFont(fuente);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 10, 10); 
        panelPrincipal.add(nombre, gbc);
        gbc.gridx = 1;
        panelPrincipal.add(nombreTf, gbc);

        //Contraseña
        JLabel pswd = new JLabel("Contraseña: ");
        pswdTf = new JPasswordField(20);  
        pswd.setFont(fuente);
        pswdTf.setFont(fuente);
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelPrincipal.add(pswd, gbc);
        gbc.gridx = 1;
        panelPrincipal.add(pswdTf, gbc);

        //Correo
        JLabel correo = new JLabel("Correo: ");
        correoTf = new JTextField(20);  
        correo.setFont(fuente);
        correoTf.setFont(fuente);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 20, 10);
        panelPrincipal.add(correo, gbc);
        gbc.gridx = 1;
        panelPrincipal.add(correoTf, gbc);
        
        //Selección imagenes
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;  
        gbc.insets = new Insets(10, 10, 5, 10);  
        JLabel textoImagenes = new JLabel("Escoge tu imagen de perfil: ");
        textoImagenes.setFont(fuente);
        panelPrincipal.add(textoImagenes, gbc);
        
        // Panel para las imágenes de juegos (icons)
        JPanel panelIconos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelIconos.setBackground(new Color(64, 133, 78)); 

        //Cargamos las imágenes
        String[] rutasIconos = {
        	"/perfil/animal_perfil.png",
        	"/perfil/demonio_perfil.png",
        	"/perfil/mujer_perfil.png",
        	"/perfil/rey_perfil.png",
        	"/perfil/niño_perfil.png"
        };

        for (String ruta : rutasIconos) {
            ImageIcon icono = new ImageIcon(getClass().getResource(ruta));
            Image imagenEscalada = icono.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JButton btnImagen = new JButton(new ImageIcon(imagenEscalada));
            btnImagen.setPreferredSize(new Dimension(60, 60));
            btnImagen.setBackground(new Color(64, 133, 78));
            btnImagen.setBorderPainted(false);

            //Acción para seleccionar imagen
            btnImagen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    imagenSeleccionada = ruta;  
                    
                    switch (ruta) {
					case "/perfil/animal_perfil.png":
						imagenSeleccionada = "animal_perfil";
						break;
						
					case "/perfil/demonio_perfil.png":
						imagenSeleccionada = "demonio_perfil";
						break;
					
					case "/perfil/mujer_perfil.png":
						imagenSeleccionada = "mujer_perfil";
						break;
					
					case "/perfil/rey_perfil.png":
						imagenSeleccionada = "rey_perfil";
						break;
						
					case "/perfil/niño_perfil.png":
						imagenSeleccionada = "niño_perfil";
					break;

					default:
						break;
					}
                }
            });
            
            panelIconos.add(btnImagen);
        }
        
        gbc.gridy = 5;  
        gbc.gridx = 0;
        gbc.gridwidth = 2; 
        gbc.insets = new Insets(5, 10, 10, 10);  
        panelPrincipal.add(panelIconos, gbc);

        //Botón de Registro
        JButton boton = new JButton("Registrar");
        boton.setFont(fuente);
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;  
        gbc.insets = new Insets(20, 10, 20, 10);
        panelPrincipal.add(boton, gbc);

        // Acción del botón para registrar usuario
        boton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = nombreTf.getText();
                String contraseña = new String(pswdTf.getPassword());
                String correo = correoTf.getText();

                // Validar que el nombre no exceda los 10 caracteres
                if (nombre.length() > 10) {
                    JOptionPane.showMessageDialog(null, "El nombre no puede exceder los 10 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validar que se haya seleccionado una imagen
                if (imagenSeleccionada == null) {
                    JOptionPane.showMessageDialog(null, "Por favor selecciona una imagen de perfil.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proceder con el registro si las validaciones pasan
                boolean exito = registrarUsuarioConRolJugador(nombre, contraseña, correo, imagenSeleccionada); 

                if (exito) {
                    JOptionPane.showMessageDialog(null, "Te has registrado con éxito. Bienvenido, " + nombre);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al registrar el usuario. Inténtalo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Usamos BorderLayout para agregar los paneles en las posiciones correctas
        setLayout(new BorderLayout());
        add(panelTitulo, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);

        //Hacemoa la ventana visible
        setVisible(true);
    }

    //Método para registrar usuario y rol en la base de datos
    public static boolean registrarUsuarioConRolJugador(String nombre, String contraseña, String correo, String imagen) {
        String insertarUsuario = "INSERT INTO usuarios(nombre, contraseña, correo, imagen) VALUES (?, ?, ?, ?)";
        String insertarRolJugador = "INSERT INTO roles(nombre) VALUES ('jugador') ON CONFLICT DO NOTHING";
        String obtenerRolJugador = "SELECT id FROM roles WHERE nombre = 'jugador'";
        String asignarRol = "INSERT INTO usuario_rol(nombreUsuario, rolId) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false); // Transacción

            // 1. Insertar usuario
            try (PreparedStatement psUsuario = conn.prepareStatement(insertarUsuario)) {
                psUsuario.setString(1, nombre);
                psUsuario.setString(2, contraseña);
                psUsuario.setString(3, correo);
                psUsuario.setString(4, imagen);
                psUsuario.executeUpdate();
            }

            // 2. Crear rol 'jugador' si no existe
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(insertarRolJugador);
            }

            // 3. Obtener ID del rol 'jugador'
            int rolId;
            try (PreparedStatement psRol = conn.prepareStatement(obtenerRolJugador)) {
                ResultSet rs = psRol.executeQuery();
                if (rs.next()) {
                    rolId = rs.getInt("id");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 4. Asignar el rol al usuario
            try (PreparedStatement psAsignar = conn.prepareStatement(asignarRol)) {
                psAsignar.setString(1, nombre);
                psAsignar.setInt(2, rolId);
                psAsignar.executeUpdate();
            }

            conn.commit(); // Confirmar todo
            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario con rol: " + e.getMessage());
            return false;
        }
    }
}
