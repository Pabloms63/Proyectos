package tfg;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author Pablo Marcos Sánchez
 */
public class ComentariosJuego extends JFrame {
    private static final long serialVersionUID = 1L;
    private int juegoId = 1;
    private JTextField nombreField;
    private JTextArea comentarioInputArea;
    private JPanel commentsPanel;
    private Font customFont;
    private final Color fondo = new Color(64, 133, 78);

    public ComentariosJuego(String nombreUsuario, String rutaImagenUsuario, int juegoId, String nombreJuego) {
        this.juegoId = juegoId;
        
        //Configuramos los botones del dialogo de eliminar en comentario en español
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.yesButtonText", "Sí");
            UIManager.put("OptionPane.noButtonText", "No");
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        setTitle("Foro" + nombreJuego.toUpperCase());
        setSize(850, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(fondo);
        
        //Cargamos el ícono
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(icono);

        customFont = new Font("Tiny5", Font.PLAIN, 20);
        String nombreFinal = (nombreUsuario == null || nombreUsuario.trim().isEmpty()) ? "Invitado" : nombreUsuario;

        // Panel superior de comentarios previos
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(fondo);

        // Panel contenedor para alinear arriba
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(fondo);
        wrapperPanel.add(commentsPanel, BorderLayout.NORTH);

        JScrollPane scrollComentarios = new JScrollPane(wrapperPanel);
        scrollComentarios.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollComentarios.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollComentarios.getViewport().setBackground(fondo);

        TitledBorder tituloComentarios = BorderFactory.createTitledBorder("FORO - " + nombreJuego.toUpperCase());
        tituloComentarios.setTitleFont(customFont);
        scrollComentarios.setBorder(tituloComentarios);
        scrollComentarios.setBackground(fondo);

        // Panel de publicación
        JPanel panelPublicar = new JPanel(new BorderLayout(5, 5));
        panelPublicar.setBackground(fondo);

        comentarioInputArea = new JTextArea(3, 40);
        comentarioInputArea.setFont(customFont);
        comentarioInputArea.setLineWrap(true);
        comentarioInputArea.setWrapStyleWord(true);
        JScrollPane scrollComentarioInput = new JScrollPane(comentarioInputArea);
        scrollComentarioInput.getViewport().setBackground(fondo);

        // Panel para nombre e imagen del usuario actual
        JPanel panelNombreConImagen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNombreConImagen.setBackground(fondo);
        ImageIcon userIcon = cargarIconoUsuario(rutaImagenUsuario);
        Image imgEsc = userIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel labelImagen = new JLabel(new ImageIcon(imgEsc));
        nombreField = new JTextField(nombreFinal);
        nombreField.setFont(customFont);
        nombreField.setEditable(false);
        nombreField.setColumns(20);
        panelNombreConImagen.add(labelImagen);
        panelNombreConImagen.add(nombreField);
        
        // Cargar comentarios
        cargarComentarios();

        JButton botonPublicar = new JButton("Publicar");
        botonPublicar.setFont(customFont);
        botonPublicar.setBackground(fondo);
        botonPublicar.addActionListener(e -> publicarComentario());

        panelPublicar.add(panelNombreConImagen, BorderLayout.NORTH);
        panelPublicar.add(scrollComentarioInput, BorderLayout.CENTER);
        panelPublicar.add(botonPublicar, BorderLayout.SOUTH);

        setLayout(new BorderLayout(10, 10));
        add(scrollComentarios, BorderLayout.CENTER);
        add(panelPublicar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private ImageIcon cargarIconoUsuario(String ruta) {
        ImageIcon icono;
        if (ruta == null || ruta.trim().isEmpty()) {
            icono = new ImageIcon(getClass().getResource("/perfil/usuario.png"));
        } else {
            URL url = getClass().getResource(ruta);
            if (url != null) {
                icono = new ImageIcon(url);
            } else {
                java.io.File file = new java.io.File(ruta);
                icono = file.exists() ? new ImageIcon(ruta)
                        : new ImageIcon(getClass().getResource("/perfil/usuario.png"));
            }
        }
        return icono;
    }

    private void cargarComentarios() {
        commentsPanel.removeAll();
        List<Comentario> comentarios = DBHelper.obtenerComentariosPorJuego(juegoId);

        if (comentarios.isEmpty()) {
            JLabel noComments = new JLabel("No hay comentarios previos.");
            noComments.setFont(customFont);
            noComments.setForeground(Color.WHITE);
            noComments.setAlignmentX(Component.LEFT_ALIGNMENT);
            commentsPanel.add(noComments);
        } else {
            for (Comentario c : comentarios) {
                JPanel single = new JPanel(new BorderLayout(5, 5));
                single.setBackground(fondo);
                single.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Icono
                Image img = c.getIcono().getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel iconLabel = new JLabel(new ImageIcon(img));
                single.add(iconLabel, BorderLayout.WEST);

                JPanel contenidoComentario = new JPanel(new BorderLayout());
                contenidoComentario.setBackground(fondo);
                contenidoComentario.setOpaque(false);

                // JTextPane con nombre, fecha y comentario
                JTextPane textPane = new JTextPane();
                textPane.setEditable(false);
                textPane.setBackground(fondo);
                textPane.setOpaque(false);

                StyleContext sc = new StyleContext();
                StyledDocument doc = textPane.getStyledDocument();

                Style estiloNegro = sc.addStyle("EstiloNegro", null);
                StyleConstants.setForeground(estiloNegro, Color.BLACK);
                StyleConstants.setBold(estiloNegro, true);
                StyleConstants.setFontSize(estiloNegro, customFont.getSize());
                StyleConstants.setFontFamily(estiloNegro, customFont.getFamily());

                Style estiloBlanco = sc.addStyle("EstiloBlanco", null);
                StyleConstants.setForeground(estiloBlanco, Color.WHITE);
                StyleConstants.setFontSize(estiloBlanco, customFont.getSize());
                StyleConstants.setFontFamily(estiloBlanco, customFont.getFamily());
                
                try {
                    doc.insertString(doc.getLength(), String.format("%s (%s):\n", c.getNombre(), c.getFecha()), estiloNegro);
                    doc.insertString(doc.getLength(), c.getTexto(), estiloBlanco);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                contenidoComentario.add(textPane, BorderLayout.CENTER);

                // Si el usuario es admin, mostrar botón
                if (DBHelper.esAdmin(nombreField.getText().trim()) || DBHelper.esModerador(nombreField.getText().trim())) {
                    JButton btnEliminar = new JButton("Eliminar");
                    btnEliminar.setFont(customFont);
                    btnEliminar.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este comentario?",
                                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean eliminado = DBHelper.eliminarComentario(c.getNombre(), juegoId, c.getTexto(), c.getFecha());
                            if (eliminado) {
                                cargarComentarios();
                            } else {
                                JOptionPane.showMessageDialog(this, "No se pudo eliminar el comentario.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    contenidoComentario.add(btnEliminar, BorderLayout.EAST);
                }

                single.add(contenidoComentario, BorderLayout.CENTER);
                commentsPanel.add(single);
            }
        }

        commentsPanel.revalidate();
        commentsPanel.repaint();
    }

    private void guardarComentario(String comentario) {
        boolean exito = DBHelper.insertarComentario(nombreField.getText().trim(), juegoId, comentario);
        if (!exito) {
            JOptionPane.showMessageDialog(this, "Error al guardar el comentario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void publicarComentario() {
        String comentario = comentarioInputArea.getText().trim();
        if (comentario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo del comentario debe contener algo.",
                    "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        guardarComentario(comentario);
        cargarComentarios();
        comentarioInputArea.setText("");
    }
  
}