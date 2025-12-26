package tfg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import tfg.juegos.juego1.Juego1;
import tfg.juegos.juego2.Juego2;
import tfg.juegos.juego3.Juego3;
import java.awt.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Pablo Marcos Sánchez
 */
public class Main extends JFrame {
    private static final long serialVersionUID = 1L;
    private Font customFont;

    public String label = "No conectado";
    private JLabel labelPerfil = new JLabel("No conectado");
    ImageIcon icono = new ImageIcon(getClass().getResource("/perfil/usuario.png"));
    Image imagenEscalada = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    JLabel profileImagen = new JLabel(new ImageIcon(imagenEscalada));
    private JButton botonJuego1, botonJuego2, botonJuego3, botonIcono;

    public Main() {
    	PantallaSplash splash = new PantallaSplash(2000);

    	//Hacemos que la pantalla de carga desaparezca tras 4 segundos.
    	new Thread(() -> {
    	    try {
    	        Thread.sleep(4000); 
    	    } catch (InterruptedException e) {
    	        e.printStackTrace();
    	    }

    	    SwingUtilities.invokeLater(splash::cerrar); 
    	}).start();
    	
        //Inicializamos la fuente
        customFont = new Font("Tiny5", Font.PLAIN, 20);

        //Configuración de ventana
        setTitle("TORNEO DE VIDEOJUEGOS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        
        //Cargamos el ícono
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(icono);

        //Panel principal
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBackground(new Color(64, 133, 78));
        add(mainContainer);

        //Panel superior
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(64, 133, 78));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 5, 5); 

        // Añadir el logo del grupo
        ImageIcon logoGrupoIcon = new ImageIcon(getClass().getResource("/logos/logoGrupo.png"));
        Image logoGrupoImage = logoGrupoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Ajusta el tamaño según necesites
        JButton logoGrupoBoton = new JButton(new ImageIcon(logoGrupoImage));
        logoGrupoBoton.setBorder(BorderFactory.createEmptyBorder());
        logoGrupoBoton.setContentAreaFilled(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Alinear a la izquierda
        topPanel.add(logoGrupoBoton, gbc);

        // Botones de inicio de sesión y registro
        JButton btnInicioSesion = new JButton("INICIAR SESIÓN");
        btnInicioSesion.setFont(customFont);
        JButton btnRegistro = new JButton("REGISTRARSE");
        btnRegistro.setFont(customFont);

        gbc.gridx = 1; // Ahora empieza en 1 porque el logo está en 0
        gbc.gridy = 0;
        topPanel.add(btnInicioSesion, gbc);
        gbc.gridx = 2;
        topPanel.add(btnRegistro, gbc);

        labelPerfil = new JLabel("No conectado");
        labelPerfil.setFont(customFont);
        gbc.gridx = 3;
        gbc.insets = new Insets(10, 30, 5, 5);
        topPanel.add(labelPerfil, gbc);

        profileImagen.setPreferredSize(new Dimension(75, 75));
        gbc.gridx = 4; // Incrementamos este valor también
        topPanel.add(profileImagen, gbc);

        mainContainer.add(topPanel, BorderLayout.NORTH);

        //Panel central con GridBagLayout
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(new Color(64, 133, 78));
        GridBagConstraints gbcCenter = new GridBagConstraints();
        gbcCenter.gridx = 0;
        gbcCenter.insets = new Insets(0, 0, 0, 0);

        //Panel para el Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(64, 133, 78));
        JLabel titleLabel = new JLabel("TORNEO DE VIDEOJUEGOS");
        titleLabel.setFont(new Font("Tiny5", Font.BOLD, 40));
        panelTitulo.add(titleLabel);

        gbcCenter.gridy = 0;
        panelCentral.add(panelTitulo, gbcCenter);

        //Juegos
        JPanel panelJuegos = new JPanel(new GridBagLayout());
        panelJuegos.setBackground(new Color(64, 133, 78));

        //Configuración para los juegos
        GridBagConstraints gbcJuegos = new GridBagConstraints();
        gbcJuegos.insets = new Insets(5, 10, 5, 10); 

        // Juego 1 - Laberinto
        botonJuego1 = new JButton("LABERINTO");
        botonJuego1.setFont(customFont);
        gbcJuegos.gridx = 0;
        gbcJuegos.gridy = 0;
        panelJuegos.add(botonJuego1, gbcJuegos);

        // Creamos panel para descripcion y comentarios JUEGO 1
        JPanel panelDescripcionJuego1 = new JPanel(new BorderLayout(5, 0));
        panelDescripcionJuego1.setBackground(new Color(64, 133, 78));

        JLabel labelJuego1 = new JLabel("<html>Escapa del laberinto, llega a la salida.&nbsp;&nbsp;&nbsp;</html>");
        labelJuego1.setFont(customFont);
        panelDescripcionJuego1.add(labelJuego1, BorderLayout.CENTER);

        ImageIcon iconoComentariosJuego1 = new ImageIcon(getClass().getResource("/comentarios/icono.png"));
        Image imgComentariosJuego1 = iconoComentariosJuego1.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton botonComentariosJuego1 = new JButton(new ImageIcon(imgComentariosJuego1));
        botonComentariosJuego1.setBorder(BorderFactory.createEmptyBorder());
        botonComentariosJuego1.setContentAreaFilled(false);
     
        panelDescripcionJuego1.add(botonComentariosJuego1, BorderLayout.EAST); 

        gbcJuegos.gridx = 1;
        panelJuegos.add(panelDescripcionJuego1, gbcJuegos);

        botonJuego2 = new JButton("GUERRERO");
        botonJuego2.setFont(customFont);
        gbcJuegos.gridx = 0;
        gbcJuegos.gridy = 1;
        panelJuegos.add(botonJuego2, gbcJuegos);

        // Creamos panel para descripcion y comentarios JUEGO 2
        JPanel panelDescripcionJuego2 = new JPanel(new BorderLayout(5, 1));
        panelDescripcionJuego2.setBackground(new Color(64, 133, 78));
        
        JLabel labelJuego2 = new JLabel("<html>Sobrevive el máximo tiempo posible.&nbsp;&nbsp;&nbsp;</html>");
        labelJuego2.setFont(customFont);
        panelDescripcionJuego2.add(labelJuego2, BorderLayout.CENTER);
        
        ImageIcon iconoComentariosJuego2 = new ImageIcon(getClass().getResource("/comentarios/icono.png"));
        Image imgComentariosJuego2 = iconoComentariosJuego2.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton botonComentariosJuego2 = new JButton(new ImageIcon(imgComentariosJuego2));
        botonComentariosJuego2.setBorder(BorderFactory.createEmptyBorder());
        botonComentariosJuego2.setContentAreaFilled(false);
     
        panelDescripcionJuego2.add(botonComentariosJuego2, BorderLayout.EAST); 
        
        gbcJuegos.gridx = 1;
        panelJuegos.add(panelDescripcionJuego2, gbcJuegos);

        botonJuego3 = new JButton("ASTRONAUTA");
        botonJuego3.setFont(customFont);
        gbcJuegos.gridx = 0;
        gbcJuegos.gridy = 2;
        panelJuegos.add(botonJuego3, gbcJuegos);
        
        // Creamos panel para descripcion y comentarios JUEGO 2
        JPanel panelDescripcionJuego3 = new JPanel(new BorderLayout(5, 2));
        panelDescripcionJuego3.setBackground(new Color(64, 133, 78));

        JLabel labelJuego3 = new JLabel("<html>Eres un astronauta. Vuela alto.&nbsp;&nbsp;&nbsp;</html>");
        labelJuego3.setFont(customFont);
        panelDescripcionJuego3.add(labelJuego3, BorderLayout.CENTER);
        
        ImageIcon iconoComentariosJuego3 = new ImageIcon(getClass().getResource("/comentarios/icono.png"));
        Image imgComentariosJuego3 = iconoComentariosJuego3.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        JButton botonComentariosJuego3 = new JButton(new ImageIcon(imgComentariosJuego3));
        botonComentariosJuego3.setBorder(BorderFactory.createEmptyBorder());
        botonComentariosJuego3.setContentAreaFilled(false);
     
        panelDescripcionJuego3.add(botonComentariosJuego3, BorderLayout.EAST); 
        
        gbcJuegos.gridx = 1;
        panelJuegos.add(panelDescripcionJuego3, gbcJuegos);
        
        //Deshabilitamos los botones inicialmente
        botonJuego1.setEnabled(false);
        botonJuego2.setEnabled(false);
        botonJuego3.setEnabled(false);

        gbcCenter.gridy = 1;
        gbcCenter.insets = new Insets(5, 0, 0, 0); 
        panelCentral.add(panelJuegos, gbcCenter);
        
        //Icono instrucciones
        ImageIcon iconoInstrucciones = new ImageIcon(getClass().getResource("/instrucciones/ajustes.png"));
        Image iconoEscalado = iconoInstrucciones.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        botonIcono = new JButton(new ImageIcon(iconoEscalado));
        botonIcono.setPreferredSize(new Dimension(80, 80));
        botonIcono.setContentAreaFilled(false);
        botonIcono.setBorder(BorderFactory.createEmptyBorder());

        JLabel iconoDesc = new JLabel("<html>Informacion de los videojuegos.</html>");
        iconoDesc.setFont(customFont);
 
        //Botón de instrucciones
        GridBagConstraints gbcInstrucciones = new GridBagConstraints();
        gbcInstrucciones.gridx = 0;
        gbcInstrucciones.gridy = 3;
        gbcInstrucciones.insets = new Insets(20, 10, 1, 10); 
        panelJuegos.add(botonIcono, gbcInstrucciones);

        GridBagConstraints gbcInstruccionesDesc = new GridBagConstraints();
        gbcInstruccionesDesc.gridx = 1;
        gbcInstruccionesDesc.gridy = 3;
        gbcInstruccionesDesc.insets = new Insets(20, 0, 10, 10);
        gbcInstruccionesDesc.anchor = GridBagConstraints.WEST;
        panelJuegos.add(iconoDesc, gbcInstruccionesDesc);

        mainContainer.add(panelCentral, BorderLayout.CENTER);

        //Panel ranking
        JPanel rankingContainer = new JPanel(new BorderLayout());
        rankingContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 50, 30));
        rankingContainer.setBackground(new Color(64, 133, 78));
        
        //Panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(customFont);
        tabbedPane.setBackground(new Color(64, 133, 78));
        tabbedPane.setForeground(Color.WHITE);

        // Pestañas: Ranking----------------------------------
        String[] juegos = {"LABERINTO", "GUERRERO", "ASTRONAUTA"};
        String[] etiquetas = {"J1", "J2", "J3"};

        for (int i = 0; i < juegos.length; i++) {
            String nombreJuego = juegos[i];
            String etiqueta = etiquetas[i];

            JPanel panelRanking = new JPanel(new BorderLayout());
            panelRanking.setBackground(Color.WHITE);

            String[] columnNames = {"Nº", "PJ", "PTS"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable table = new JTable(model);
            table.setFont(customFont);
            table.getTableHeader().setFont(customFont);
            table.setRowHeight(25); 

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(250, 350));

            JLabel labelRanking = new JLabel("RANKING - " + nombreJuego);
            labelRanking.setFont(customFont);
            labelRanking.setHorizontalAlignment(JLabel.CENTER);
            labelRanking.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JPanel labelPanel = new JPanel();
            labelPanel.setBackground(Color.WHITE);
            labelPanel.add(labelRanking);

            panelRanking.add(labelPanel, BorderLayout.NORTH);
            panelRanking.add(scrollPane, BorderLayout.CENTER);

            JButton botonActualizar = new JButton("Actualizar");
            botonActualizar.setFont(customFont);
            botonActualizar.addActionListener(e -> actualizarTablaJuego(model, nombreJuego));

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(botonActualizar);

            panelRanking.add(buttonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab(etiqueta, panelRanking);

            // Cargar datos inicialmente
            actualizarTablaJuego(model, nombreJuego);
        }

        rankingContainer.add(tabbedPane, BorderLayout.CENTER);
        mainContainer.add(rankingContainer, BorderLayout.EAST);


        JPanel rankingPanel = new JPanel();
        rankingPanel.setPreferredSize(new Dimension(250, 600));
        rankingPanel.setBackground(Color.WHITE);
        
        //Listeners
        btnInicioSesion.addActionListener(e -> new InicioSesión(this));
        btnRegistro.addActionListener(e -> new Registro(this));
        
        //Modificamos los listeners para verificar autenticación
        botonJuego1.addActionListener(e -> {
            if (labelPerfil.getText().equals("No conectado")) {
                JOptionPane.showMessageDialog(this, "Para poder jugar debes iniciar sesión");
            } else {
                lanzarJuego1(labelPerfil.getText());
            }
        });
        
        botonJuego2.addActionListener(e -> {
            if (labelPerfil.getText().equals("No conectado")) {
                JOptionPane.showMessageDialog(this, "Para poder jugar debes iniciar sesión");
            } else {
                lanzarJuego2(labelPerfil.getText());
            }
        });
        
        botonJuego3.addActionListener(e -> {
            if (labelPerfil.getText().equals("No conectado")) {
                JOptionPane.showMessageDialog(this, "Para poder jugar debes iniciar sesión");
            } else {
                lanzarJuego3(labelPerfil.getText());
            }
        });
        
        botonIcono.addActionListener(e -> {
        	Instrucciones instrucciones = new Instrucciones(Main.this); 
        	instrucciones.setVisible(true);
        });
        
        botonComentariosJuego1.addActionListener(e -> {
            if (labelPerfil.getText().equals("No conectado")) {
                JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ver los comentarios");
            } else {
                try {
                    String nombreUsuario = labelPerfil.getText();
                    
                    //Obtenemos nombre de la imagen del usaurio desde la base de datos
                    String nombreImagen = DBHelper.obtenerImagenUsuario(nombreUsuario);
                    String rutaImagen = "/perfil/" + (nombreImagen != null ? nombreImagen : "usuario") + ".png";

                    new ComentariosJuego(nombreUsuario, rutaImagen, 1, "LABERINTO");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar los comentarios: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        botonComentariosJuego2.addActionListener(e->{
        	if(labelPerfil.getText().equals("No conectado")) {
        		JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ver los comentarios");
        	}else {
        		try {
        			String nombreUsuario = labelPerfil.getText();
        			
                    //Obtenemos nombre de la imagen del usaurio desde la base de datos
                    String nombreImagen = DBHelper.obtenerImagenUsuario(nombreUsuario);
                    String rutaImagen = "/perfil/" + (nombreImagen != null ? nombreImagen : "usuario") + ".png";
                    
                    new ComentariosJuego(nombreUsuario, rutaImagen, 2, "GUERRERO");
					
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, "Error al cargar los comentarios: " + e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
        	}
        });
        
        botonComentariosJuego3.addActionListener(e->{
        	if(labelPerfil.getText().equals("No conectado")) {
        		JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ver los comentarios");
        	}else {
        		try {
        			String nombreUsuario = labelPerfil.getText();
        			
                    //Obtenemos nombre de la imagen del usaurio desde la base de datos
                    String nombreImagen = DBHelper.obtenerImagenUsuario(nombreUsuario);
                    String rutaImagen = "/perfil/" + (nombreImagen != null ? nombreImagen : "usuario") + ".png";
                    
                    new ComentariosJuego(nombreUsuario, rutaImagen, 3, "ASTRONAUTA");
					
				} catch (Exception e3) {
					JOptionPane.showMessageDialog(this, "Error al cargar los comentarios: " + e3.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
        	}
        });
        
        logoGrupoBoton.addActionListener(e -> {
        	if(labelPerfil.getText().equals("No conectado")) {
        		JOptionPane.showMessageDialog(this, "Debes iniciar sesión para ver a los usuarios");
        	}else {
        		try {
            		String nombreUsuario = labelPerfil.getText();
            		
            		new SwingWorker<Void, Void>() {
            		    @Override
            		    protected Void doInBackground() {
            		        new Usuarios(nombreUsuario);
            		        return null;
            		    }
            		}.execute();
				} catch (Exception eg) {
					JOptionPane.showMessageDialog(this, "Error al cargar los usuarios: " + eg.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
        	}
        });

        setVisible(true);
    }

    //Metodo para establecer el nombre del usuario
    public void setNombrePerfil(String name) {
        labelPerfil.setText(name);
        if (name.equals("No conectado")) {
            habilitarBotonesJuegos(false);
        }
    }
    //Metodo para establecer la imagen de perfil del usuario
    public void setImagenPerfil(String ruta) {
        ImageIcon icono = new ImageIcon(getClass().getResource("/perfil/" + ruta + ".png"));
        Image imagenEscalada = icono.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
        profileImagen.setIcon(new ImageIcon(imagenEscalada));
    }
    
    //Método para habilitar los botones después del login
    public void habilitarBotonesJuegos(boolean habilitar) {
        botonJuego1.setEnabled(habilitar);
        botonJuego2.setEnabled(habilitar);
        botonJuego3.setEnabled(habilitar);
    }
    
    //Metodo para actualizar las puntuaciones de los rankings
    private void actualizarTablaJuego(DefaultTableModel model, String nombreJuego) {
        model.setRowCount(0); 

        try {
            ResultSet rs = DBHelper.obtenerPuntuacionesPorJuego(nombreJuego);
            int posicion = 1;

            while (rs.next()) {
                String nombre = rs.getString("nombreUsuario");
                int puntuacion = rs.getInt("puntuacion");
                model.addRow(new Object[]{posicion++, nombre, puntuacion});
            }

            rs.getStatement().getConnection().close();
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar puntuaciones de " + nombreJuego + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Métodos para lanzar los juegos
    private void lanzarJuego1(String nombreUsuario) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Laberinto");
        config.setWindowedMode(800, 600);

        new Lwjgl3Application(new Juego1(nombreUsuario), config); 
    }

    private void lanzarJuego2(String nombreUsuario) {
    	Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Guerrero");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new Juego2(nombreUsuario), config); 
    }

    private void lanzarJuego3(String nombreUsuario) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Astronauta");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new Juego3(nombreUsuario), config); 
    }

    public static void main(String[] args) {
    	DBHelper.inicializarBaseDatos();
    	
        new Main(); 
    }
}
