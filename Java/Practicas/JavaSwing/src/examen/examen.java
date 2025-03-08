package examen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class examen extends JFrame {

	public static void main(String[] args) {
		examen e = new examen();
	}

	//Declaración variables
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	
	JDialog d1;
	JMenuItem mi1;
	JMenu menu1;
	JMenuBar mb;
	
	String fich;
	
	JLabel jlnombre = new JLabel("Nombre");
	JLabel jltamaño = new JLabel("Tamaño");
	JLabel jlruta = new JLabel("Ruta");
	JLabel jlfechaCreacion = new JLabel("Fecha Creación");
	
	JTextField jtnombre = new JTextField();
	JTextField jttamaño = new JTextField();
	JTextField jtruta = new JTextField();
	JTextField jtfechaCreacion = new JTextField();
	
    JTree fileTree;

	public examen() {
		setTitle("Examen 02 01");
		
		//Creación de menu y evento
	    mi1 = new JMenuItem("Seleccionar Carpeta");
	    mi1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
	    mi1.addActionListener(e -> seleccionFicheros());
	    
	    menu1 = new JMenu("Archivo");
	    menu1.add(mi1);
	    
	    mb = new JMenuBar();
	    mb.add(menu1);
	    setJMenuBar(mb);

        // Crea el nodo raíz del árbol
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("C:\\Program Files");
        
        System.out.println("");
        
        // Construye el árbol de archivos
        crearFileTree(root, new File("C:\\Program Files"));

        // Crea el modelo del árbol
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        // Crea el JTree con el modelo
        fileTree = new JTree(treeModel);
        
        // Configura el JScrollPane con el JTree
        JScrollPane scrollPane = new JScrollPane(fileTree);
        
        
		// PANEL 1
        panel1.setLayout(new BorderLayout());
		panel1.add(scrollPane, BorderLayout.CENTER);

		// PANEL2
		panel2.setLayout(new GridLayout(8,1));
		panel2.add(jlnombre);
		panel2.add(jtnombre);
		panel2.add(jltamaño);
		panel2.add(jttamaño);
		panel2.add(jlruta);
		panel2.add(jtruta);
		panel2.add(jlfechaCreacion);
		panel2.add(jtfechaCreacion);

		//Creación splitpane
		JSplitPane panelPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		panelPrincipal.add(panel1);
		panelPrincipal.add(panel2);
		panelPrincipal.setOneTouchExpandable(true); //Con un click en las flechas, se expande a un lado o a otro.
		add(panelPrincipal);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

    private void crearFileTree(DefaultMutableTreeNode parentNode, File parentFile) {
        File[] files = parentFile.listFiles();

        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                parentNode.add(node);

                if (file.isDirectory()) {
                	crearFileTree(node, file); // Llamada recursiva para directorios
                }
            }
        }
    }
    
	public void seleccionFicheros() {
		FileDialog dialogo = new FileDialog(this, "Dialogo");
		dialogo.setPreferredSize(new Dimension(200, 400));
		dialogo.setVisible(true);
		
	    JFileChooser chooser = new JFileChooser();
	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    fich = dialogo.getDirectory();
	    
        // Crea el nodo raíz del árbol
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(fich);
        
        // Construye el árbol de archivos
        crearFileTree(root, new File(fich));

        // Crea el modelo del árbol
        DefaultTreeModel treeModel = new DefaultTreeModel(root);

        // Crea el JTree con el modelo
        fileTree = new JTree(treeModel);
        
        // Configura el JScrollPane con el JTree
        JScrollPane scrollPane = new JScrollPane(fileTree);
	    
	}

}






