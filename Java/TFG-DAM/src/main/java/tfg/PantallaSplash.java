package tfg;

import javax.swing.*;
import java.awt.*;

public class PantallaSplash extends JWindow {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PantallaSplash(int duracion) {
        // Configuración básica del splash
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(64, 133, 78)); // Mismo color que tu main
        
        // Imagen del splash
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/logos/LogoTFG.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        content.add(new JLabel(new ImageIcon(scaledImage)), BorderLayout.CENTER);
        
        // Texto de carga
        JLabel text = new JLabel("Cargando aplicación...", JLabel.CENTER);
        text.setFont(new Font("Tiny5", Font.PLAIN, 16));
        text.setForeground(Color.WHITE);
        content.add(text, BorderLayout.SOUTH);
        
        setContentPane(content);
        setSize(500, 350);
        setLocationRelativeTo(null); // Centrar
        setVisible(true);
    }
    
    public void cerrar() {
        dispose();
    }
}