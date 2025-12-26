package tfg;

import java.awt.*;
import javax.swing.*;

/**
 * @author Pablo Marcos Sánchez
 */
public class Instrucciones extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND_COLOR = new Color(64, 133, 78);
    private static final int IMAGE_WIDTH = 700;
    private static final int IMAGE_HEIGHT = 600;

    private final Font customFont;
    private final Main main;

    public Instrucciones(Main main) {
        this.main = main;
        this.customFont = new Font("Tiny5", Font.PLAIN, 20);

        configurarUI();
        inicializarVentana();

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);
        
        //Cargamos el ícono
        Image iconoVentana = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/iconos/LogoTFG.png"));
        setIconImage(iconoVentana);

        JTabbedPane tabbedPane = crearTabbedPane();

        tabbedPane.addTab("LABERINTO", crearPestana(
            "CONTROLES DE 'LABERINTO':",
            "<html>W: Andar hacia arriba<br>A: Andar hacia la izquierda<br>D: Andar hacia la derecha<br>S: Andar hacia abajo</div></html>",
            "/instrucciones/Laberinto/laberintoInstrucciones.png"
        ));

        tabbedPane.addTab("GUERRERO", crearPestana(
            "CONTROLES DE 'GUERRERO':",
            "<html>ESPACIO: Saltar.<br>A: Andar hacia la izquierda.<br>D: Andar hacia la derecha.<br>Click izquierdo: Atacar.</div></html>",
            "/instrucciones/Guerrero/guerreroInstrucciones.png"
        ));

        tabbedPane.addTab("ASTRONAUTA", crearPestana(
            "CONTROLES DE 'ASTRONAUTA':",
            "<html>A: Ir hacia la izquierda.<br>D: Ir hacia la derecha.</div></html>",
            "/instrucciones/Astronauta/astronautaInstrucciones.png"
        ));

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);
    }

    // Configuración básica de la ventana
    private void inicializarVentana() {
        setTitle("Instrucciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 900);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Estilo del UIManager para pestañas
    private void configurarUI() {
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(15, 10, 15, 10));
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 20, 10, 20));
        UIManager.put("TabbedPane.font", customFont);
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.selectedForeground", Color.BLACK);
        UIManager.put("TabbedPane.background", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.selected", new Color(100, 180, 110));
    }

    // Crea el JTabbedPane
    private JTabbedPane crearTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        return tabbedPane;
    }

    // Crea una pestaña de instrucciones con scroll
    private JScrollPane crearPestana(String titulo, String texto, String rutaImagen) {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(customFont);
        tituloLabel.setForeground(Color.BLACK);
        tituloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textoLabel = new JLabel(texto);
        textoLabel.setFont(customFont);
        textoLabel.setForeground(Color.BLACK);
        textoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textoLabel.setMaximumSize(new Dimension(800, Integer.MAX_VALUE)); // permite que se ajuste a ancho máximo
        textoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel mapaLabel = new JLabel("MAPA INDICATIVO");
        mapaLabel.setFont(customFont);
        mapaLabel.setForeground(Color.BLACK);
        mapaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);   

        JLabel imagenLabel = new JLabel(cargarImagen(rutaImagen));
        imagenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel creditosAutor = new JLabel("<html>Modelos de los personajes por Sven Thole.<br>sventhole.artstation.com  / sven_thole@hotmail.com</html>");
        creditosAutor.setFont(customFont);
        creditosAutor.setForeground(Color.BLACK);
        creditosAutor.setAlignmentX(Component.CENTER_ALIGNMENT);  

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(tituloLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(textoLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); 
        panel.add(mapaLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); 
        panel.add(imagenLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); // Espacio inferior
        panel.add(creditosAutor);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    // Cargar y escalar imagen
    private ImageIcon cargarImagen(String ruta) {
        ImageIcon icon = new ImageIcon(getClass().getResource(ruta));
        Image img = icon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
