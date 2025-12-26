package tfg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class Usuarios extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Color fondo = new Color(64, 133, 78);
    private final Font fuente = new Font("Tiny5", Font.PLAIN, 20);
    private final Font fuentetAcciones = new Font("Tiny5", Font.PLAIN, 15);
    private DefaultTableModel modelo;
    private JTable tablaUsuarios;
    private String usuarioActual;

    public Usuarios(String usuarioActual) {
        this.usuarioActual = usuarioActual;
        setTitle("USUARIOS REGISTRADOS");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(fondo);
        setLayout(new BorderLayout());
        setBackground(fondo);
        
        //Cargamos el ícono
        Image iconoVentana = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(iconoVentana);

        // Columnas en el orden que queremos mostrarlas
        String[] columnas = {"Imagen", "Nombre", "Rol", "Fecha Registro", "Acciones"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return Object.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; 
            }
        };
        
        tablaUsuarios = new JTable(modelo);
        tablaUsuarios.setFont(fuente);
        tablaUsuarios.getTableHeader().setFont(fuente);
        tablaUsuarios.setRowHeight(50);
        tablaUsuarios.setBackground(fondo);
        tablaUsuarios.setForeground(Color.WHITE);
        tablaUsuarios.getTableHeader().setBackground(fondo);
        tablaUsuarios.getTableHeader().setForeground(Color.WHITE);
        
        tablaUsuarios.getColumn("Acciones").setCellRenderer(new PanelRenderer());
        tablaUsuarios.getColumn("Acciones").setCellEditor(new PanelEditor());

        long inicio = System.currentTimeMillis();
        cargarUsuarios();
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo de carga usuarios: " + (fin - inicio) + " ms");

        // Ajustar el ancho de las columnas
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(150);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                Point point = e.getPoint();
                int row = tablaUsuarios.rowAtPoint(point);
                int column = tablaUsuarios.columnAtPoint(point);

                if (column == 4 && row != -1) {
                    tablaUsuarios.editCellAt(row, column);
                    Component comp = tablaUsuarios.getEditorComponent();
                    if (comp != null) comp.requestFocusInWindow();
                }
            }
        });


        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        add(scroll, BorderLayout.CENTER);
        scroll.getViewport().setBackground(fondo);   
        
        setVisible(true);
    }

    public void cargarUsuarios() {
        modelo.setRowCount(0); // Limpiar tabla
        
        boolean esAdmin = DBHelper.esAdmin(usuarioActual);
        boolean esMod = DBHelper.esModerador(usuarioActual);
        
        List<String[]> usuarios = DBHelper.obtenerUsuarios();
        for (String[] usuario : usuarios) {
            ImageIcon icono = DBHelper.cargarIconoUsuarioDesdeDB(usuario[2]);
            Image imagenEscalada = icono.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            
            // Crear botones de acción solo si el usuario actual es admin/moderador
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panelBotones.setBackground(fondo);
            
            if (esAdmin|| esMod) {
                JButton cambiarRolBtn = crearBotonCambiarRol(usuario[0]);
                
                // Solo admins pueden eliminar usuarios
                if (esAdmin) {
                    JButton eliminarBtn = crearBotonEliminar(usuario[0]);
                    panelBotones.add(eliminarBtn);
                }
                
                panelBotones.add(cambiarRolBtn);
                
            }else {
            	JLabel txtAcciones = new JLabel("No tienes acceso a las acciones");
            	txtAcciones.setFont(fuentetAcciones);
            	txtAcciones.setForeground(Color.WHITE);
            	panelBotones.add(txtAcciones);
            }
            
            Object[] fila = {
                new ImageIcon(imagenEscalada),
                usuario[0],
                usuario[4],
                usuario[3],
                panelBotones
            };
            modelo.addRow(fila);
        }
    }

    private JButton crearBotonCambiarRol(String nombreUsuario) {
        JButton boton = new JButton("Cambiar Rol");
        boton.addActionListener(e -> cambiarRolUsuario(nombreUsuario));
        return boton;
    }

    private JButton crearBotonEliminar(String nombreUsuario) {
        JButton boton = new JButton("Eliminar");
        boton.addActionListener(e -> eliminarUsuario(nombreUsuario));
        return boton;
    }

    private void cambiarRolUsuario(String nombreUsuario) {
        String[] rolesDisponibles = {"jugador", "moderador", "admin"};
        String rolActual = DBHelper.obtenerRolUsuario(nombreUsuario);

        String nuevoRol = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el nuevo rol para el usuario: " + nombreUsuario,
            "Cambiar rol de usuario",
            JOptionPane.QUESTION_MESSAGE,
            null,
            rolesDisponibles,
            rolActual
        );

        if (nuevoRol != null && !nuevoRol.equals(rolActual)) {
            if (DBHelper.actualizarRolUsuario(nombreUsuario, nuevoRol)) {
                JOptionPane.showMessageDialog(this, "El rol ha sido actualizado correctamente.");
                cargarUsuarios(); // Refrescar la tabla
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al actualizar el rol.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarUsuario(String nombreUsuario) {
        Object[] opciones = {"Sí", "No"};
        int confirm = JOptionPane.showOptionDialog(
            this,
            "¿Está seguro que desea eliminar al usuario " + nombreUsuario + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[1] // Opción por defecto: "No"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (DBHelper.eliminarUsuario(nombreUsuario)) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente");
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

//Clases internas para la funcionalidad de las acciones
class PanelEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panelActual;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected, int row, int column) {
        panelActual = (JPanel) value;
        return panelActual;
    }

    @Override
    public Object getCellEditorValue() {
        return panelActual;
    }
}



class PanelRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return (JPanel) value;
    }
}
