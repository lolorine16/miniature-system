package restaurant.app.view.components;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Champ de texte moderne avec placeholder et style personnalisé.
 * Utilise la police Montserrat.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ModernTextField extends JTextField {
    
    private String placeholder;
    private Color placeholderColor = new Color(156, 163, 175);
    private Color borderColorDefault = new Color(209, 213, 219);
    private Color borderColorFocused = new Color(59, 130, 246);
    private boolean focused = false;
    
    /**
     * Constructeur par défaut.
     */
    public ModernTextField() {
        this("", "");
    }
    
    /**
     * Constructeur avec placeholder.
     * @param placeholder Le placeholder
     */
    public ModernTextField(String placeholder) {
        this("", placeholder);
    }
    
    /**
     * Constructeur avec texte et placeholder.
     * @param text Le texte initial
     * @param placeholder Le placeholder
     */
    public ModernTextField(String text, String placeholder) {
        super(text);
        this.placeholder = placeholder;
        initStyle();
        initListeners();
    }
    
    /**
     * Initialise le style du champ.
     */
    private void initStyle() {
        setFont(new Font("Montserrat", Font.PLAIN, 14));
        setForeground(new Color(31, 41, 55));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColorDefault, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        setCaretColor(new Color(59, 130, 246));
        setPreferredSize(new Dimension(250, 45));
    }
    
    /**
     * Initialise les écouteurs d'événements.
     */
    private void initListeners() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                updateBorder();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                updateBorder();
            }
        });
    }
    
    /**
     * Met à jour la bordure selon l'état de focus.
     */
    private void updateBorder() {
        Color borderColor = focused ? borderColorFocused : borderColorDefault;
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, focused ? 2 : 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Dessiner le placeholder si le champ est vide
        if (getText().isEmpty() && !focused && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(placeholderColor);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            
            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            
            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }
    
    /**
     * Définit le placeholder.
     * @param placeholder Le placeholder
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    /**
     * Retourne le placeholder.
     * @return Le placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Définit la couleur du placeholder.
     * @param color La couleur
     */
    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        repaint();
    }
    
    /**
     * Indique si le champ est en erreur.
     * @param error true pour afficher un état d'erreur
     */
    public void setError(boolean error) {
        if (error) {
            borderColorDefault = new Color(239, 68, 68);
            borderColorFocused = new Color(220, 38, 38);
        } else {
            borderColorDefault = new Color(209, 213, 219);
            borderColorFocused = new Color(59, 130, 246);
        }
        updateBorder();
    }
}
